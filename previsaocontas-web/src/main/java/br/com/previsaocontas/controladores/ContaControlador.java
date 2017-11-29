package br.com.previsaocontas.controladores;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.com.previsaocontas.enums.EnumSimNao;
import br.com.previsaocontas.enums.EnumStatusConta;
import br.com.previsaocontas.enums.EnumTipoConta;
import br.com.previsaocontas.exception.ErrorException;
import br.com.previsaocontas.exception.WarningException;
import br.com.previsaocontas.model.Conta;
import br.com.previsaocontas.model.Usuario;
import br.com.previsaocontas.services.ContaServiceImpl;
import br.com.previsaocontas.services.UsuarioServiceImpl;
import br.com.previsaocontas.utilitarios.UtilNumero;
import br.com.previsaocontas.utilitarios.UtilObjeto;

@ManagedBean
@SessionScoped
public class ContaControlador extends SuperController implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{contaServiceImpl}")
	private ContaServiceImpl contaServiceImpl;

	@ManagedProperty(value = "#{usuarioServiceImpl}")
	private UsuarioServiceImpl usuarioServiceImpl;

	private Conta conta;

	private List<Conta> contas;
	private List<Conta> contasDespesa;
	private List<Conta> contasReceita;

	private Usuario usuario;

	private Integer numr_parcelas = 0;

	private Conta contaFilha;

	private List<Conta> parcelas;

	private Integer mesSelecionado;

	private Integer anoSelecionado;

	private Double totalDespesaMes = 0.0;
	private Double totalReceitaMes = 0.0;

	private Double totalDespesaPrevistoMes = 0.0;
	private Double totalReceitaPrevistoMes = 0.0;

	private Double totalDespesaPrevistoAcumulado = 0.0;
	private Double totalReceitaPrevistoAcumulado = 0.0;

	private Integer indexTabMes = 0;

	public void limpaUsuario() {
		this.setUsuario(null);
	}

	public String validaEntrada() {
		try {

			if (this.getUsuario().getId() != null) {
				return this.abreInicial();
			}
			// } else {
			// return this.abreLogin();
			// }

		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}
		return null;

	}

	public void atualizaSaldo() {

		try {

			usuarioServiceImpl.salvar(this.getUsuario());
			adicionaMensagem("Salvo com Sucesso!", FacesMessage.SEVERITY_INFO);

		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public void atualizaInicio() throws ErrorException, WarningException {
		this.buscaContasMes();
		this.preparaInclusao();
	}

	public void excluir() {
		try {

			this.contaServiceImpl.exluir(this.getConta());
			this.atualizaInicio();

			adicionaMensagem("Excluído com sucesso!", FacesMessage.SEVERITY_INFO);
		} catch (WarningException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
		} catch (ErrorException e) {
			try {
				this.buscaContasMes();
			} catch (WarningException ex) {
				adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
			} catch (ErrorException ex) {
				adicionaMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
			}
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public void buscaContasMesTab() {

		try {

			this.iniciaMesAno();

			this.setIndexTabMes(this.mesSelecionado - 1);

			this.setContas(contaServiceImpl.buscaContaMes(mesSelecionado, anoSelecionado, this.getUsuario()));

			this.calculaResumoMes();

			this.buscaUsuario();

			this.preparaInclusao();

		} catch (WarningException ex) {
			adicionaMensagem(ex.getMessage(), FacesMessage.SEVERITY_WARN);
		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	private void buscaContasMes() throws ErrorException, WarningException {

		this.iniciaMesAno();

		this.setIndexTabMes(this.mesSelecionado - 1);

		this.setContas(contaServiceImpl.buscaContaMes(mesSelecionado, anoSelecionado, this.getUsuario()));

		this.calculaResumoMes();

		this.buscaUsuario();

	}

	private void iniciaMesAno() {

		if (UtilObjeto.isNull(mesSelecionado) || UtilObjeto.isNull(anoSelecionado)) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			this.mesSelecionado = month + 1;
			this.anoSelecionado = year;

		}
	}

	private void calculaResumoMes() throws ErrorException {

		this.setTotalDespesaMes(0.0);
		this.setTotalReceitaMes(0.0);
		this.setTotalDespesaPrevistoMes(0.0);
		this.setTotalReceitaPrevistoMes(0.0);
		this.setTotalDespesaPrevistoAcumulado(0.0);
		this.setTotalReceitaPrevistoAcumulado(0.0);

		for (Conta conta : this.getContas()) {

			// Despesas Mes
			if (conta.getTipo().equals(EnumTipoConta.D)) {

				this.totalDespesaMes += conta.getValor();

				if (conta.getStatus().equals(EnumStatusConta.A)) {

					Double totalParciais = contaServiceImpl.somaParciais(conta);
					if (totalParciais == conta.getValor()) {

						totalParciais = 0.0;

					}
					this.totalDespesaPrevistoMes += conta.getValor() - UtilNumero.round(totalParciais, 2);

				}

			}
			// Receitas Mes
			if (conta.getTipo().equals(EnumTipoConta.R)) {

				this.totalReceitaMes += conta.getValor();

				if (conta.getStatus().equals(EnumStatusConta.A)) {

					Double totalParciais =contaServiceImpl.somaParciais(conta);
					if (totalParciais == conta.getValor()) {

						totalParciais = 0.0;

					}
					this.totalReceitaPrevistoMes += conta.getValor() - UtilNumero.round(totalParciais, 2);

				}

			}

		}

		List<Conta> contas = contaServiceImpl.buscaContasAcumuladoAberto(mesSelecionado, anoSelecionado,
				this.getUsuario());
		for (Conta conta : contas) {

			if (conta.getTipo().equals(EnumTipoConta.D)) {

				Double totalParciais =contaServiceImpl.somaParciais(conta);
				if (totalParciais == conta.getValor()) {

					totalParciais = 0.0;

				}
				this.totalDespesaPrevistoAcumulado += conta.getValor() - UtilNumero.round(totalParciais, 2);

			}

			if (conta.getTipo().equals(EnumTipoConta.R)) {

				Double totalParciais =contaServiceImpl.somaParciais(conta);
				if (totalParciais == conta.getValor()) {

					totalParciais = 0.0;

				}
				this.totalReceitaPrevistoAcumulado += conta.getValor() - UtilNumero.round(totalParciais, 2);

			}
		}

	}

	public void baixarContaFilha() {

		try {

			this.contaServiceImpl.baixarConta(this.getContaFilha());

			this.buscaContasMes();
			this.preparaEdicao();

			adicionaMensagem("Conta Baixada", FacesMessage.SEVERITY_INFO);
		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
			this.preparaEdicao();
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
			this.preparaEdicao();

		}

	}

	public void abrirContaFilha() {

		try {

			this.contaServiceImpl.abrirConta(this.getContaFilha());
			this.buscaContasMes();
			this.preparaEdicao();

			adicionaMensagem("Conta Aberta", FacesMessage.SEVERITY_INFO);
		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
			this.preparaEdicao();
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public void baixarConta() {

		try {

			this.contaServiceImpl.baixarConta(this.getConta());

			this.buscaContasMes();

			adicionaMensagem("Conta Baixada", FacesMessage.SEVERITY_INFO);
		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
			this.preparaEdicao();
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public void abrirConta() {

		try {

			this.contaServiceImpl.abrirConta(this.getConta());
			this.buscaContasMes();

			adicionaMensagem("Conta Aberta", FacesMessage.SEVERITY_INFO);
		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
			this.preparaEdicao();
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public String abreLogin() {
		this.getUsuario();
		return "/login.xhtml?faces-redirect=true";
	}

	public String abreInicial() throws ErrorException, WarningException {

		this.atualizaInicio();
		return "/contas/inicial.xhtml?faces-redirect=true";
	}

	public String abreIncluir() {

		this.preparaInclusao();
		return "/contas/incluir.xhtml?faces-redirect=true";

	}

	public String abreAlterar() {

		this.preparaEdicao();
		return "/contas/incluir.xhtml?faces-redirect=true";

	}

	private void buscaUsuario() {
		try {
			this.setUsuario(usuarioServiceImpl.obter(this.getUsuario().getId()));
		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public void salvar() {

		try {

			this.getConta().setUsuario(this.getUsuario());
			this.getContaServiceImpl().salvar(this.getConta());
			this.preparaInclusao();
			this.buscaContasMes();

			adicionaMensagem("Salvo com Sucesso!", FacesMessage.SEVERITY_INFO);

		} catch (WarningException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
		} catch (ErrorException e) {
			try {
				this.buscaContasMes();
			} catch (WarningException ex) {
				adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
			} catch (ErrorException ex) {
				adicionaMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
			}
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public void salvarTodos() {

		try {

			this.contaServiceImpl.salvarTodos(this.getConta());

			this.preparaInclusao();
			this.buscaContasMes();

			adicionaMensagem("Salvo Todos com Sucesso!", FacesMessage.SEVERITY_INFO);
		} catch (ErrorException e) {
			try {
				this.buscaContasMes();
			} catch (WarningException ex) {
				adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
			} catch (ErrorException ex) {
				adicionaMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
			}
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public void excluirTodos() {

		try {

			List<Conta> contas = (List<Conta>) contaServiceImpl.obterPorAgrupador(this.getConta().getNumr_agrupador());

			for (Conta conta : contas) {
				if (conta.getStatus().equals(EnumStatusConta.B) ||contaServiceImpl.somaParciais(conta) > 0) {
					continue;
				}
				contaServiceImpl.exluir(conta);

			}

			this.buscaContasMes();
			this.preparaInclusao();

			adicionaMensagem("Excluido Todos com Sucesso!", FacesMessage.SEVERITY_INFO);
		} catch (WarningException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
		} catch (ErrorException e) {
			try {
				this.buscaContasMes();
			} catch (WarningException ex) {
				adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
			} catch (ErrorException ex) {
				adicionaMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
			}
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}
	}

	public List<EnumTipoConta> getListaTipo() {

		return Arrays.asList(EnumTipoConta.values());

	}

	public List<EnumStatusConta> getListaStatus() {

		return Arrays.asList(EnumStatusConta.A, EnumStatusConta.B);

	}

	public List<EnumSimNao> getListaFlagComum() {

		return Arrays.asList(EnumSimNao.values());

	}

	public void preparaInclusao() {

		this.setContaFilha(new Conta());
		this.parcelas = new ArrayList<Conta>();

		this.setConta(new Conta());

		this.iniciaMesAno();

		Calendar calendar = Calendar.getInstance();
		if (this.mesSelecionado - 1 != calendar.get(Calendar.MONTH)) {
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		}
		calendar.set(Calendar.MONTH, this.mesSelecionado - 1);
		calendar.set(Calendar.YEAR, this.anoSelecionado);

		this.getConta().setData_mes(calendar.getTime());

		this.getConta().setFlag_comum(EnumSimNao.N);
		this.numr_parcelas = 0;

	}

	public void preparaEdicao() {
		try {

			this.parcelas = new ArrayList<Conta>();

			this.setConta(contaServiceImpl.obter(this.getConta().getId()));

			this.novaParcial();

		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	private void novaParcial() {

		this.setContaFilha(new Conta());
		this.getContaFilha().setData_registro(new Date());
		this.getContaFilha().setData_mes(this.getConta().getData_mes());
		this.getContaFilha().setFlag_comum(this.getConta().getFlag_comum());
		this.getContaFilha().setTipo(this.getConta().getTipo());
		this.getContaFilha().setStatus(EnumStatusConta.A);
		this.getContaFilha().setUsuario(this.getUsuario());
		this.getContaFilha().setContaPai(this.getConta());

	}

	public void incluirParcial() {

		try {

			this.contaServiceImpl.validaContaFilha(this.getConta(), this.getContaFilha());

			this.getContaServiceImpl().salvar(this.getContaFilha());

			// this.buscaContasMes();
			this.preparaEdicao();

		} catch (WarningException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public List<Conta> buscaContasFilhas() {
		try {
			return this.getContaServiceImpl().buscaContasFilhas(this.getConta());
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
			return null;
		}
	}

	public void removerParcial() {

		try {

			if (this.getConta().getStatus().equals(EnumStatusConta.B)) {
				adicionaMensagem("Conta Baixada, verifique!", FacesMessage.SEVERITY_WARN);
				return;
			}

			if (this.getContaFilha().getStatus().equals(EnumStatusConta.B)) {
				adicionaMensagem("Conta Filha Baixada, verifique!", FacesMessage.SEVERITY_WARN);
				return;
			}

			// this.getConta().getContasFilhas().remove(this.getContaFilha());

			// this.getContaServiceImpl().salvar(this.getConta());

			this.getContaServiceImpl().exluir(getContaFilha());

			this.preparaEdicao();

		} catch (WarningException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
		} catch (ErrorException e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
		} catch (Exception e) {
			adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_FATAL);
		}

	}

	public String contaStyleClass(Conta conta) throws Exception {

		String styleClass = "";

		if (conta.getTipo().equals(EnumTipoConta.D)) {
			if (conta.getStatus().equals(EnumStatusConta.B)) {
				styleClass = "rowBaixadoD";
			} else {
				styleClass = "rowDespesa";

				if (contaServiceImpl.somaParciais(conta) > 0) {
					styleClass = "rowParciaisD";
				}

			}

		}
		if (conta.getTipo().equals(EnumTipoConta.R)) {

			if (conta.getStatus().equals(EnumStatusConta.B)) {
				styleClass = "rowBaixadoR";
			} else {
				styleClass = "rowReceita";

				if (contaServiceImpl.somaParciais(conta) > 0) {
					styleClass = "rowParciaisR";
				}

			}

		}

		return styleClass;
	}

	public String verificaVencimento(Conta conta) {

		if (conta == null) {
			return "";
		}
		if (conta.getDia_vencimento() == null) {
			return "";
		}

		if (conta.getStatus().equals(EnumStatusConta.B)) {
			return "";
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, mesSelecionado - 1);
		calendar.set(Calendar.YEAR, anoSelecionado);
		calendar.set(Calendar.DAY_OF_MONTH, conta.getDia_vencimento() - 1);

		String classe = "";
		if (calendar.getTime().before(new Date())) {
			classe = "colVencido";
		}
		return classe;

	}

	public String verificaFlagComum(Conta conta) {

		if (conta == null) {
			return "";
		}

		if (conta.getFlag_comum().equals(EnumSimNao.S)) {
			return "colFlagComum";
		} else {
			return "";
		}
	}

	public String verificaTipoConta(Conta conta) {

		if (conta == null) {
			return "";
		}

		if (conta.getTipo().equals(EnumTipoConta.D)) {
			return "colDespesa";
		} else if (conta.getTipo().equals(EnumTipoConta.R)) {
			return "colReceita";
		} else {
			return "";
		}
	}

	public Conta getContaFilha() {
		return contaFilha;
	}

	public void setContaFilha(Conta contaFilha) {
		this.contaFilha = contaFilha;
	}

	public ContaServiceImpl getContaServiceImpl() {
		return contaServiceImpl;
	}

	public void setContaServiceImpl(ContaServiceImpl contaServiceImpl) {
		this.contaServiceImpl = contaServiceImpl;
	}

	public UsuarioServiceImpl getUsuarioServiceImpl() {
		return usuarioServiceImpl;
	}

	public void setUsuarioServiceImpl(UsuarioServiceImpl usuarioServiceImpl) {
		this.usuarioServiceImpl = usuarioServiceImpl;
	}

	public List<Conta> getContasDespesa() {
		if (this.contasDespesa == null) {
			this.setContasDespesa(new ArrayList<Conta>());
		}
		return contasDespesa;
	}

	public void setContasDespesa(List<Conta> contasDespesa) {
		this.contasDespesa = contasDespesa;
	}

	public List<Conta> getContasReceita() {
		if (this.contasReceita == null) {
			this.setContasReceita(new ArrayList<Conta>());
		}
		return contasReceita;
	}

	public void setContasReceita(List<Conta> contasReceita) {
		this.contasReceita = contasReceita;
	}

	public Integer getIndexTabMes() {
		return indexTabMes;
	}

	public void setIndexTabMes(Integer indexTabMes) {
		this.indexTabMes = indexTabMes;
	}

	public Conta getConta() {
		if (this.conta == null) {
			this.setConta(new Conta());
		}
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public List<Conta> getContas() {
		if (this.contas == null) {
			this.setContas(new ArrayList<Conta>());
		}
		return contas;
	}

	public void setContas(List<Conta> contas) {
		this.contas = contas;
	}

	public Integer getMesSelecionado() {
		return mesSelecionado;
	}

	public void setMesSelecionado(Integer mesSelecionado) {
		this.mesSelecionado = mesSelecionado;
	}

	public Integer getAnoSelecionado() {
		return anoSelecionado;
	}

	public void setAnoSelecionado(Integer anoSelecionado) {
		this.anoSelecionado = anoSelecionado;
	}

	public Double getTotalDespesaMes() {
		return totalDespesaMes;
	}

	public void setTotalDespesaMes(Double totalDespesaMes) {
		this.totalDespesaMes = totalDespesaMes;
	}

	public Double getTotalReceitaMes() {
		return totalReceitaMes;
	}

	public void setTotalReceitaMes(Double totalReceitaMes) {
		this.totalReceitaMes = totalReceitaMes;
	}

	public Double getTotalDespesaPrevistoMes() {
		return totalDespesaPrevistoMes;
	}

	public void setTotalDespesaPrevistoMes(Double totalDespesaPrevistoMes) {
		this.totalDespesaPrevistoMes = totalDespesaPrevistoMes;
	}

	public Double getTotalReceitaPrevistoMes() {
		return totalReceitaPrevistoMes;
	}

	public void setTotalReceitaPrevistoMes(Double totalReceitaPrevistoMes) {
		this.totalReceitaPrevistoMes = totalReceitaPrevistoMes;
	}

	public Double getTotalDespesaPrevistoAcumulado() {
		return totalDespesaPrevistoAcumulado;
	}

	public void setTotalDespesaPrevistoAcumulado(Double totalDespesaPrevistoAcumulado) {
		this.totalDespesaPrevistoAcumulado = totalDespesaPrevistoAcumulado;
	}

	public Double getTotalReceitaPrevistoAcumulado() {
		return totalReceitaPrevistoAcumulado;
	}

	public void setTotalReceitaPrevistoAcumulado(Double totalReceitaPrevistoAcumulado) {
		this.totalReceitaPrevistoAcumulado = totalReceitaPrevistoAcumulado;
	}

	public Usuario getUsuario() {
		if (this.usuario == null || this.usuario.getId() == null) {
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			Usuario usuario = (Usuario) session.getAttribute("usuario");
			if (usuario == null) {
				usuario = new Usuario();
			} else {
				try {
					usuario = usuarioServiceImpl.obter(usuario.getId());
				} catch (ErrorException e) {
					adicionaMensagem(e.getMessage(), FacesMessage.SEVERITY_ERROR);
				}
			}
			this.setUsuario(usuario);
		}
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Conta> getParcelas() {
		if (this.parcelas == null) {
			this.setParcelas(new ArrayList<Conta>());
		}
		return parcelas;
	}

	public void setParcelas(List<Conta> parcelas) {
		this.parcelas = parcelas;
	}

	public Integer getNumr_parcelas() {
		return numr_parcelas;
	}

	public void setNumr_parcelas(Integer numr_parcelas) {
		this.numr_parcelas = numr_parcelas;
	}

}
