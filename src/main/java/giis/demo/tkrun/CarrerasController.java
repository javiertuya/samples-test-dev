package giis.demo.tkrun;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;
import giis.demo.util.Util;

/**
 * Controlador para la funcionalidad de visualizacion de carreras para la inscripcion.
 * Es el punto de entrada de esta pantalla que se invocará:
 * -instanciando el controlador con la vista y el modelo
 * -ejecutando initController que instalara los manejadores de eventos
 */
public class CarrerasController {
	private CarrerasModel model;
	private CarrerasView view;
	private String lastSelectedKey=""; //recuerda la ultima fila seleccionada para restaurarla cuando cambie la tabla de carreras

	public CarrerasController(CarrerasModel m, CarrerasView v) {
		this.model = m;
		this.view = v;
		//no hay inicializacion especifica del modelo, solo de la vista
		this.initView();
	}
	/**
	 * Inicializacion del controlador: anyade los manejadores de eventos a los objetos del UI.
	 * Cada manejador de eventos se instancia de la misma forma, para que invoque un metodo privado
	 * de este controlador, encerrado en un manejador de excepciones generico para mostrar ventanas
	 * emergentes cuando ocurra algun problema o excepcion controlada.
	 */
	public void initController() {
		//ActionListener define solo un metodo actionPerformed(), es un interfaz funcional que se puede invocar de la siguiente forma:
		//view.getBtnTablaCarreras().addActionListener(e -> getListaCarreras());
		//ademas invoco el metodo que responde al listener en el exceptionWrapper para que se encargue de las excepciones
		view.getBtnTablaCarreras().addActionListener(e -> SwingUtil.exceptionWrapper(() -> getListaCarreras()));
	
		//En el caso del mouse listener (para detectar seleccion de una fila) no es un interfaz funcional puesto que tiene varios metodos
		//ver discusion: https://stackoverflow.com/questions/21833537/java-8-lambda-expressions-what-about-multiple-methods-in-nested-class
		view.getTablaCarreras().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//no usa mouseClicked porque al establecer seleccion simple en la tabla de carreras
				//el usuario podria arrastrar el raton por varias filas e interesa solo la ultima
				SwingUtil.exceptionWrapper(() -> updateDetail());
			}
		});
	}
	
	public void initView() {
		//Inicializa la fecha de hoy a un valor que permitira mostrar carreras en diferentes fases 
		//y actualiza los datos de la vista
		view.setFechaHoy("2016-11-10");
		this.getListaCarreras();
		
		//Abre la ventana (sustituye al main generado por WindowBuilder)
		view.getFrame().setVisible(true); 
	}
	/**
	 * La obtencion de la lista de carreras solo necesita obtener la lista de objetos del modelo 
	 * y usar metodo de SwingUtil para crear un tablemodel que se asigna finalmente a la tabla.
	 */
	public void getListaCarreras() {
		List<CarreraDisplayDTO> carreras=model.getListaCarreras(Util.isoStringToDate(view.getFechaHoy()));
		TableModel tmodel=SwingUtil.getTableModelFromPojos(carreras, new String[] {"id", "descr", "estado"});
		view.getTablaCarreras().setModel(tmodel);
		SwingUtil.autoAdjustColumns(view.getTablaCarreras());
		
		//Como se guarda la clave del ultimo elemento seleccionado, restaura la seleccion de los detalles
		this.restoreDetail();

		//A modo de demo, se muestra tambien la misma informacion en forma de lista en un combobox
		List<Object[]> carrerasList=model.getListaCarrerasArray(Util.isoStringToDate(view.getFechaHoy()));
		ComboBoxModel<Object> lmodel=SwingUtil.getComboModelFromList(carrerasList);
		view.getListaCarreras().setModel(lmodel);
	}
	/**
	 * Restaura la informacion del detalle de la carrera para visualizar los valores correspondientes
	 * a la ultima clave almacenada.
	 */
	public void restoreDetail() {
		//Utiliza la ultimo valor de la clave (que se reiniciara si ya no existe en la tabla)
		this.lastSelectedKey=SwingUtil.selectAndGetSelectedKey(view.getTablaCarreras(), this.lastSelectedKey);
		//Si hay clave para seleccionar en la tabla muestra el detalle, si no, lo reinicia
		if ("".equals(this.lastSelectedKey)) { 
			view.setDescuentoNoAplicable();
			view.getDetalleCarrera().setModel(new DefaultTableModel());		
		} else {
			this.updateDetail();
		}
	}
	/**
	 * Al seleccionar un item de la tabla muestra el detalle con el valor del porcentaje de descuento
	 * de la carrera seleccinada y los valores de esta entidad
	 */
	public void updateDetail() {
		//Obtiene la clave seleccinada y la guarda para recordar la seleccion en futuras interacciones
		this.lastSelectedKey=SwingUtil.getSelectedKey(view.getTablaCarreras());
		int idCarrera=Integer.parseInt(this.lastSelectedKey);
		
		//Detalle de descuento/recargo:
		//Controla excepcion porque el modelo causa excepcion cuando no se puede calcular el descuento
		//y debe indicarse esto en la vista para evitar mostrar datos falsos que se veian antes
		try { 
			int descuento=model.getDescuentoRecargo(idCarrera, Util.isoStringToDate(view.getFechaHoy()));
			view.setDescuento(String.valueOf(descuento));
		} catch (ApplicationException e) {
			view.setDescuentoNoAplicable();
		}
		
		//Detalles de la carrera seleccionada
		CarreraEntity carrera=model.getCarrera(idCarrera);
		TableModel tmodel=SwingUtil.getRecordModelFromPojo(carrera, new String[] {"id", "inicio", "fin", "fecha", "descr"});
		view.getDetalleCarrera().setModel(tmodel);
		SwingUtil.autoAdjustColumns(view.getDetalleCarrera());
	}

}
