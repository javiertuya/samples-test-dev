package giis.demo.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

/**
 * Metodos de utilidad para simplificar las queries realizadas en las clases 
 * que implementan la logica de negocio:
 * Se implementa como una clase abstracta para que la clase derivada implemente los detalles
 * relativos a la conexion y a la estructura de la base de datos a crear, y a la vez
 * pueda usar los metodos que se definen aqui.
 * 
 * <br>La mayoria de los metodos de utilidad utilizan apache commons-dbutils que gestiona todo el manejo
 * de resultsets, su mapeo a objetos y las excepciones permitiendo un codigo mucho mas limpio 
 * en las clases de la capa de negocio y DAOs.
 */
public abstract class DbUtil {
	/** Obtencion de la url de conexion que debe implementarse en la subclase */
	public abstract String getUrl();
	
	/** Obtiene un objeto conexion para esta base de datos */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getUrl());
	}

	//Documentacion de apache dbutils:
	//https://commons.apache.org/proper/commons-dbutils/apidocs/index.html
	//https://commons.apache.org/proper/commons-dbutils/examples.html
	
	/**
	 * Ejecuta una query sql con los parametros especificados mapeando el resultet en una lista de objetos 
	 * de la clase indicada en pojoClass;
	 * Utiliza apache commons-dbutils para realizar el mapeo y el manejo del resto de aspectos de jdbc
	 */
	public <T> List<T> executeQueryPojo(Class<T> pojoClass, String sql, Object... params) {
		Connection conn=null;
		try {
			conn=this.getConnection();
			BeanListHandler<T> beanListHandler=new BeanListHandler<>(pojoClass);
			QueryRunner runner=new QueryRunner();
			return runner.query(conn, sql, beanListHandler, params);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	/**
	 * Ejecuta una query sql con los parametros especificados mapeando el resultet en una lista de arrays de objetos;
	 * Utiliza apache commons-dbutils para relizar el mapeo y el manejo del resto de aspectos de jdbc
	 */
	public List<Object[]> executeQueryArray(String sql, Object... params) {
		Connection conn=null;
		try {
			conn=this.getConnection();
			//Como no hay una clase especificada para realizar el mapeo, utiliza el ArrayListHandler
			ArrayListHandler beanListHandler=new ArrayListHandler();
			QueryRunner runner=new QueryRunner();
			return runner.query(conn, sql, beanListHandler, params);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	public List<Map<String,Object>> executeQueryMap(String sql, Object... params) {
		Connection conn=null;
		try {
			conn=this.getConnection();
			MapListHandler beanListHandler=new MapListHandler();
			QueryRunner runner=new QueryRunner();
			return runner.query(conn, sql, beanListHandler, params);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	/**
	 * Ejecuta una sentencia sql de actualizacion con los parametros especificados;
	 * Utiliza apache commons-dbutils para manejar todos los aspectos de jdbc
	 */
	public void executeUpdate(String sql, Object... params) {
		Connection conn=null;
		try {
			conn=this.getConnection();
			QueryRunner runner=new QueryRunner();
			runner.update(conn, sql, params);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	/**
	 * Metodo simple para ejecutar todas las sentencias sql que se encuentran en un archivo, teniendo en cuenta:
	 * <br/>- Cada sentencia DEBE finalizar en ; pudiendo ocupar varias lineas
	 * <br/>- Se permiten comentarios de linea (--)
	 * <br/>- Todas las sentencias drop se ejecutan al principio, 
	 * y se ignoran los fallos en caso de que no exista la tabla (solo para drop)
	 */
	public void executeScript(String fileName) {
		List<String> lines;
		try {
			lines=Files.readAllLines(Paths.get(fileName));
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		//separa las sentencias sql en dos listas, una para drop y otra para el resto pues se ejecutaran de forma diferente
		List<String> batchUpdate=new ArrayList<>();
		List<String> batchDrop=new ArrayList<>();
		StringBuilder previousLines=new StringBuilder(); //guarda lineas anteriores al separador (;)
		for (String line : lines) {
			line=line.trim();
			if (line.length()==0 || line.startsWith("--")) //ignora lineas vacias comentarios de linea
				continue;
			if (line.endsWith(";")) {
				String sql=previousLines.toString()+line;
				//separa drop del resto
				if (line.toLowerCase().startsWith("drop"))
					batchDrop.add(sql);
				else
					batchUpdate.add(sql);
				//nueva linea
				previousLines=new StringBuilder();
			} else {
				previousLines.append(line+" ");
			}
		}
		//Ejecuta todas las sentencias, primero los drop (si existen)
		if (!batchDrop.isEmpty())
			this.executeBatchNoFail(batchDrop);
		if (!batchUpdate.isEmpty())
			this.executeBatch(batchUpdate);
	}
	/**
	 * Ejecuta un conjunto de sentencias sql de actualizacion en un unico batch
	 */
	public void executeBatch(String[] sqls) {
		executeBatch(Arrays.asList(sqls));
	}
	/**
	 * Ejecuta un conjunto de sentencias sql de actualizacion en un unico batch
	 */
	public void executeBatch(List<String> sqls) {
		try (Connection cn=DriverManager.getConnection(getUrl());
			Statement stmt = cn.createStatement()) {
				for (String sql : sqls)
					stmt.addBatch(sql);
				stmt.executeBatch();
		} catch (SQLException e) {
			//Ojo, no dejar pasar las excepciones (no limitarse a dejar el codigo autoegenerado por Eclipse con printStackTrace)
			throw new UnexpectedException(e);
		}
	}
	/**
	 * Ejecuta un conjunto de sentencias sql de actualizacion en un unico batch, sin causar excepcion cuando falla la ejecucion
	 * (usado normalmente para borrar tablas de la bd, que fallarian si no existen)
	 */
	public void executeBatchNoFail(List<String> sqls) {
		try (Connection cn=DriverManager.getConnection(getUrl());
			Statement stmt = cn.createStatement()) {
				for (String sql : sqls)
					executeWithoutException(stmt,sql);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	private void executeWithoutException(Statement stmt, String sql) {
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			//no causa excepcion intencionaamente
		}		
	}

}
