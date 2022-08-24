package giis.demo.tkrun.ut;
import junit.framework.*;
import giis.demo.tkrun.*;
import giis.demo.util.*;
import java.util.*;

/**
 * Ejemplo de pruebas con JUnit3 (incluye las pruebas de getListaCarreras y las invalidas de getDescuentoRecargo)
 * Pruebas del ejemplo de Inscripciones en carreras populares (primer ejemplo).
 * Notar que en JUnit3 no se usan anotaciones, sino convenios (Test* para la clases, test* para los metodos, 
 * setUp y tearDown para inicializacion y finalizacion), las clases deben extender TestCase, que ya define
 * los metodos estaticos assert*
 */
public class TestInscripcionJUnit3 extends TestCase {
	private Database db=new Database();
	
	public void setUp() {
		db.createDatabase(true);
		giis.demo.tkrun.ut.TestInscripcion.loadCleanDatabase(this.db); 
	}
	public void tearDown(){
	}
	
	/**
	 * Test de clases validas (getListaCarreras)
	 */
	public void testCarrerasActivasList() {
		CarrerasModel inscr=new CarrerasModel();
		List<Object[]> carreras=inscr.getListaCarrerasArray(Util.isoStringToDate("2016-11-10"));
		//Deben mostrarse todas las carreras de la BD menos la primera que es pasada, la ultima no debe indicar abierto
		assertEquals("el numero de carreras mostradas es incorrecto",4,carreras.size());
		//la lista de carreras contiene un array de objetos de una dimension
		assertEquals("101-en fase 3 (Abierta)",carreras.get(0)[0]);
		assertEquals("102-en fase 2 (Abierta)",carreras.get(1)[0]);
		assertEquals("103-en fase 1 (Abierta)",carreras.get(2)[0]);
		assertEquals("104-antes inscripcion ",carreras.get(3)[0]);
	}
	
	/**
	 * Test clases invalidas: ilustra como se comprueban las excepciones en JUnit3
	 */
	public void testPorcentajeDescuentoRecargoInvalidaCarreraFinalizada() {
		Date fecha=Util.isoStringToDate("2016-11-10");
		CarrerasModel inscr=new CarrerasModel();
		try { 
			inscr.getDescuentoRecargo(100,fecha);
			fail("no se puede hacer inscripcion en carrera finalizada");
		} catch (RuntimeException e) {
			assertEquals("No es posible la inscripcion en esta fecha",e.getMessage());
		}
	}

}
