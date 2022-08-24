package giis.demo.tkrun.ut;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import giis.demo.tkrun.*;
import giis.demo.util.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Ejemplo de pruebas parametrizadas con JUnit4 (https://github.com/junit-team/junit4/wiki/parameterized-tests),
 * (incluye las pruebas validas de getDescuentoRecargo).
 * Nota: solo se permite un mismo conjunto de parametros por clase (si se requieren varios e la misma clase
 * ver https://stackoverflow.com/questions/43249970/how-to-add-multi-parameterized-unit-tests-in-the-same-test-class)
 * o usar p.e. JUnitParams
 */
@RunWith(Parameterized.class)
public class TestInscripcionParametrized {
	private static Database db=new Database();
	@Before
	public void setUp() {
		db.createDatabase(true);
		giis.demo.tkrun.ut.TestInscripcion.loadCleanDatabase(db); 
	}
	/**
	 * La prueba testPorcentajeDescuentoRecargoValidas que se encuentra en TestInscripcionJUnit3
	 * realiza cinco asserts en un mismo metodo, que en realidad son cinco tests que solo varian en los datos usados.
	 * En JUnit4 existe la posibilidad de crear tests parametrizdos:
	 * - definimos por separado los valores de los parametros
	 * - establecemos el mapeo de los parametros a variables que serán las utilizadas por el test
	 * - solo se necesita implementar un test, que se ejecutara tantas veces como parametros
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
			//Reutilizamos el setUp para los tests de la lista de carreras mostradas al usuario
			//utlizando una fecha y diferentes carreras que nos cubriran las clases validas
			{"2016-11-10", -30, 103},
			{"2016-11-10",  0, 102},
			{"2016-11-10", +50, 101},
			//Como no se han probado los valores limite en los dos extremos de los rangos, 
			//anyade casos para ello (fase 1 y 2, extremo superior)
			{"2016-11-15", -30, 103},
			{"2016-11-19",   0, 102},
		});
	}
	@Parameter(value=0) public String fechaStr;
	@Parameter(value=1) public int descuentoRecargo;
	@Parameter(value=2) public long idCarrera;
	@Test
	public void testPorcentajeDescuentoRecargoValidas() {
		Date fecha=Util.isoStringToDate(fechaStr);
		CarrerasModel inscr=new CarrerasModel();
		assertEquals(descuentoRecargo,inscr.getDescuentoRecargo(idCarrera,fecha));
	}

}
