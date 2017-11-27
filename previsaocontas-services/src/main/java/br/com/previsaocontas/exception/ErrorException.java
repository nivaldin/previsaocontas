package br.com.previsaocontas.exception;

public class ErrorException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ErrorException(String msg) {
	super(msg);
	super.printStackTrace();
    }

    public ErrorException(String msg, Throwable cause) {
	super(msg, cause);
	super.printStackTrace();
    }

}
