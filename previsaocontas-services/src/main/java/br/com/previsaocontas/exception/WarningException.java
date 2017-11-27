package br.com.previsaocontas.exception;

public class WarningException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public WarningException(String msg) {
	super(msg);
    }

    public WarningException(String msg, Throwable cause) {
	super(msg, cause);
    }

}
