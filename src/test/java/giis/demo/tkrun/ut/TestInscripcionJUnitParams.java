package giis.demo.tkrun.ut;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.runner.RunWith;
import junitparams.Parameters;
import junitparams.JUnitParamsRunner;

import giis.demo.tkrun.*;
import giis.demo.util.*;

import java.util.Date;

/**
 * Alternativa para pruebas parametrizadas utilizando JUnitParams (https://github.com/Pragmatists/JUnitParams):
 * - Permite un codigo mas compacto para expresar los parametros (tambien hay posibilidad de obtenerlos de un csv externo)
 * - No tiene la restriccion de un unico conjunto de parametros en la clase
 * - Pero desde Eclipse tienen que ejecutarse todas las pruebas de la clase a la vez, no se permite ejecutar de forma independiente
 */
@RunWith(JUnitParamsRunner.class)
public class TestInscripcionJUnitParams {
	private static Database db=new Database();
	@Before
	public void setUp() {
		db.createDatabase(true);
		giis.demo.tkrun.ut.TestInscripcion.loadCleanDatabase(db); 
	}
	/**
	 * El test simplemente tiene que indicar un array de tests, en cada uno de ellos los parametros en formato csv,
	 * y los argumentos se ponen directamente en el metodo de prueba
	 */
	@Test
	@Parameters({ 
		"2016-11-10, -30, 103",
		"2016-11-10,   0, 102",
		"2016-11-10, +50, 101",
		"2016-11-15, -30, 103",
		"2016-11-19,   0, 102"})
	public void testPorcentajeDescuentoRecargoValidas(String fechaStr, int descuentoRecargo, long idCarrera) {
		Date fecha=Util.isoStringToDate(fechaStr);
		CarrerasModel inscr=new CarrerasModel();
		assertEquals(descuentoRecargo,inscr.getDescuentoRecargo(idCarrera,fecha));
	}
	
	/**
	 * De la misma forma se pueden probar las clases invalidas que comprueban que aparezca una excepcion
	 */
	@Test
	@Parameters({ 
		"100, No es posible la inscripcion en esta fecha",
		"104, No es posible la inscripcion en esta fecha",
		"99, Id de carrera no encontrado: 99"})
	public void testPorcentajeDescuentoRecargoInvalidas(long idCarrera, String message) {
		Date fecha=Util.isoStringToDate("2016-11-10");
		CarrerasModel inscr=new CarrerasModel();
		ApplicationException exception=assertThrows(ApplicationException.class, () -> {
			inscr.getDescuentoRecargo(idCarrera,fecha);
		});
		assertEquals(message, exception.getMessage());
	}

}
