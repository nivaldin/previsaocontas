package br.com.previsaocontas.services;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.previsaocontas.dao.UsuarioDAOImpl;
import br.com.previsaocontas.exception.WarningException;
import br.com.previsaocontas.model.Usuario;

@Service
@Transactional(rollbackFor = Throwable.class)
public class UsuarioServiceImpl {

	@Autowired
	private UsuarioDAOImpl usuarioDAOImpl;

	public void salvar(Usuario usuario) {
		usuarioDAOImpl.salvar(usuario);
	}

	public Usuario buscaUsuario(String usuario, String senha) throws WarningException {

		try {
			return usuarioDAOImpl.buscaUsuario(usuario, senha);
		} catch (NonUniqueResultException e) {
			throw new WarningException("Mais de um usuário encontrado!");
		} catch (NoResultException e) {
			throw new WarningException("Usuário ou senha inválidos!");
		}
	}

	public Usuario obter(Long id) {
		return usuarioDAOImpl.obter(id);
	}

}
