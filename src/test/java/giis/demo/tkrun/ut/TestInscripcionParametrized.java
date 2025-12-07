package giis.demo.tkrun.ut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import giis.demo.tkrun.CarrerasModel;
import giis.demo.util.ApplicationException;
import giis.demo.util.Database;
import giis.demo.util.Util;

/**
 * Pruebas parametrizadas (requiere declarar junit-jupiter-params en el pom.xml).
 * En JUnit4 la forma de parametrizar las pruebas es similar, utilizando JUnitParams (https://github.com/Pragmatists/JUnitParams):
 */
public class TestInscripcionParametrized {
	private static Database db = new Database();

	@BeforeEach
	public void setUp() {
		db.createDatabase(true);
		giis.demo.tkrun.ut.TestInscripcion.loadCleanDatabase(db);
	}

	/**
	 * Solamente hay que indicar un array con los parametros para cada test, en cada uno de ellos los 
	 * parametros en formato csv, y los argumentos se ponen directamente en el metodo de prueba.
	 * Los parametros se pueden obtener desde diferentes fuentes de datos, 
	 * p.e. para usar un fichero externo, con la anotacion @CsvFileSource
	 */
	@ParameterizedTest
	@CsvSource({ 
		"2016-11-10, -30, 103",
		"2016-11-10,   0, 102",
		"2016-11-10, +50, 101",
		"2016-11-15, -30, 103",
		"2016-11-19,   0, 102"})
	public void testPorcentajeDescuentoRecargoValidas(String fechaStr, int descuentoRecargo, long idCarrera) {
		Date fecha = Util.isoStringToDate(fechaStr);
		CarrerasModel inscr = new CarrerasModel();
		assertEquals(descuentoRecargo, inscr.getDescuentoRecargo(idCarrera, fecha));
	}

	/**
	 * De la misma forma se pueden probar las clases invalidas que comprueban que aparezca una excepcion
	 */
	@ParameterizedTest
	@CsvSource({ 
		"100, No es posible la inscripcion en esta fecha",
		"104, No es posible la inscripcion en esta fecha",
		"99, Id de carrera no encontrado: 99"})
	public void testPorcentajeDescuentoRecargoInvalidas(long idCarrera, String message) {
		Date fecha = Util.isoStringToDate("2016-11-10");
		CarrerasModel inscr = new CarrerasModel();
		ApplicationException exception = assertThrows(ApplicationException.class, () -> {
			inscr.getDescuentoRecargo(idCarrera, fecha);
		});
		assertEquals(message, exception.getMessage());
	}

}
