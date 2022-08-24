package giis.demo.jdbc;
import java.sql.*;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giis.demo.util.UnexpectedException;

/**
 * Ejemplos de acceso a una base de datos con conexion JDBC y base de datos Sqlite
 */
public class DemoJdbc {
	//informacion de conexion a la base de datos utilizada
	public static final String DRIVER="org.sqlite.JDBC";
	public static final String URL="jdbc:sqlite:DemoDB.db";
	//datos para SQLServer:
	//com.microsoft.sqlserver.jdbc.SQLServerDriver
	//jdbc:sqlserver://localhost:1433;DatabaseName=******;user=******;password=******
	private static final Logger log=LoggerFactory.getLogger(DemoJdbc.class);
	
	/**
	 * Demo basico de acceso a bases de datos, parte 1: conexiones, consultas y manejo basico de excepciones
	 */
	public void demo1Basic() {
		try {
			//instalacion de la clase que contiene al driver (basta con hacerlo una vez)
			//el driver (en este caso sqljdbc.jar ha sido descargado y puesto en el classpath)
			//Esto normalmente no es necesario pues a partir de JDBC 4.0 los drivers se autoregistran
			Class.forName(DRIVER);
			//Definicion de la cadena de conexion, especifica para cada gestor de base de datos
			String connString=URL;

			//Ejecuta acciones de actualizacion: insertar datos en una tabla
			Connection cn=DriverManager.getConnection(connString); //NOSONAR
			Statement stmt = cn.createStatement(); //NOSONAR
			try {
				stmt.executeUpdate("drop table if exists test");
			} catch (SQLException e) {
				//ignora excepcion, que se causara si la tabla no existe en la bd (p.e. al ejecutar la primera vez)
			}
			stmt.executeUpdate("create table test(id int not null, id2 int, text varchar(32))");
			stmt.executeUpdate("insert into test(id,id2,text) values(1,null,'abc')");
			stmt.executeUpdate("insert into test(id,id2,text) values(2,9999,'xyz')");
			stmt.close(); //no olvidar cerrar estos objetos
			cn.close();

			//Consulta todas las filas a partir del resultado de una query SQL
			cn=DriverManager.getConnection(connString); //NOSONAR
			stmt=cn.createStatement(); //NOSONAR
			ResultSet rs=stmt.executeQuery("select id,id2,text from test order by id desc"); //NOSONAR
			while (rs.next()) { //cada vez que se llama rs.next() avanza el cursor a una fila
				int id=rs.getInt(1);        //obtencion de un valor con un tipo de dato especificado, indicando el numero de columna
				String id2=rs.getString(2); //obtencion de un valor como string, aunque sea entero
				if (rs.wasNull())           //comprobacion de valores nulos (respecto del ultimo get realizado)
					id2="NULO";			    //  si es nulo puedo hacer un tratamiento especial, en este caso poner un valor
				String text=rs.getString("text"); //obtencion de un valor indicando el nombre de la columna
				log.info("demo1Basic: "+id+" "+id2+" "+text);
			}
			rs.close();
			stmt.close();
			cn.close();
			//varios de los metodos anteriores causan excepciones que se capturan aqui, normalmente habra que tratar 
			//cada excepcion en cada caso
		} catch (ClassNotFoundException | SQLException e) { 
			//Ojo, no dejar pasar las excepciones (no limitarse a dejar el codigo autoegenerado por Eclipse haciendo solo printStackTrace)
			throw new UnexpectedException(e); //Es mas habitual usar excepciones propias de la aplicacion (DemoException que RuntimeException)
		}
	}

