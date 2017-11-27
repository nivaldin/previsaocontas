package br.com.previsaocontas.dao;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.previsaocontas.arquitetura.HibernateDAO;
import br.com.previsaocontas.model.Usuario;
import br.com.previsaocontas.utilitarios.UtilSecurity;

@Repository
public class UsuarioDAOImpl extends HibernateDAO<Usuario> {

    public Usuario buscaUsuario(String usuario, String senha) throws NoResultException, NonUniqueResultException   {

	String jpql = "";
	jpql = "select u from Usuario u where login = :usuario and senha = :senha";
	
	Query query = getEm().createQuery(jpql);
	query.setParameter("usuario", usuario);
	query.setParameter("senha", UtilSecurity.cript(senha));
	
	return (Usuario) query.getSingleResult();

    }

}
