package br.com.previsaocontas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.previsaocontas.arquitetura.Entidade;

@Entity
@Table(name = "USUARIOS")
public class Usuario extends Entidade {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, length = 20)
	private String login;

	@Column(nullable = false, length = 50)
	private String senha;

	@Column(nullable = false)
	private Double saldo;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}
	
	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = super.hashCode();
	    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (!super.equals(obj))
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    Conta other = (Conta) obj;
	    if (this.getId() == null) {
		if (other.getId() != null)
		    return false;
	    } else if (!this.getId().equals(other.getId()))
		return false;
	    return true;
	}

}