	/**
	 * Creacion de tabla para los ejemplos a partir del segundo
	 */
	private void createTable() {
		//el try with resources nos va a asegurar que los objetos cn y stmt siempre se cierran haya o no excepcion
		try (Connection cn=DriverManager.getConnection(URL)) { //NOSONAR
			try (Statement stmt = cn.createStatement()) {
				stmt.executeUpdate("drop table if exists test");
				stmt.executeUpdate("create table test(id int not null, id2 int, text varchar(32))");
				stmt.executeUpdate("insert into test(id,id2,text) values(1,null,'abc')");
				stmt.executeUpdate("insert into test(id,id2,text) values(2,9999,'xyz')");
			}
		} catch (SQLException e) {
			//Ojo, no dejar pasar las excepciones (no limitarse a dejar el codigo autoegenerado por Eclipse haciendo solo printStackTrace)
			throw new UnexpectedException(e); //Es mas habitual usar excepciones propias de la aplicacion
		}
	}
	/**
	 * Demo basico de acceso a bases de datos, parte 2:
	 * Uso de he try-with-resources Statement para manejar excepciones y cerrar de forma segura los recursos
	 * (Las mismas acciones que el anterior pero con mejor control de excepciones) y consultas con parametros
	 */
	public void demo2TryWithResources() {
		createTable();
		//En un mismo try se pueden poner diferentes sentencias que crean objetos que gestionan recursos que hay que cerrar
		try (Connection cn=DriverManager.getConnection(URL); //NOSONAR
				Statement stmt=cn.createStatement();
				ResultSet rs=stmt.executeQuery("select id,id2,text from test order by id desc")) {
			while (rs.next()) { //cada vez que se llama rs.next() avanza el cursor a una fila
				int id=rs.getInt(1);        //obtencion de un valor con un tipo de dato especificado, indicando el numero de columna
				String id2=rs.getString(2); //obtencion de un valor como string, aunque sea entero
				if (rs.wasNull())           //comprobacion de valores nulos (respecto del ultimo get realizado)
					id2="NULO";			    //  si es nulo puedo hacer un tratamiento especial, en este caso poner un valor
				String text=rs.getString("text"); //obtencion de un valor indicando el nombre de la columna
				log.info("demo2TryWithResources: "+id+" "+id2+" "+text);
			}
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	/**
	 * Demo de acceso a bases de datos, parte 3: Consultas con parametros.
	 * Permite definir consultas sql donde alguno de los valores no son conocidos de moemnto (?).
	 * Estos valores se instancian en el momento de la ejecucion.
	 */
	public void demo3Parameters() {
		createTable();
		//En vez de crear un Statement y pasar el sql en executeQuery,
		//se crea un PreparedStatement con el sql, luego se le ponen los parametros y finalmente se ejecuta
		try (Connection cn=DriverManager.getConnection(URL); //NOSONAR
			PreparedStatement pstmt=cn.prepareStatement("select id,id2,text from test where id>=?")) {
			pstmt.setInt(1, 2); // pone valor 2 en el primer (y unico) parametro
			try (ResultSet rs=pstmt.executeQuery()) {
				while (rs.next()) {
					log.info("demo3Parameters: "+rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getString(3));
				}
			}
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		//de forma similar se pueden ejecutar acciones de actualizacion sobre el PreparedStatement
	}
	/**
	 * Demo de acceso a bases de datos, parte 3: Uso de Apache commons-dbutils.
	 * Las librerias Apache Commons https://commons.apache.org/ incluyen muchos componentes
	 * que facilitan y amplian funcionalidades estandar en Java.
	 * Una de ellas es DbUtils que permite simplificar el acceso y recuperacion de datos procedentes 
	 * de una base de datos que se muestra en este ejemplo:
	 * El acceso a la base de datos con DbUtils se basa en el uso de objetos QueryRunner que 
	 * realizan las consultas y handlers que indican como se manejaran los datos. 
	 */
	public void demo4DbUtils() {
		createTable();
		//El siguiente ejemplo define un handler que obtendra los resultados de una consulta
		//como una lista de objetos (POJOs/Beans). Comparar este codigo con lo que seria necesario
		//para leer un resultset y poner los resultados en los objetos.
		Connection conn=null;
		List<Entity> pojoList; //lista de objetos que seran devueltos por la query
		try {
			conn=DriverManager.getConnection(URL); //NOSONAR
			//declara el handler que permitira obtener la lista de objetos de la clase indicada
			BeanListHandler<Entity> beanListHandler=new BeanListHandler<>(Entity.class);
			//Declara el runner que ejecutara la consulta
			QueryRunner runner=new QueryRunner();
			//ejecuta la consulta, el ultimo argumento es el parametro (lista variable si hay mas de uno)
			String sql="select id,id2,text from test where id>=?";
			pojoList=runner.query(conn, sql, beanListHandler, 2);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn); //usar este metodo para facilitar el cierre de conexion de forma mas segura
		}
		//al ejecutarse con el parametro 1 devolvera la unica fila con id>1
		for (Entity item : pojoList)
			log.info("demo4DbUtils (Bean): "+item.getId()+" "+item.getId2()+" "+item.getText());
		
		//El siguiente ejemplo muestra lo mismo pero usando un MapListHandler que obtiene una lista de maps
		//escrito de forma mas compacta
		List<Map<String,Object>> mapList; //lista de maps que seran devueltos por la query
		try {
			conn=DriverManager.getConnection(URL); //NOSONAR
			String sql="select id,id2,text from test where id>?";
			mapList=new QueryRunner().query(conn, sql, new MapListHandler(),1);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
		for (Map<String,Object> item : mapList)
			log.info("demo4DbUtils (Map): "+item.get("id")+" "+item.get("id2")+" "+item.get("text"));
		
		//Otro ejemplo que obtiene un unico valor escalar.
		Integer cnt=null;
		try {
			conn=DriverManager.getConnection(URL); //NOSONAR
			String sql="select count(*) as cnt from test where id>?";
			cnt=new QueryRunner().query(conn, sql, new ScalarHandler<Integer>(), 0);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
		log.info("demo4DbUtils (Scalar): numero de filas: "+cnt);
		
		//Como se ve en los ejemplos anteriores, el patron es siempre el mismo, 
		//solo cambia el Handler en funcion del tipo de dato que se desea obtener

		//Existen otros tipos de handlers para manejar otros tipos de valores como escalares, arrays o listas,
		//y metodos de QueryRunner para sentencias sql de actualizacion
		//Ver mas documentacion:
		//http://commons.apache.org/proper/commons-dbutils/apidocs/index.html
		//https://commons.apache.org/proper/commons-dbutils/examples.html
	}

}
