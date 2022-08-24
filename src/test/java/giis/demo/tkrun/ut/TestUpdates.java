package giis.demo.tkrun.ut;
import org.junit.*;
import static org.junit.Assert.assertEquals;

import giis.demo.tkrun.*;
import giis.demo.util.*;

import java.util.List;

/**
 * Ejemplo de comprobación de resultados en pruebas que actualizan la base de datos.
 * Se tratan igual que las que consultan, pero en este caso es esencial comprobar tanto los datos que se actualizan
 * en la base de datos los datos que tienen que cambiar como que no se han actualizado los datos que no deberian cambiar
 */
public class TestUpdates {
	private static Database db=new Database();
	@Before
	public void setUp() {
		db.createDatabase(true);
		giis.demo.tkrun.ut.TestInscripcion.loadCleanDatabase(db); 
	}
	
	/**
	 * Prueba otro metodo del modelo que actualiza las fechas de inscripcion de una carrera:
	 * comprueba tanto las fechas que se han actualizado como las que no se han actualizado para evitar efectos indeseados
	 */
	@Test
	public void testUpdateFechasInscripcion1() {
		CarrerasModel inscr=new CarrerasModel();
		inscr.updateFechasInscripcion(101, Util.isoStringToDate("2016-09-01"), Util.isoStringToDate("2016-09-02"));
		//el test habra modificado las dos fechas de la carrera 101,
		//lee todos los datos de la tabla y las compara con los iniciales tras cambiar solamente estos dos datos
		List<CarreraEntity> carreras=db.executeQueryPojo(CarreraEntity.class, "SELECT * FROM carreras ORDER BY id");
        assertEquals(
				"100,2016-10-05,2016-10-25,2016-11-09,finalizada\n"
				+"101,2016-09-01,2016-09-02,2016-11-10,en fase 3\n"
				+"102,2016-11-05,2016-11-09,2016-11-20,en fase 2\n"
				+"103,2016-11-10,2016-11-15,2016-11-21,en fase 1\n"
				+"104,2016-11-11,2016-11-15,2016-11-22,antes inscripcion\n",
        		Util.pojosToCsv(carreras,new String[] {"id","inicio","fin","fecha","descr"}));
 	}
	/**
	 * Lo anterior comprueba todos los datos pero es dificil de establecer los valores deseados
	 * pues hay que reproducir todo el contenido de la tabla inicial salvo lo que debe cambiar.
	 * Otra forma mas facil crear primero la salida deseada leyendo de la base de datos y modificando solo los valores que cambian
	 */
	@Test
	public void testUpdateFechasInscripcion2() {
		//crea la salida deseada leyendo el estado inicial y cambiando solo las fechas de la carrera 101 (segundo elemento de la lista)
		List<CarreraEntity> expected=db.executeQueryPojo(CarreraEntity.class, "SELECT * FROM carreras ORDER BY id");
		expected.get(1).setInicio("2016-09-01");
		expected.get(1).setFin("2016-09-02");

		//ahora se ejecuta el metodo que actualizara la base de datos
		CarrerasModel inscr=new CarrerasModel();
		inscr.updateFechasInscripcion(101, Util.isoStringToDate("2016-09-01"), Util.isoStringToDate("2016-09-02"));

		//y se hace la comprobacion respecto del estado actual tras el cambio
		//El assert se hace sobre las representaciones csv de los datos para que en caso de discrepancias sea facil de comprobar
		List<CarreraEntity> actual=db.executeQueryPojo(CarreraEntity.class, "SELECT * FROM carreras ORDER BY id");
        assertEquals(Util.pojosToCsv(expected,new String[] {"id","inicio","fin","fecha","descr"}),
        		Util.pojosToCsv(actual,new String[] {"id","inicio","fin","fecha","descr"}));
 	}

}
