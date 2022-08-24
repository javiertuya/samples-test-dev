package giis.demo.jdbc;

public class Entity {
	private Integer id;
	private Integer id2;
	private String text;
	
	public Integer getId() { return this.id; }
	public Integer getId2() { return this.id2; }
	public String getText() { return this.text; }
	public void setId(Integer value) { this.id=value; }
	public void setId2(Integer value) { this.id2=value; }
	public void setText(String value) { this.text=value; }
	//NOTA: se pueden generar getters y setters de forma automatica usando lombok:  
	//https://www.sitepoint.com/declutter-pojos-with-lombok-tutorial/
	//http://www.baeldung.com/intro-to-project-lombok
}
