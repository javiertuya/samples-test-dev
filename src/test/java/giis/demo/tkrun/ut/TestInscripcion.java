package giis.demo.tkrun.ut;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import giis.demo.tkrun.CarreraDisplayDTO;
import giis.demo.tkrun.CarrerasModel;
import giis.demo.util.ApplicationException;
import giis.demo.util.Database;
import giis.demo.util.Util;
import giis.visualassert.VisualAssert;

/**
 * Pruebas del ejemplo de Inscripciones en carreras populares (primer ejemplo) con JUnit6
 */
public class TestInscripcion {
	private static Database db=new Database();
	
	@BeforeEach
	public void setUp() {
		db.createDatabase(true);
		loadCleanDatabase(db); 
	}
	
	@AfterEach
	public void tearDown(){
	}
	
	public static void loadCleanDatabase(Database db) {
		db.executeBatch(new String[] {
				"delete from carreras",
				"insert into carreras(id,inicio,fin,fecha,descr) values (100,'2016-10-05','2016-10-25','2016-11-09','finalizada')",
				"insert into carreras(id,inicio,fin,fecha,descr) values (101,'2016-10-05','2016-10-25','2016-11-10','en fase 3')",
				"insert into carreras(id,inicio,fin,fecha,descr) values (102,'2016-11-05','2016-11-09','2016-11-20','en fase 2')",
				"insert into carreras(id,inicio,fin,fecha,descr) values (103,'2016-11-10','2016-11-15','2016-11-21','en fase 1')",
				"insert into carreras(id,inicio,fin,fecha,descr) values (104,'2016-11-11','2016-11-15','2016-11-22','antes inscripcion')"
			});
	}
	
	// //////////////////////////////////////////////
	// Pruebas de obtencion de la lista de carreras
	// /////////////////////////////////////////////
	
	/**
	 * Comprueba la lista de carreras que ve el usuario en el momento de la inscripcion para diferentes fases de inscripcion:
	 * Debe mostrar todas las carreras excluyendo las pasadas, indicando Abierto en las que se puede realizar inscripcion;
	 * Con la base de datos del setUp cubre las cinco clases de equivalencia relativas a Fecha de inscrpcion
	 * (carreras 100 a 104)
	 */
	@Test
	public void testCarrerasActivasList() {
		CarrerasModel inscr = new CarrerasModel();
		List<Object[]> carreras = inscr.getListaCarrerasArray(Util.isoStringToDate("2016-11-10"));
		// Deben mostrarse todas las carreras de la BD menos la primera que es pasada, la ultima no debe indicar abierto
		assertEquals(4, carreras.size(), "el numero de carreras mostradas es incorrecto");
		// la lista de carreras contiene un array de objetos de una dimension
		assertEquals("101-en fase 3 (Abierta)", carreras.get(0)[0]);
		assertEquals("102-en fase 2 (Abierta)", carreras.get(1)[0]);
		assertEquals("103-en fase 1 (Abierta)", carreras.get(2)[0]);
		assertEquals("104-antes inscripcion ", carreras.get(3)[0]);
	}
	
	/**
	 * En la anterior, si falla un assert, no se ejecutan los siguientes.
	 * Desde JUnit5 existe la posibilidad de ejecutar todos los asserts, evitando este problema
	 */
	@Test
	public void testCarrerasActivasListAssertAll() {
		CarrerasModel inscr = new CarrerasModel();
		List<Object[]> carreras = inscr.getListaCarrerasArray(Util.isoStringToDate("2016-11-10"));
		assertAll("Casos 1 y 2",
				() -> assertEquals(4, carreras.size(), "el numero de carreras mostradas es incorrecto"),
				() -> assertEquals("101-en fase 3 (Abierta)", carreras.get(0)[0]),
				() -> assertEquals("102-en fase 2 (Abierta)", carreras.get(1)[0]),
				() -> assertEquals("103-en fase 1 (Abierta)", carreras.get(2)[0]),
				() -> assertEquals("104-antes inscripcion ", carreras.get(3)[0])
				);
	}

