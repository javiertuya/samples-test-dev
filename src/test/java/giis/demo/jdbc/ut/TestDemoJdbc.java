package giis.demo.jdbc.ut;

import org.junit.jupiter.api.Test;

import giis.demo.jdbc.DemoJdbc;

/**
 * Ejecuta las demo de acceso a base de datos con JDBC
 * (no compara resultados, solo se usa como ejecutor de los demos)
 */
public class TestDemoJdbc {
	@Test
	public void testDemo1Basic() {
		DemoJdbc d = new DemoJdbc();
		d.demo1Basic();
	}

	@Test
	public void testDemo2TryWithResources() {
		DemoJdbc d = new DemoJdbc();
		d.demo2TryWithResources();
	}

	@Test
	public void testDemo3Parameters() {
		DemoJdbc d = new DemoJdbc();
		d.demo3Parameters();
	}

	@Test
	public void testDemo4DbUtils() {
		DemoJdbc d = new DemoJdbc();
		d.demo4DbUtils();
	}

	@Test
	public void testDemo5Autoincrement() {
		DemoJdbc d = new DemoJdbc();
		d.demo5Autoincrement();
	}
}
