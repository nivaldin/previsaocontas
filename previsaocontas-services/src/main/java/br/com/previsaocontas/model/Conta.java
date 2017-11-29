package br.com.previsaocontas.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.previsaocontas.arquitetura.Entidade;
import br.com.previsaocontas.enums.EnumSimNao;
import br.com.previsaocontas.enums.EnumStatusConta;
import br.com.previsaocontas.enums.EnumTipoConta;

@Entity
@Table(name = "CONTAS")
public class Conta extends Entidade {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false, length = 50)
	private String descricao;

	@Column(nullable = false)
	private Double valor;

	@Column
	private Integer dia_vencimento;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EnumTipoConta tipo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EnumSimNao flag_comum;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EnumStatusConta status;

	@Column
	private Long numr_agrupador;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date data_mes;

	@Column
	private Integer qtde_parcelas;

	@Column
	private Integer numr_parcela;

	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "id_conta")
	private Conta contaPai;
	
	@Transient
	private Double valorParciais;
	
//	@OneToMany(mappedBy = "contaPai", fetch = FetchType.EAGER ,cascade = CascadeType.ALL)
//	private List<Conta> contasFilhas;
	
	@PrePersist
	public void onCreateConta() {
		this.setStatus(EnumStatusConta.A);
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao
	 *            the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the valor
	 */
	public Double getValor() {
		return valor;
	}

	/**
	 * @param valor
	 *            the valor to set
	 */
	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Integer getDia_vencimento() {
		return dia_vencimento;
	}

	public void setDia_vencimento(Integer dia_vencimento) {
		this.dia_vencimento = dia_vencimento;
	}

	public EnumTipoConta getTipo() {
		return tipo;
	}

	public void setTipo(EnumTipoConta tipo) {
		this.tipo = tipo;
	}

	public EnumSimNao getFlag_comum() {
		return flag_comum;
	}

	public void setFlag_comum(EnumSimNao flag_comum) {
		this.flag_comum = flag_comum;
	}

	public EnumStatusConta getStatus() {
		return status;
	}

	public void setStatus(EnumStatusConta status) {
		this.status = status;
	}

	public Long getNumr_agrupador() {
		return numr_agrupador;
	}

	public void setNumr_agrupador(Long numr_agrupador) {
		this.numr_agrupador = numr_agrupador;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getData_mes() {
		return data_mes;
	}

	public void setData_mes(Date data_mes) {
		this.data_mes = data_mes;
	}

	public Integer getQtde_parcelas() {
		return qtde_parcelas;
	}

	public void setQtde_parcelas(Integer qtde_parcelas) {
		this.qtde_parcelas = qtde_parcelas;
	}

	public Integer getNumr_parcela() {
		return numr_parcela;
	}

	public void setNumr_parcela(Integer numr_parcela) {
		this.numr_parcela = numr_parcela;
	}

	public Conta getContaPai() {
	    return contaPai;
	}

	public void setContaPai(Conta contaPai) {
	    this.contaPai = contaPai;
	}

	
//	public List<Conta> getContasFilhas() {
//	    return contasFilhas;
//	}
//
//	public void setContasFilhas(List<Conta> contasFilhas) {
//	    this.contasFilhas = contasFilhas;
//	}

	public Double getValorParciais() {
		return valorParciais;
	}

	public void setValorParciais(Double valorParciais) {
		this.valorParciais = valorParciais;
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
