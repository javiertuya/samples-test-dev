package giis.demo.util;

/**
 * Excepcion producida por la aplicacion antes situaciones incontroladas
 * (excepciones al acceder a la base de datos o al utlizar metodos que declaran excepciones throwable, etc)
 */
@SuppressWarnings("serial")
public class UnexpectedException extends RuntimeException {
	public UnexpectedException(Throwable e) {
		super(e);
	}
	public UnexpectedException(String s) {
		super(s);
	}
}
