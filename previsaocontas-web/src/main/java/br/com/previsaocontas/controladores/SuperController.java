package br.com.previsaocontas.controladores;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class SuperController {

    public void adicionaMensagem(String mensagem, Severity level) {
	adicionaMensagemJSF(mensagem, level);
    }

    public void adicionaMensagem(Exception ex, Severity level) {

	String mensagem = ex.getCause().toString();

	if (level == FacesMessage.SEVERITY_ERROR || level == FacesMessage.SEVERITY_FATAL) {
	    ex.printStackTrace();
	}
	adicionaMensagemJSF(mensagem, level);

    }

    private void adicionaMensagemJSF(String mensagem, Severity level) {
	if (level.equals(FacesMessage.SEVERITY_FATAL))
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Erro Fatal", mensagem));
	if (level.equals(FacesMessage.SEVERITY_ERROR))
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", mensagem));
	if (level.equals(FacesMessage.SEVERITY_WARN))
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", mensagem));
	if (level.equals(FacesMessage.SEVERITY_INFO))
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mensagem));
    }

}
