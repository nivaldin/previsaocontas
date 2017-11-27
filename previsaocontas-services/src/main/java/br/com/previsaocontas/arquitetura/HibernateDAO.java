package br.com.previsaocontas.arquitetura;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TransactionRequiredException;

import org.hibernate.Criteria;

import br.com.previsaocontas.utilitarios.UtilColecao;
import br.com.previsaocontas.utilitarios.UtilObjeto;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

@SuppressWarnings({ "unchecked", "restriction" })
public abstract class HibernateDAO<E extends Entidade> {

	public HibernateDAO() {
	}

	@PersistenceContext
	private EntityManager em;

	public EntityManager getEm() {
	    return em;
	}

	public void setEm(EntityManager em) {
	    this.em = em;
	}

	public E salvar(E entidade) {

		if (!UtilObjeto.isNull(entidade)) {

			if (UtilObjeto.isNull(entidade.getId())) {

				this.em.persist(entidade);
			} else {

				this.em.merge(entidade);

			}

		}

		return entidade;
	}

	public void remover(final E entidade) throws IllegalArgumentException, TransactionRequiredException, EntityNotFoundException {

		if (!UtilObjeto.isNull(entidade)) {

			this.em.remove(this.em.getReference(entidade.getClass(), entidade.getId()));

		}
	}

	public void removerTodos(final Collection<E> entidades) throws TransactionRequiredException, IllegalArgumentException, EntityNotFoundException {

		if (!UtilObjeto.isNull(entidades)) {

			for (final E entidade : entidades) {

				this.remover(entidade);
			}
		}
	}

	public E obter(Long identificador) {
	    	
		E resultado = null;

		if (!UtilObjeto.isNull(identificador)) {

			final Class<E> tipo = this.getEntidadeClass();

			resultado = this.em.find(tipo, identificador);
			
		}
		
		return resultado;
	}

	protected E obter(final Criteria criteria) {

		final List<E> result = criteria.list();

		return UtilColecao.isEmpty(result) ? null : this.getEntidadeClass().cast(result.get(0));
	}

	protected Class<E> getEntidadeClass() {

		final Type type[] = ((ParameterizedTypeImpl) this.getClass().getGenericSuperclass()).getActualTypeArguments();

		return (Class<E>) type[0];
	}

//	protected Criteria novoCriteria() {
//
//		final Class<E> clazz = this.getEntidadeClass();
//
//		return ((Session) em.getDelegate()).createCriteria(clazz);
//	}

}
