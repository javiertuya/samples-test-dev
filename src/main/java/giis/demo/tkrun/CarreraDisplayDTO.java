package giis.demo.tkrun;
/**
 * Cada una de las filas que muestran al usuario las carreras y su estado
 * IMPORTANTE: Cuando se usan los componentes de Apache Commons DbUtils debe
 * mantenerse de forma estricta el convenio de capitalización de Java:
 *  - Capitalizar todas las palabras que forman un identificador 
 *    excepto la primera letra de nombres de métodos y variables.
 *  - No utilizar subrayados
 * Seguir tambien estos mismos criterios en los nombres de tablas y campos de la BD
 */
public class CarreraDisplayDTO {
	private String id;
	private String descr;
	private String abierta;
	public CarreraDisplayDTO() {}
	public CarreraDisplayDTO(String rowId, String rowDescripcion, String rowEstado) {
		this.id=rowId;
		this.descr=rowDescripcion;
		this.abierta=rowEstado;
	}
	public String getId() { return this.id; }
	public String getDescr() { return this.descr; }
	public String getEstado() { return this.abierta; }
	public void setId(String value) { this.id=value; }
	public void setDescr(String value) { this.descr=value; }
	public void setAbierta(String value) { this.abierta=value; }
	//NOTA: se pueden generar getters y setters de forma automatica usando lombok:  
	//https://www.sitepoint.com/declutter-pojos-with-lombok-tutorial/
	//http://www.baeldung.com/intro-to-project-lombok
}
