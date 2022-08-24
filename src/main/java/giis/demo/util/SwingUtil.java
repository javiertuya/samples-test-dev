package giis.demo.util;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Metodos de utilidad para interfaces de usuario con swing (poblar tablas a partir de un objeto POJO
 * que ha sido obtenido desde la base de datos, manejo de excepciones para metodos del 
 * controlador, autoajuste de la dimension de columnas, etc)
 */
public class SwingUtil {
	private SwingUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Ejecuta un metodo en respuesta a un evento envolviendolo en un manejador de excepciones estandar
	 * que muestra un mensaje informativo o mensaje de error en funcion de la excepcion producida
	 * (utilizado en el Controlador al instalar los handlers en respuesta a los eventos de swing)
	 * NOTA: Si devolviese parametros utilizar Consumer en vez de Runnable: http://www.baeldung.com/java-lambda-exceptions
	 * @param consumer Metodo a ejecutar (sin parametros de entrada ni valores de salida)
	 */
	public static void exceptionWrapper(Runnable consumer) {
		try {
			consumer.run();
		} catch (ApplicationException e) { //excepcion controlada de la que se puede recuperar la aplicacion
			showMessage(e.getMessage(), "Informacion",JOptionPane.INFORMATION_MESSAGE);
		} catch (RuntimeException e) { //resto de excepciones, ademas de la ventana informativa muestra el stacktrace
			e.printStackTrace(); //NOSONAR
			showMessage(e.toString(), "Excepcion no controlada",JOptionPane.ERROR_MESSAGE);
		}
	}	 
	private static void showMessage(String message, String title, int type) {
		//Como este metodo no recibe el contexto de la ventana de la aplicacion, 
		//no usa el metodo estatico showMessageDialog de JOptionPane 
		//y establece la posicion para que no aparezca en el centro de la pantalla
	    JOptionPane pane = new JOptionPane(message,type,JOptionPane.DEFAULT_OPTION);
	    pane.setOptions(new Object[] {"ACEPTAR"}); //fija este valor para que no dependa del idioma
	    JDialog d = pane.createDialog(pane, title);
	    d.setLocation(200,200);
	    d.setVisible(true);
	}
	
	/**
	 * Ajusta todas las columnas de la tabla al tamanyo correspondiente al contenido del tablemodel
	 */
	public static void autoAdjustColumns(JTable table) {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //si se usa ON la ultima columna se expandra en el panel
		TableColumnAdjuster tca=new TableColumnAdjuster(table);
		tca.adjustColumns();
	}

	/** 
	 * Obtiene la key (primera columna) de la fila seleccionada en la tabla de carreras o string vacio (si no existe)
	 */
	public static String getSelectedKey(JTable table) {
		int row=table.getSelectedRow(); //el item de primera columna es el id de carrera
		if (row>=0)
			return (String)table.getModel().getValueAt(row,0);
		else //no hay filas seleccionadas
			return "";
	}
	
	/** 
	 * Selecciona la fila de la tabla con la clave indicada y devuelve el valor la clave de la fila seleccionada resultante
	 * (la misma clave o string vacio si no existe la fila)
	 */
	public static String selectAndGetSelectedKey(JTable table, String key) {
		for (int i=0; i<table.getModel().getRowCount(); i++)
			if (table.getModel().getValueAt(i, 0).equals(key)) {
				table.setRowSelectionInterval(i, i);
				return key;
			}
		return ""; //ya no existe esta clave
	}

	//http://www.baeldung.com/apache-commons-beanutils
	//http://commons.apache.org/proper/commons-beanutils/javadocs/v1.8.2/apidocs/org/apache/commons/beanutils/PropertyUtilsBean.html

	/**
	 * Crea un tablemodel a partir de una lista de objetos POJO con las columnas que se indican.
	 * @param pojos Lista de objetos cuyos atributos se utilizaran para crear el tablemodel
	 * (utiliza apache commons beanutils). Si es null solamente crea el tablemodel con las cabeceras de columna
	 * @param colProperties Los nombres de atributo de los objetos (ordenados) que se incluiran en el tablemodel
	 * (cada uno debe disponer del correspondiente getter)
	 */
	public static <E> TableModel getTableModelFromPojos(List<E> pojos, String[] colProperties) {
		//Creacion inicial del tablemodel y dimensionamiento
		//tener en cuenta que para que la tabla pueda mostrar las columnas debera estar dentro de un JScrollPane
		TableModel tm;
		if (pojos==null) //solo las columnas (p.e. para inicializaciones)
			return new DefaultTableModel(colProperties,0);
		else
			tm=new DefaultTableModel(colProperties, pojos.size());
		//carga cada uno de los valores de pojos usando PropertyUtils (de apache coommons beanutils)
		for (int i=0; i<pojos.size(); i++) {
			for (int j=0; j<colProperties.length; j++) {
				try {
					Object pojo=pojos.get(i);
					Object value=PropertyUtils.getSimpleProperty(pojo, colProperties[j]);
					tm.setValueAt(value, i, j);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					throw new UnexpectedException(e);
				}
			}
		}
		return tm;
	}
	public static <E> TableModel getRecordModelFromPojo(E pojo, String[] colProperties) {
		//Creacion inicial del tablemodel y dimensionamiento
		//como solo habra dos columnas pongo una cabecera con dos valores vacios, de forma que 
		//aparezca muy reducida pero con el handler necesario para poder redimensionarla
		TableModel tm;
		tm=new DefaultTableModel(new String[] {"",""}, colProperties.length);
		//carga cada uno de los valores de pojos usando PropertyUtils (de apache coommons beanutils)
			for (int j=0; j<colProperties.length; j++) {
				try {
					tm.setValueAt(colProperties[j], j, 0);
					Object value=PropertyUtils.getSimpleProperty(pojo, colProperties[j]);
					tm.setValueAt(value, j, 1);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					throw new UnexpectedException(e);
				}
			}
		return tm;
	}
	
	/**
	 * Crea un Comboboxmodel a partir de una lista de objetos.
	 * @param lst Lista de arrays de objetos de los cuales se usara el primero de cada uno de ellos para poblar el combo
	 */
	public static ComboBoxModel<Object> getComboModelFromList(List<Object[]> lst) {
		DefaultComboBoxModel<Object> cm=new DefaultComboBoxModel<>();
		for (int i=0; i<lst.size(); i++) {
			Object value=lst.get(i)[0];
			cm.addElement(value);
		}
		return cm;
	}
	
	

}
