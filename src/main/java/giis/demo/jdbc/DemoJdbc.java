package giis.demo.jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * 
 * Incluye varios metodos que se pueden ejecutar desde los tests en src/test/java (giis.demo.jdbc.ut):
 * (1) Consulta e insercion de datos y manejo basico de excepciones
 * (2) Excepciones con Try With Resources
 * (3) Ejecucion de consultas con parametros (PreparedStatement)
 * (4) Uso de Apache Commons DbUtils para simplificar las consultas y manejo de excepciones
 * (5) Uso de campos autoincrementales
 */
public class DemoJdbc {
	//informacion de conexion a la base de datos utilizada
	public static final String DRIVER="org.sqlite.JDBC";
	public static final String URL="jdbc:sqlite:DemoDB.db";

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
				stmt.executeUpdate("drop table if exists Test");
			} catch (SQLException e) {
				//ignora excepcion, que se causara si la tabla no existe en la bd (p.e. al ejecutar la primera vez)
			}
			stmt.executeUpdate("create table Test(id integer not null, id2 int, text varchar(32))");
			stmt.executeUpdate("insert into Test(id,id2,text) values(1,null,'abc')");
			stmt.executeUpdate("insert into Test(id,id2,text) values(2,9999,'xyz')");
			stmt.close(); //no olvidar cerrar estos objetos
			cn.close();

			//Consulta todas las filas a partir del resultado de una query SQL
			cn=DriverManager.getConnection(connString); //NOSONAR
			stmt=cn.createStatement(); //NOSONAR
			ResultSet rs=stmt.executeQuery("select id,id2,text from Test order by id desc"); //NOSONAR
			while (rs.next()) { //cada vez que se llama rs.next() avanza el cursor a una fila
				int id=rs.getInt(1);        //obtencion de un valor con un tipo de dato especificado, indicando el numero de columna
				String id2=rs.getString(2); //obtencion de un valor como string, aunque sea entero
				if (rs.wasNull())           //comprobacion de valores nulos (respecto del ultimo get realizado)
					id2="NULO";			    //  si es nulo puedo hacer un tratamiento especial, en este caso poner un valor
				String text=rs.getString("text"); //obtencion de un valor indicando el nombre de la columna
				log.info("demo1Basic - id: {} id2: {} text: {}", id, id2, text);
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
				stmt.executeUpdate("drop table if exists Test");
				stmt.executeUpdate("create table Test(id int not null, id2 int, text varchar(32))");
				stmt.executeUpdate("insert into Test(id,id2,text) values(1,null,'abc')");
				stmt.executeUpdate("insert into Test(id,id2,text) values(2,9999,'xyz')");
			}
		} catch (SQLException e) {
			//Ojo, no dejar pasar las excepciones (no limitarse a dejar el codigo autoegenerado por Eclipse haciendo solo printStackTrace)
			throw new UnexpectedException(e); //Es mas habitual usar excepciones propias de la aplicacion
		}
	}
	
	/**
	 * Demo basico de acceso a bases de datos, parte 2:
	 * Uso de he try-with-resources para manejar excepciones y cerrar de forma segura los recursos
	 * (Las mismas acciones que el anterior pero con mejor control de excepciones)
	 */
	public void demo2TryWithResources() {
		createTable();
		//En un mismo try se pueden poner diferentes sentencias que crean objetos que gestionan recursos que hay que cerrar
		try (Connection cn=DriverManager.getConnection(URL); //NOSONAR
				Statement stmt=cn.createStatement();
				ResultSet rs=stmt.executeQuery("select id,id2,text from Test order by id desc")) {
			while (rs.next()) { //cada vez que se llama rs.next() avanza el cursor a una fila
				int id=rs.getInt(1);        //obtencion de un valor con un tipo de dato especificado, indicando el numero de columna
				String id2=rs.getString(2); //obtencion de un valor como string, aunque sea entero
				if (rs.wasNull())           //comprobacion de valores nulos (respecto del ultimo get realizado)
					id2="NULO";			    //  si es nulo puedo hacer un tratamiento especial, en este caso poner un valor
				String text=rs.getString("text"); //obtencion de un valor indicando el nombre de la columna
				log.info("demo2TryWithResources - id: {} id2: {} text: {}", id, id2, text);
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
			PreparedStatement pstmt=cn.prepareStatement("select id,id2,text from Test where id>=?")) {
			pstmt.setInt(1, 2); // pone valor 2 en el primer (y unico) parametro
			try (ResultSet rs=pstmt.executeQuery()) {
				while (rs.next()) {
					log.info("demo3Parameters - rs(1): {} rs(2): {} rs(3): {}", rs.getInt(1), rs.getInt(2), rs.getString(3));
				}
			}
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		//de forma similar se pueden ejecutar acciones de actualizacion sobre el PreparedStatement
	}
	
	/**
	 * Demo de acceso a bases de datos, parte 3: Uso de Apache commons-dbutils.
	 * 
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
			String sql="select id,id2,text from Test where id>=?";
			pojoList=runner.query(conn, sql, beanListHandler, 2);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn); //usar este metodo para facilitar el cierre de conexion de forma mas segura
		}
		//al ejecutarse con el parametro 1 devolvera la unica fila con id>1
		for (Entity item : pojoList)
			log.info("demo4DbUtils (Bean) - id: {} id2: {} text: {}", item.getId(), item.getId2(), item.getText());
		
		//El siguiente ejemplo muestra lo mismo pero usando un MapListHandler que obtiene una lista de maps
		//escrito de forma mas compacta
		List<Map<String,Object>> mapList; //lista de maps que seran devueltos por la query
		try {
			conn=DriverManager.getConnection(URL); //NOSONAR
			String sql="select id,id2,text from Test where id>?";
			mapList=new QueryRunner().query(conn, sql, new MapListHandler(),1);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
		for (Map<String,Object> item : mapList)
			log.info("demo4DbUtils (Map) - id: {} id2: {} text: {}", item.get("id"), item.get("id2"), item.get("text"));
		
		//Otro ejemplo que obtiene un unico valor escalar.
		Integer cnt=null;
		try {
			conn=DriverManager.getConnection(URL); //NOSONAR
			String sql="select count(*) as cnt from Test where id>?";
			cnt=new QueryRunner().query(conn, sql, new ScalarHandler<Integer>(), 0);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
		log.info("demo4DbUtils (Scalar) - numero de filas: {}", cnt);
		
		//Como se ve en los ejemplos anteriores, el patron es siempre el mismo, 
		//solo cambia el Handler en funcion del tipo de dato que se desea obtener

		//Existen otros tipos de handlers para manejar otros tipos de valores como escalares, arrays o listas,
		//y metodos de QueryRunner (p.e. update) para sentencias sql de actualizacion
		
		//Ver mas documentacion:
		//http://commons.apache.org/proper/commons-dbutils/apidocs/index.html
		//https://commons.apache.org/proper/commons-dbutils/examples.html
	}
	
	/**
	 * Creacion de tabla con un campo autoincremental
	 */
	private void createTableAutoincrement() {
		try (Connection cn=DriverManager.getConnection(URL)) { //NOSONAR
			try (Statement stmt = cn.createStatement()) {
				stmt.executeUpdate("drop table if exists TestAuto");
				stmt.executeUpdate("create table TestAuto(id integer primary key autoincrement not null, id2 int, text varchar(32))");
				stmt.executeUpdate("insert into TestAuto(id2,text) values(null,'abc')");
				stmt.executeUpdate("insert into TestAuto(id2,text) values(9999,'xyz')");
			}
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	/**
	 * Demo de manejo de claves primarias autoincrementales:
	 * Este metodo se encarga solamente de crear la tabla para la prueba, la conexion
	 * y gestionar las excepciones, invocando a demo5AutoincrementImpl
	 * que contiene el codigo de la demo.
	 */
	public void demo5Autoincrement() {
		createTableAutoincrement();
		Connection conn=null;
		try {
			conn=DriverManager.getConnection(URL); //NOSONAR
			demo5AutoincrementImpl(conn);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		} finally {
			DbUtils.closeQuietly(conn); //usar este metodo para facilitar el cierre de conexion de forma mas segura
		}
	}
	/**
	 * Demo de manejo de claves primarias autoincrementales:
	 * Ilustra varias formas de consultar en SQLite la ultima clave creada y reinicar la secuencia de claves
	 * (estas acciones son altamente dependientes del SGBD utilizado).
	 * 
	 * Este metodo utiliza DbUtils de una forma compacta, partiendo de una BD creada y conexion abierta,
	 * acercandose mas a lo que contendria un metodo que implemente la logica de negocio de una aplicacion
	 */
	public void demo5AutoincrementImpl(Connection conn) throws SQLException {
		//Insercion de una nueva fila. Como la tabla ha sido creada con dos filas, dara como resultado tres filas.
		//Notar que al ser la clave autoincremental (generada por el SGBD), este campo no se incluye en el insert.
		log.info("demo5Autoincrement - Insercion de un tercer elemento, obtendra el siguiente valor de la secuencia id=3");
		new QueryRunner().update(conn, "insert into TestAuto(id2,text) values(null,'abc')");
		String sql="select id,id2,text from TestAuto where id>=?";
		List<Entity> pojoList=new QueryRunner().query(conn, sql, new BeanListHandler<>(Entity.class), 0);
		for (Entity item : pojoList)
			log.info("demo5Autoincrement - id: {} id2: {} text: {}", item.getId(), item.getId2(), item.getText());
		
		//Frecuentemente, tras una insercion se necesita conocer la clave primaria que ha sido generada
		//(p.e. para enlazar con una tabla detalle).
		//Aqui se ilustran dos formas de realizarlo:
		// (1) utilizando una funcion  que devuelve el ultimo id generado en la ultima operacion de insercion.
		// (2) consultando la tabla de secuencias (sqlite_sequence) que devuelve el ultimo id generado para cada tabla.
		//NOTA: Por el propio disenyo de SQLite, estas funciones pueden no devolver el valor correcto si hay queries concurrentes.
		log.info("demo5Autoincrement - Lectura del ultimo id generado por dos metodos diferentes");
		long lastId=new QueryRunner().query(conn, "select last_insert_rowid()", new ScalarHandler<Integer>());
		log.info("demo5Autoincrement - last_insert_rowid() - lastId: {}", lastId);

		lastId=new QueryRunner().query(conn, "select seq from sqlite_sequence where name='TestAuto'", new ScalarHandler<Integer>());
		log.info("demo5Autoincrement - sqlite_sequence - lastId: {}", lastId);
		
		//Cuando se ejecutan tests es importante comenzar con una base de datos en un estado consistente (o vacia).
		//En este punto ya hay tres filas, aunque las eliminemos, esto no reinicia las secuencias, 
		//por lo que el siguiente valor insertado tendria 4 como clave primaria.
		//Se incluye una inicializacion de la ultima secuencia para la tabla actualizando directamente la tabla sqlite_sequence
		log.info("demo5Autoincrement - Elimina los valores de la tabla y reinicia el valor de la secuencia autoincremental");
		new QueryRunner().update(conn, "delete from TestAuto");
		new QueryRunner().update(conn, "update sqlite_sequence set seq=0 where name='TestAuto'");
		new QueryRunner().update(conn, "insert into TestAuto(id2,text) values(111,'reset')");
		sql="select id,id2,text from TestAuto where id>=?";
		pojoList=new QueryRunner().query(conn, sql, new BeanListHandler<>(Entity.class), 0);
		for (Entity item : pojoList)
			log.info("demo5Autoincrement - id: {} id2: {} text: {}", item.getId(), item.getId2(), item.getText());
	}

}
