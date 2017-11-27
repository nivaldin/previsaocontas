package br.com.previsaocontas.arquitetura;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * <b>Title:</b> DAO
 * </p>
 * 
 * <p>
 * <b>Description:</b> Interface responsável por definir as funções padrão da camada de persistência.
 * </p>
 * 
 * <p>
 * <b>Company:</b> ITSS Factory
 * </p>
 * 
 * @author Silvânio Júnior
 * 
 * @version 1.0.0
 */
public interface DAO<E extends Entidade> extends Serializable{

	/** Atributo UNIT_NAME. */
	String	UNIT_NAME	= "default";

	/**
	 * Método responsável por inserir uma entidade no sistema.
	 * 
	 * @author Silvânio Júnior
	 * 
	 * @param entidade
	 * 
	 * @return <code>String</code>
	 */
	E salvar(final E entidade);


	/**
	 * Método responsável por remover uma entidade do sistema.
	 * 
	 * @author Silvânio Júnior
	 * 
	 * @param entidade
	 * 
	 * @return <code>String</code>
	 */
	void remover(final E entidade);

	/**
	 * Método responsável por remover uma lista de entidades do sistema.
	 * 
	 * @author Silvânio Júnior
	 * 
	 * @param entidades
	 * 
	 * @return <code>String</code>
	 */
	void removerTodos(final Collection<E> entidades);

	/**
	 * Método responsável por obter uma entidade pelo seu identificador.
	 * 
	 * @author Silvânio Júnior
	 * 
	 * @param identificador
	 * 
	 * @return <code>E</code>
	 */
	E obter(Long identificador);

	/**
	 * Método responsável por consultar uma lista de entidades no sistema.
	 * 
	 * @author Silvânio Júnior
	 * 
	 * @param entidade
	 * 
	 * @return <code>Collection</code>
	 */
	Collection<E> consultar(final E entidade);

	/**
	 * Método responsável por listar as entidades do sistema.
	 * 
	 * @author Silvânio Júnior
	 * 
	 * @return <code>Collection</code>
	 */
	Collection<E> listar();

	/**
	 * Método responsável por obter por atributo
	 *
	 * @author Silvânio Júnior
	 *
	 * @param atributo
	 * 
	 * @param valor
	 * 
	 * @return <code>E</code>
	 */
	public Collection<E> obterPorAtributo(String atributo, Serializable valor);

}
