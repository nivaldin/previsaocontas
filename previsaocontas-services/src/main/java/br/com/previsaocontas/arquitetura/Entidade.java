package br.com.previsaocontas.arquitetura;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.com.previsaocontas.utilitarios.UtilObjeto;
import br.com.previsaocontas.utilitarios.UtilString;

@MappedSuperclass
public abstract class Entidade implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	private String				uuid				= UUID.randomUUID().toString();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Date data_registro;

	@PrePersist
	public void onCreate() {

		this.setData_registro(new Date());
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the data_registro
	 */
	public Date getData_registro() {
		return data_registro;
	}

	/**
	 * @param data_registro
	 *            the data_registro to set
	 */
	public void setData_registro(Date data_registro) {
		this.data_registro = data_registro;
	}
	
	public String getUuid() {

		return this.uuid;
	}
	
	
	@Transient
	public void setUuid(final String uuid) {

		if (UtilString.isEmpty(uuid)) {
			return;
		}

		this.uuid = uuid;
	}


	@Override
	public int hashCode() {

		final int PRIMO = 31;

		return UtilObjeto.isNull(this.getId()) ? 0 : new HashCodeBuilder(this.getId() % 2 == 0 ? this.getId().intValue() + 1 : this.getId().intValue(), PRIMO).toHashCode();
	}


	@Override
	public boolean equals(final Object object) {

		if (object == this) {

			return Boolean.TRUE;
		}

		if (UtilObjeto.isNull(object) || !( object instanceof Entidade ) || object.getClass() != this.getClass()) {

			return Boolean.FALSE;
		}

		final Entidade entidade = (Entidade) object;

		if (!UtilObjeto.isNull(entidade.getId()) && !UtilObjeto.isNull(this.getId())) {

			return UtilString.isEquals(this.getId().toString(), entidade.getId().toString());
		} else {

			return UtilString.isEquals(this.getUuid(), entidade.getUuid());
		}
	}

	

}
