package br.com.previsaocontas.utilitarios;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * <p>
 * <b>Title:</b> UtilString
 * </p>
 * 
 * <p>
 * <b>Description:</b> Classe utilitária responsável por manipular objetos <code>String</code>.
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
public final class UtilString {

	/** Atributo equalsBuilder. */
	private static final EqualsBuilder	equalsBuilder;

	static {

		equalsBuilder = new EqualsBuilder();
	}

	/**
	 * Responsável pela criação de novas instâncias desta classe.
	 */
	private UtilString() {

		super();
	}

	/**
	 * Método responsável por verificar se existe referencia armazenada na variável.
	 * 
	 * @param string
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isNotEmptyNull(final String string) {

		return !UtilString.isEmptyNull(string);
	}

	/**
	 * Método responsável por verificar se existe referencia armazenada na variável.
	 * 
	 * @param string
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isEmptyNull(final String string) {

		return UtilString.isEmpty(string) || UtilString.isEquals(string, "null");
	}

	/**
	 * Método responsável por verificar se existe referencia armazenada na variável.
	 * 
	 * @param string
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isEmpty(final String string) {

		final boolean isEmpty = UtilString.equalsBuilder.append(string, UtilString.empty()).isEquals() || UtilObjeto.isNull(string);

		UtilString.equalsBuilder.reset();

		return isEmpty;
	}

	/**
	 * Método responsável por verificar se existe referencia armazenadas nas variáveis.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param strings
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isEmpty(final String... strings) {

		if (UtilObjeto.isNull((Object[]) strings)) {

			return Boolean.TRUE;
		}

		for (final String string : strings) {

			if (UtilString.isEmpty(string)) {

				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * Método responsável por verificar se os valores são iguais.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param principal
	 * 
	 * @param texto
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isEquals(final String principal, final String texto) {

		final boolean isEquals = UtilString.equalsBuilder.append(principal.trim(), texto.trim()).isEquals();

		UtilString.equalsBuilder.reset();

		return isEquals;
	}

	/**
	 * Método responsável por transformar um valor em caixa alta.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param string
	 * 
	 * @return <code>String</code>
	 */
	public static String maiuscula(final String string) {

		String strMaiuscula = string;

		if (!UtilString.isEmpty(string)) {

			strMaiuscula = string.toUpperCase();
		}

		return strMaiuscula;
	}

	/**
	 * Método responsável por transformar um valor em caixa baixa.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param string
	 * 
	 * @return <code>String</code>
	 */
	public static String minuscula(final String string) {

		String strMinuscula = string;

		if (!UtilString.isEmpty(string)) {

			strMinuscula = string.toLowerCase();
		}

		return strMinuscula;
	}

	/**
	 * Método responsável por separar uma string de acordo com o delimitador informado.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param string
	 * 
	 * @param delimitador
	 * 
	 * @return <code>List</code>
	 */
	public static List<String> split(final String string, final String delimitador) {

		final List<String> result = new ArrayList<String>();

		if (!UtilString.isEmpty(string, delimitador)) {

			final StringTokenizer st = new StringTokenizer(string, delimitador);

			while (st.hasMoreTokens()) {

				result.add(st.nextToken());
			}
		}

		return result;
	}

	/**
	 * Método responsável por remover acentuação de uma string.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param string
	 * 
	 * @return <code>String</code>
	 */
	public static String removerAcentuacao(final String string) {

		String result = string;

		if (!UtilString.isEmpty(string)) {

			result = UtilString.substituir(result, "[ÁÀÂÃ]", "A");
			result = UtilString.substituir(result, "[áàâãª]", "a");
			result = UtilString.substituir(result, "[ÉÈÊ]", "E");
			result = UtilString.substituir(result, "[éèê]", "e");
			result = UtilString.substituir(result, "[ÍÌÎÏ]", "I");
			result = UtilString.substituir(result, "[íìîï]", "i");
			result = UtilString.substituir(result, "[ÓÒÔÕÖ]", "O");
			result = UtilString.substituir(result, "[óòôõºö]", "o");
			result = UtilString.substituir(result, "[ÚÙÛÜ]", "U");
			result = UtilString.substituir(result, "[úùûü]", "u");
			result = UtilString.substituir(result, "[Ç]", "C");
			result = UtilString.substituir(result, "[ç]", "c");
			result = UtilString.substituir(result, "[~\\d^Ž`]", "");
			result = UtilString.substituir(result, "[\\dš]", "");
		}

		return string;
	}