	/**
	 * Otra alternativa para comparacion de los valores de una lista, pasando todos sus elementos a un string csv.
	 * Es mas compacto y facil la comparacion de resultados, p.e. cuando aparecen o desaparecen filas.
	 */
	@Test
	public void testCarrerasActivasListAssertCsv() {
		CarrerasModel inscr=new CarrerasModel();
		List<Object[]> carreras=inscr.getListaCarrerasArray(Util.isoStringToDate("2016-11-10"));
		assertEquals(
				"101-en fase 3 (Abierta)\n102-en fase 2 (Abierta)\n103-en fase 1 (Abierta)\n104-antes inscripcion ",
				list2string(carreras));
	}
	// Convierte una lista de Object[] a un string, separando cada item por salto de linea
	private String list2string(List<Object[]> lst) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < lst.size(); i++)
			s.append((i == 0 ? "" : "\n") + lst.get(i)[0]);
		return s.toString();
	}
	
	// /////////////////////////////////////////////////////
	//  Alternativas comprobando el DTO en diversos formatos
	// /////////////////////////////////////////////////////
	
	/**
	 * Otra version de una variante del metodo getListaCarreras, en este caso el DTO devuele una lista de objetos con tres valores,
	 * las comparaciones se realizan para cada uno de ellos.
	 */
	@Test
	public void testCarrerasActivasDto() {
		CarrerasModel inscr = new CarrerasModel();
		List<CarreraDisplayDTO> carreras = inscr.getListaCarreras(Util.isoStringToDate("2016-11-10"));
		assertAll("Carreras activas",
				() -> assertEquals(4, carreras.size(), "el numero de carreras mostradas es incorrecto"),
				() -> assertEquals(carreras.get(0).getId(), "101"),
				() -> assertEquals(carreras.get(0).getDescr(), "en fase 3"),
				() -> assertEquals(carreras.get(0).getEstado(), "(Abierta)"),
				() -> assertEquals(carreras.get(1).getId(), "102"),
				() -> assertEquals(carreras.get(1).getDescr(), "en fase 2"),
				() -> assertEquals(carreras.get(1).getEstado(), "(Abierta)"),
				() -> assertEquals(carreras.get(2).getId(), "103"),
				() -> assertEquals(carreras.get(2).getDescr(), "en fase 1"),
				() -> assertEquals(carreras.get(2).getEstado(), "(Abierta)"),
				() -> assertEquals(carreras.get(3).getId(), "104"),
				() -> assertEquals(carreras.get(3).getDescr(), "antes inscripcion"),
				() -> assertEquals(carreras.get(3).getEstado(), "")
				);
	}

	/**
	 * Alternativa para comparacion utilizando una representacion serializada del DTO a Json 
	 * (utiliza un metodo de utilidad basado en Jackson):
	 * Es mas compacto y facilita la comparacion de resultados, 
	 * permitiendo tambien realizar las comparaciones cuando se prueba un api REST.
	 * El uso de strings multilinea evita la necesidad de hacer escape de las comillas.
	 */
	@Test
	public void testCarrerasActivasDtoJson() throws JsonProcessingException {
		CarrerasModel inscr = new CarrerasModel();
		List<CarreraDisplayDTO> carreras = inscr.getListaCarreras(Util.isoStringToDate("2016-11-10"));
		assertEquals("""
				[{"id":"101","descr":"en fase 3","estado":"(Abierta)"},
				{"id":"102","descr":"en fase 2","estado":"(Abierta)"},
				{"id":"103","descr":"en fase 1","estado":"(Abierta)"},
				{"id":"104","descr":"antes inscripcion","estado":""}]""",
				Util.serializeToJson(CarreraDisplayDTO.class, carreras, false));
	}
	
	/**
	 * Otra alternativa que facilita la comparacion, en vez de comparar con el Json completo se compara
	 * con una representacion estilo CSV en el que cada atributo del objeto se representa como un elemento de un array
	 */
	@Test
	public void testCarrerasActivasDtoCsv() {
		CarrerasModel inscr = new CarrerasModel();
		List<CarreraDisplayDTO> carreras = inscr.getListaCarreras(Util.isoStringToDate("2016-11-10"));
        assertEquals("""
        		101,en fase 3,(Abierta)
        		102,en fase 2,(Abierta)
        		103,en fase 1,(Abierta)
        		104,antes inscripcion,
        		""", 
        		Util.pojosToCsv(carreras,new String[] {"id","descr","estado"}));
 	}
	
	/**
	 * Igual que el anterior, pero utiliza otro componente (visual-assert) para comparar
	 * y generar un archivo html con las diferencias, que se puede revisar sin depender del entorno Eclipse
	 * (util si los strings que se comparan son de gran tamanyo)
	 */
	@Test
	public void testCarrerasActivasDtoCsvHtmlDiffs() {
		CarrerasModel inscr = new CarrerasModel();
		List<CarreraDisplayDTO> carreras = inscr.getListaCarreras(Util.isoStringToDate("2016-11-10"));
		VisualAssert va = new VisualAssert(); // dejara los ficheros con diferencias en target
        va.assertEquals("""
        		101,en fase 3,(Abierta)
        		102,en fase 2,(Abierta)
        		103,en fase 1,(Abierta)
        		104,antes inscripcion,
        		""", 
        		Util.pojosToCsv(carreras,new String[] {"id","descr","estado"}));
 	}
	
	// /////////////////////////////////////////////
	//  Pruebas de excepciones:
	//  Tipicamente, para clases invalidas el comportamiento deseado es que se produzca una excepcion.
	// /////////////////////////////////////////////
		
	/**
	 * Para probar que un metodo debe causar una excepcion, utiliza una expresion lambda en la que se incluye 
	 * el codigo a ejecutar en el que se espera la excepcion.
	 */
	@Test
	public void testCarrerasActivasException() {
		CarrerasModel inscr = new CarrerasModel();
		assertThrows(ApplicationException.class, () -> {
			inscr.getListaCarreras(null);
		});
	}

	/**
	 * Cuando se precisa comprobar tambien el mensaje de la excepcion se utiliza el objeto excepcion
	 * devuelto por assertTrhows para comprobar dicho mensaje.
	 */
	@Test
	public void testCarrerasActivasExceptionMessageCheck() {
		CarrerasModel inscr = new CarrerasModel();
		ApplicationException exception = assertThrows(ApplicationException.class, () -> {
			inscr.getListaCarreras(null);
		});
		assertEquals("La fecha de inscripcion no puede ser nula", exception.getMessage());
	}

	// //////////////////////////////////////////////
	//  Pruebas de la obtencion del descuento/recargo
	// //////////////////////////////////////////////

	/**
	 * Determinacion del descuento o recargo porcentual segun la fecha de inscripcion
	 * (Cubre las clases validas para el Porcentaje de descuento:
	 * 3 relativas fecha de inscripcion y 1 relativa al id de carrera valido )
	 */
	@Test
	public void testPorcentajeDescuentoRecargoValidas() {
		// Reutilizamos el setUp para los tests de la lista de carreras mostradas al usuario
		// utlizando una fecha y diferentes carreras que nos cubriran las clases validas
		Date fecha = Util.isoStringToDate("2016-11-10");
		CarrerasModel inscr = new CarrerasModel();
		assertAll("Porcentaje descuento/recargo",
				() -> assertEquals(-30, inscr.getDescuentoRecargo(103, fecha)),
				() -> assertEquals(0, inscr.getDescuentoRecargo(102, fecha)),
				() -> assertEquals(+50, inscr.getDescuentoRecargo(101, fecha)),
				// Como no se han probado los valores limite en los dos extremos de los rangos,
				// anyade casos para ello (fase 1 y 2, extremo superior)
				() -> assertEquals(-30, inscr.getDescuentoRecargo(103, Util.isoStringToDate("2016-11-15"))),
				() -> assertEquals(0, inscr.getDescuentoRecargo(102, Util.isoStringToDate("2016-11-19")))
				);
	}
	
	/**
	 * Determinacion del descuento o recargo porcentual segun la fecha de inscripcion 
	 * (Cubre las clases invalidas, a las que habria que anyadir la validacion de la fecha)
	 * Para evitar duplicacion de codigo se utiliza un metodo generico invocado desde los tres tests.
	 * En este caso seria preferible utilizar pruebas parametrizadas (ver ejemplo en otra clase)
	 */
	@Test
	public void testPorcentajeDescuentoRecargoInvalidaCarreraFinalizada() {
		porcentajeDescuentoRecargoInvalidas(100, "No es posible la inscripcion en esta fecha");
	}
	@Test
	public void testPorcentajeDescuentoRecargoInvalidaCarreraAntesInscripcion() {
		porcentajeDescuentoRecargoInvalidas(104, "No es posible la inscripcion en esta fecha");
	}
	@Test
	public void testPorcentajeDescuentoRecargoInvalidaCarreraNoExiste() {
		porcentajeDescuentoRecargoInvalidas(99, "Id de carrera no encontrado: 99");
	}
	public void porcentajeDescuentoRecargoInvalidas(long idCarrera, String message) {
		Date fecha = Util.isoStringToDate("2016-11-10");
		CarrerasModel inscr = new CarrerasModel();
		ApplicationException exception = assertThrows(ApplicationException.class, () -> {
			inscr.getDescuentoRecargo(idCarrera, fecha);
		});
		assertEquals(message, exception.getMessage());
	}

}
