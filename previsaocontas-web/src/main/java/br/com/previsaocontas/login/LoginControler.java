package br.com.previsaocontas.login;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;

import br.com.previsaocontas.controladores.SuperController;
import br.com.previsaocontas.exception.WarningException;
import br.com.previsaocontas.model.Usuario;
import br.com.previsaocontas.services.UsuarioServiceImpl;

@ManagedBean
@ViewScoped
public class LoginControler extends SuperController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario usuario;

    @ManagedProperty(value = "#{usuarioServiceImpl}")
    private UsuarioServiceImpl usuarioServiceImpl;

    public UsuarioServiceImpl getUsuarioServiceImpl() {
	return usuarioServiceImpl;
    }

    public void setUsuarioServiceImpl(UsuarioServiceImpl usuarioServiceImpl) {
	this.usuarioServiceImpl = usuarioServiceImpl;
    }

    @PostConstruct
    public void iniciar() {
	this.setUsuario(new Usuario());
    }

    public Usuario getUsuario() {

	HttpSession session = SessionBean.getSession();

	if (this.usuario == null && (Usuario) session.getAttribute("usuario") == null) {
	    this.setUsuario(new Usuario());
	}

	if ((Usuario) session.getAttribute("usuario") != null) {
	    this.setUsuario((Usuario) session.getAttribute("usuario"));
	}

	return usuario;
    }

    public void setUsuario(Usuario usuario) {
	this.usuario = usuario;
    }

    // validate login
    public void validaLogin() {

	try {
	    this.setUsuario(usuarioServiceImpl.buscaUsuario(this.getUsuario().getLogin(), this.getUsuario().getSenha()));
	} catch (WarningException e) {
	    adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
	    return;
	} catch (Exception e) {
	    adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
	    return;
	}
	HttpSession session = SessionBean.getSession();
	session.setAttribute("usuario", this.getUsuario());
    }

    // logout event, invalidate session
    public String logout() {
	HttpSession session = SessionBean.getSession();
	session.invalidate();
	// session.setAttribute("usuario", null);
	return "inicial.xhtml?faces-redirect=true";

    }

}
