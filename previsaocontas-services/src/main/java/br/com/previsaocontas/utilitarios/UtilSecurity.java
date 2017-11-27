package br.com.previsaocontas.utilitarios;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * <b>Title:</b> UtilSecurity.java
 * </p>
 * 
 * <p>
 * <b>Description:</b> Classe utilitária para segurança.
 * </p>
 * 
 * <p>
 * <b>Company: </b> ITSS Factory
 * </p>
 * 
 * @author Silvânio Júnior
 * 
 * @version 1.0.0
 */
public class UtilSecurity {

	/** Atributo digester. */
	private static MessageDigest	digester;

	static {
		try {
			
			
			
			
			UtilSecurity.digester = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método responsável por criptografar dados.
	 *
	 * @author Silvânio Júnior
	 *
	 * @param str
	 * 
	 * @return <code>String</code>
	 */
	public static String cript(final String str) {

		if (str == null || str.length() == 0) {
			//throw new IllegalArgumentException("String to encript cannot be null or zero length");
			return "";
		}

		UtilSecurity.digester.update(str.getBytes());
		final byte[] hash = UtilSecurity.digester.digest();
		final StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			if (( 0xff & hash[i] ) < 0x10) {
				hexString.append("0" + Integer.toHexString(( 0xFF & hash[i] )));
			} else {
				hexString.append(Integer.toHexString(0xFF & hash[i]));
			}
		}
		return hexString.toString();
	}

	public static void main(final String[] args) {

		System.out.println(UtilSecurity.cript("admin"));
	}
}
