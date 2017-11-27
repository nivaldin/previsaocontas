package br.com.previsaocontas.utilitarios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * <b>Title:</b> UtilColecao
 * </p>
 * 
 * <p>
 * <b>Description:</b> Classe responsável por manipular objetos <code>Collection</code>
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
public final class UtilColecao {

	/**
	 * Responsável pela criação de novas instâncias desta classe.
	 */
	private UtilColecao() {

		super();
	}

	/**
	 * Método responsável por ordenar uma coleção.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param colecao
	 * 
	 * @return <code>Collection</code>
	 */
	public static <T extends Comparable<? super T>> Collection<T> ordenar(final Collection<T> colecao) {

		List<T> resultado = null;

		if (UtilColecao.isReferencia(colecao)) {

			resultado = new ArrayList<T>(colecao);

			Collections.sort(resultado);
		}

		return resultado;
	}

	/**
	 * Método responsável por ordenar uma coleção.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param colecao
	 * 
	 * @param comparator
	 * 
	 * @return <code>Collection</code>
	 */
	public static <T> Collection<T> ordenar(final Collection<T> colecao, final Comparator<? super T> comparator) {

		if (!UtilObjeto.isNull(colecao) && !UtilObjeto.isNull(comparator)) {

			Collections.sort(new ArrayList<T>(colecao), comparator);
		}

		return colecao;
	}

	/**
	 * Método responsável por obter um elemento de determinado índice.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param lista
	 * 
	 * @param indice
	 * 
	 * @return <code>T</code>
	 */
	public static <T> T getElementoDoIndice(final List<T> lista, final int indice) {

		T resultado = null;

		if (!UtilColecao.isEmpty(lista) && indice >= 0 && indice < lista.size()) {

			resultado = lista.get(indice);
		}

		return resultado;
	}

	/**
	 * Método responsável por obter um elemento de determinado índice.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param colecao
	 * 
	 * @param indice
	 * 
	 * @return <code>T</code>
	 */
	public static <T> T getElementoDoIndice(final Collection<T> colecao, final int indice) {

		final List<T> lista = new ArrayList<T>(colecao);

		return UtilColecao.getElementoDoIndice(lista, indice);
	}

	/**
	 * Método responsável por obter um elemento da última posição.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param colecao
	 * 
	 * @return <code>T</code>
	 */
	public static <T> T getElementoDoUltimoIndice(final Collection<T> colecao) {

		final List<T> lista = new ArrayList<T>(colecao);

		final int indice = ( lista.size() - 1 );

		return UtilColecao.getElementoDoIndice(lista, indice);
	}

	/**
	 * Método responsável por obter o tamanho da coleção.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param colecao
	 * 
	 * @return <code>int</code>
	 */
	public static int getTamanho(final Collection<?> colecao) {

		int resultado = 0;

		if (UtilColecao.isReferencia(colecao)) {

			resultado = colecao.size();
		}

		return resultado;
	}

	/**
	 * Método responsável por verificar se a coleção está vazia.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param colecao
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isEmpty(final Collection<?> colecao) {

		return ( colecao == null || UtilColecao.getTamanho(colecao) == 0 );
	}

	/**
	 * Método responsável por verificar se a coleção é diferente de null.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param objeto
	 * 
	 * @return <code>boolean</code>
	 */
	private static boolean isReferencia(final Object objeto) {

		return !UtilObjeto.isNull(objeto);
	}
}
