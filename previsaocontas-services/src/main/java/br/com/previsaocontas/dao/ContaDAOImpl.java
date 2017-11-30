package br.com.previsaocontas.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.previsaocontas.arquitetura.HibernateDAO;
import br.com.previsaocontas.enums.EnumStatusConta;
import br.com.previsaocontas.model.Conta;
import br.com.previsaocontas.model.Usuario;
import br.com.previsaocontas.utilitarios.UtilObjeto;

@Repository
public class ContaDAOImpl extends HibernateDAO<Conta> {

	@SuppressWarnings("unchecked")
	public List<Conta> obterPorAgrupador(Long valor) {

		String jpql = "";
		jpql = "select c from Conta c where numr_agrupador = :valor";

		Query query = getEm().createQuery(jpql);
		query.setParameter("valor", valor);

		return (List<Conta>) query.getResultList();

	}

	public Long obterProximoAgrupador(Usuario usuario) {

		String jpql = "";
		jpql = "select max(numr_agrupador) from Conta where usuario = :usuario";

		Query query = getEm().createQuery(jpql);
		query.setParameter("usuario", usuario);
		Long codigo = (Long) query.getSingleResult();

		if (UtilObjeto.isNull(codigo)) {
			codigo = 0L;
		}
		return codigo + 1;

	}

	@SuppressWarnings("unchecked")
	public List<Conta> buscaContaMes(Integer mesSelecionado, Integer anoSelecionado, Usuario usuario) {

		Calendar dataI = Calendar.getInstance();
		Calendar dataF = Calendar.getInstance();

		dataI.set(anoSelecionado, mesSelecionado - 1, 1, 0, 0, 0);
		dataF.set(anoSelecionado, mesSelecionado - 1, 1, 23, 59, 59);

		dataF.add(Calendar.MONTH, 1);
		dataF.add(Calendar.DAY_OF_MONTH, -1);

		String jpql = "";
		jpql += "select distinct(c) from Conta c";
		jpql += " where usuario = :usuario ";
		jpql += "   and contaPai is null";
		jpql += "   and data_mes >= :dataI";
		jpql += "   and data_mes <= :dataF";
		jpql += " order by tipo, flag_comum, data_registro desc";

		Query query = getEm().createQuery(jpql);
		query.setParameter("usuario", usuario);
		query.setParameter("dataI", dataI.getTime());
		query.setParameter("dataF", dataF.getTime());

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<Conta> buscaContasAcumuladoAberto(Integer mesSelecionado, Integer anoSelecionado, Usuario usuario) {

		Calendar dataF = Calendar.getInstance();
		dataF.set(anoSelecionado, mesSelecionado - 1, 1, 23, 59, 59);

		dataF.add(Calendar.MONTH, 1);
		dataF.add(Calendar.DAY_OF_MONTH, -1);

		String jpql = "";
		jpql += "select distinct(c) from Conta c";
		jpql += " where usuario = :usuario ";
		jpql += "   and contaPai is null";
		jpql += "   and data_mes <= :dataF";
		jpql += "   and status = :status";
		jpql += " order by tipo, flag_comum, data_registro";

		Query query = getEm().createQuery(jpql);
		query.setParameter("usuario", usuario);
		query.setParameter("dataF", dataF.getTime());
		query.setParameter("status", EnumStatusConta.A);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<Conta> buscaContasAgrupador(Long numr_agrupador, Usuario usuario) {

		String jpql = "";
		jpql = "select from Conta where usuario = :usuario and numr_agrupador = :numr_agrupador";

		Query query = getEm().createQuery(jpql);
		query.setParameter("usuario", usuario);
		query.setParameter("numr_agrupador", numr_agrupador);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<Conta> buscaContasFilhas(Long idPai) {

		String jpql = "";
		jpql = "select c from Conta c where id_conta = :id_conta order by data_registro desc";

		Query query = getEm().createQuery(jpql);
		query.setParameter("id_conta", idPai);
		return query.getResultList();

	}

}