	/**
	 * Método responsável por remover caracteres de uma string, deixando apenas digitos.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param string
	 * 
	 * @return <code>String</code>
	 */
	public static String removerCaracteres(final String string) {

		String result = string;

		if (!UtilString.isEmpty(string)) {

			result = UtilString.remover(string, "[^0-9]");
		}

		return result;
	}

	/**
	 * Método responsável por remover valores de uma string.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param srcString
	 * 
	 * @param localizar
	 * 
	 * @return <code>String</code>
	 */
	public static String remover(final String srcString, final String localizar) {

		String result = srcString;

		if (!UtilString.isEmpty(srcString, localizar)) {

			result = srcString.replaceAll(localizar, "");
		}

		return result;
	}

	/**
	 * Método responsável por substituir um valor da string.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param srcString
	 * 
	 * @param localizar
	 * 
	 * @param novaString
	 * 
	 * @return <code>String</code>
	 */
	public static String substituir(final String srcString, final String localizar, final String novaString) {

		String result = srcString;

		if (!UtilString.isEmpty(srcString, localizar)) {

			result = srcString.replaceAll(localizar, novaString);
		}

		return result;
	}

	/**
	 * Método responsável por substituir uma valor da string de acordo as condições informadas.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param condicao
	 * 
	 * @param srcString
	 * 
	 * @param localizar
	 * 
	 * @param seVerdadeiro
	 * 
	 * @param seFalso
	 * 
	 * @return <code>String</code>
	 */
	public static String substituirSe(final boolean condicao, final String srcString, final String localizar, final String seVerdadeiro, final String seFalso) {

		String result = srcString;

		if (condicao) {

			result = UtilString.substituir(srcString, localizar, seVerdadeiro);

		} else {

			result = UtilString.substituir(srcString, localizar, seFalso);
		}

		return result;
	}

	/**
	 * Método responsável por obter a quantidade de caracteres da string.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @param string
	 * 
	 * @return <code>int</code>
	 */
	public static int getTamanho(final String string) {

		return !UtilString.isEmpty(string) ? string.length() : 0;
	}

	/**
	 * Método responsável por obter uma string vazia.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @return <code>String</code>
	 */
	public static String empty() {

		return StringUtils.EMPTY;
	}

	/**
	 * Método responsável por obter uma string representando uma nova linha.
	 * 
	 * @author Bruno Zafalão
	 * 
	 * @return <code>String</code>
	 */
	public static String newLine() {

		return "\n";
	}

	/**
	 * Método responsável por por verificar se a String contém digito.
	 * 
	 * @author Silvânio Júnior
	 * 
	 * @param valor
	 * @return
	 */
	public static boolean contemNumero(final String valor) {

		final char[] dados = valor.toCharArray();

		for (final char item : dados) {

			return ( Character.isDigit(item) );

		}

		return false;

	}

	/**
	 * Método responsável por por verificar se a String contém digito.
	 * 
	 * @author Silvânio Júnior
	 * 
	 * @param valor
	 * @return
	 */
	public static boolean isNumerico(final String valor) {

		final char[] dados = valor.toCharArray();

		int contador = 0;

		for (final char item : dados) {

			if (Character.isDigit(item)) {

				contador++;
			}
		}

		return contador == valor.length();
	}

	/**
	 * Método responsável por verificar se o metodo é decimal, recebendo uma String
	 * 
	 * @author Ezequiel Bispo Nunes
	 * 
	 * @param valor
	 * 
	 * @return <code>boolean</code>
	 */
	public static boolean isDecimal(final String valor) {

		try {

			Double.parseDouble(valor);

			return true;

		} catch (Exception e) {

			return false;
		}

	}
}
