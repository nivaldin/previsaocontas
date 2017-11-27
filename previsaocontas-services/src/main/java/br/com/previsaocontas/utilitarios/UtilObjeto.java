package br.com.previsaocontas.utilitarios;

import java.util.Collection;
import java.util.Date;

/**
 * <p>
 * <b>Title:</b> UtilObjeto
 * </p>
 * 
 * <p>
 * <b>Description:</b> Classe utilitária responsável por manipular objetos <code>Object</code>.
 * </p>
 * 
 * <p>
 * <b>Company:</b> ITSS Factory
 * </p>
 * 
 * @author Bruno Zafalão
 * 
 * @version 1.0.0
 */
@SuppressWarnings("unchecked")
public final class UtilObjeto {

	/**
	 * Responsável pela criação de novas instâncias desta classe.
	 */
	private UtilObjeto() {

		super();
	}

	/**
	 * Método responsável por verificar se existe referencia armazenada na variável.
	 * 
	 * @param obj
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isNull(final Object obj) {

		return obj == null;
	}

	/**
	 * Método responsável por verificar se não existe referencia armazenada na variável.
	 * 
	 * @param obj
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isNotNull(final Object obj) {
		
		return !isNull(obj);
	}

	/**
	 * Método responsável por verificar se existe referencia armazenada nas variáveis.
	 * 
	 * @param objects
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isNull(final Object... objects) {

		final boolean existeNull = Boolean.FALSE;

		for (final Object obj : objects) {

			if (UtilObjeto.isNull(obj)) {

				return Boolean.TRUE;
			}
		}

		return existeNull;
	}

	/**
	 * Método responsável por obter a classe do objeto.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param objeto
	 * 
	 * @return <code>Class</code>
	 */
	public static <T> Class<T> getClazz(final T objeto) {

		Class<T> classe = null;

		if (!UtilObjeto.isNull(objeto)) {

			classe = UtilObjeto.isClazz(objeto) ? (Class<T>) objeto : (Class<T>) objeto.getClass();
		}

		return classe;
	}

	/**
	 * Método responsável por verificar se o objeto informado é um <code>Class<?></code>
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param objeto
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isClazz(final Object objeto) {

		return UtilObjeto.isObjetoDoTipo(objeto, Class.class);
	}

	/**
	 * Método responsável por verificar se o objeto informado é um <code>Collection</code>
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param objeto
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isColecao(final Object objeto) {

		return UtilObjeto.isObjetoDoTipo(objeto, Collection.class);
	}

	/**
	 * Método responsável por verificar se o objeto informado é um <code>Comparable</code>
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param objeto
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isComparable(final Object objeto) {

		return UtilObjeto.isObjetoDoTipo(objeto, Comparable.class);
	}

	/**
	 * Método responsável por verificar se o objeto informado é um <code>java.util.Date</code>
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param objeto
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isDate(final Object objeto) {

		return UtilObjeto.isObjetoDoTipo(objeto, Date.class);
	}

	/**
	 * Método responsável por verificar se o objeto informado é um <code>java.sql.Date</code>
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param objeto
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isSqlDate(final Object objeto) {

		return UtilObjeto.isObjetoDoTipo(objeto, java.sql.Date.class);
	}

	/**
	 * Método responsável por verificar se um objeto é do tipo informado.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param objeto
	 * 
	 * @param tipo
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isObjetoDoTipo(final Object objeto, final Class<?> tipo) {

		boolean res = false;

		if (!UtilObjeto.isNull(objeto, tipo)) {

			final Class<?> classe = UtilObjeto.getClazz(objeto);

			res = tipo.isAssignableFrom(classe);
		}

		return res;
	}
}
