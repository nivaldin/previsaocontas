package br.com.previsaocontas.services;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.previsaocontas.dao.UsuarioDAOImpl;
import br.com.previsaocontas.exception.ErrorException;
import br.com.previsaocontas.exception.WarningException;
import br.com.previsaocontas.model.Usuario;

@Service
@Transactional(rollbackFor = Throwable.class)
public class UsuarioServiceImpl {

	@Autowired
	private UsuarioDAOImpl usuarioDAOImpl;

	public void salvar(Usuario usuario) throws ErrorException {
		try {
			usuarioDAOImpl.salvar(usuario);
		} catch (Exception e) {
			throw new ErrorException(e.getMessage());
		}
	}

	public Usuario buscaUsuario(String usuario, String senha) throws WarningException, ErrorException {

		try {
			return usuarioDAOImpl.buscaUsuario(usuario, senha);
		} catch (NonUniqueResultException e) {
			throw new WarningException("Mais de um usuário encontrado, verifique!");
		} catch (NoResultException e) {
			throw new WarningException("Usuário ou senha inválidos, verifique!");
		}
	}

	public Usuario obter(Long id) throws ErrorException {
		try {
			return usuarioDAOImpl.obter(id);
		} catch (Exception e) {
			throw new ErrorException(e.getMessage());
		}
	}

}
