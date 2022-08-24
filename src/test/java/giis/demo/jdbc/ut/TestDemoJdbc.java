package giis.demo.jdbc.ut;

import giis.demo.jdbc.DemoJdbc;
import junit.framework.TestCase;
/**
 * Ejecuta las demo de acceso a base de datos con JDBC
 * (no compara resultados, solo se usa como ejecutor de los demos)
 */
public class TestDemoJdbc extends TestCase {
	public void testDemo1Basic() {
		DemoJdbc d=new DemoJdbc();
		d.demo1Basic();
	}
	public void testDemo2TryWithResources() {
		DemoJdbc d=new DemoJdbc();
		d.demo2TryWithResources();
	}
	public void testDemo3Parameters() {
		DemoJdbc d=new DemoJdbc();
		d.demo3Parameters();
	}
	public void testDemo4DbUtils() {
		DemoJdbc d=new DemoJdbc();
		d.demo4DbUtils();
	}
}
