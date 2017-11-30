package br.com.previsaocontas.services;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.previsaocontas.dao.ContaDAOImpl;
import br.com.previsaocontas.dao.UsuarioDAOImpl;
import br.com.previsaocontas.enums.EnumStatusConta;
import br.com.previsaocontas.enums.EnumTipoConta;
import br.com.previsaocontas.exception.ErrorException;
import br.com.previsaocontas.exception.WarningException;
import br.com.previsaocontas.model.Conta;
import br.com.previsaocontas.model.Usuario;
import br.com.previsaocontas.utilitarios.UtilObjeto;

@Service
@Transactional(rollbackFor = Throwable.class)
public class ContaServiceImpl {

	@Autowired
	private ContaDAOImpl contaDAOImpl;

	@Autowired
	private UsuarioDAOImpl usuarioDAOImpl;

	public void salvar(Conta conta) throws WarningException, ErrorException {

		if (conta == null) {
			throw new WarningException("Conta inválida, verifique!");
		}
		if (conta.getId() != null) {
			if (conta.getStatus().equals(EnumStatusConta.B)) {
				throw new WarningException("Conta já Baixada, verifique!");
			}
		}

		if (conta.getFlag_comum() == null || conta.getFlag_comum().equals("")) {
			throw new WarningException("Informar se a conta é Comun!");
		}

		if (conta.getValor() == null || conta.getValor() == 0) {
			throw new WarningException("Informar um Valor para a Conta!");
		}

		if (conta.getValor() < somaParciais(conta)) {
			throw new WarningException("Valor da Conta inferior ao total das parcelas, verifique!!");
		}

		if (conta.getDia_vencimento() != null && (conta.getDia_vencimento() > 31 || conta.getDia_vencimento() <= 0)) {
			throw new WarningException("Dia de Vencimento inválido (1-31), verifique!");
		}

		if (UtilObjeto.isNull(conta.getQtde_parcelas()) || conta.getQtde_parcelas() == 0) {
			conta.setQtde_parcelas(1);
		}

		List<Conta> contasFilhas = buscaContasFilhas(conta.getId());
		if (contasFilhas != null) {
			for (Conta conta1 : contasFilhas) {
				conta1.setContaPai(conta);
				conta1.setUsuario(conta.getUsuario());
			}
		}

		if (conta.getContaPai() != null) {

			if (conta.getContaPai().getStatus().equals(EnumStatusConta.B)) {
				throw new WarningException("Conta Baixada, verifique!");
			}

		}
		if (conta.getId() == null) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(conta.getData_mes());

			Long numrAgrupador = contaDAOImpl.obterProximoAgrupador(conta.getUsuario());
			for (int i = 0; i < conta.getQtde_parcelas(); i++) {
				Conta contac = SerializationUtils.clone(conta);
				if (contac.getQtde_parcelas() > 1) {
					contac.setId(null);
				}
				contac.setNumr_agrupador(numrAgrupador);
				contac.setNumr_parcela(i + 1);
				if (i > 0) {
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.add(Calendar.MONTH, 1);
				}
				contac.setData_mes(calendar.getTime());
				contaDAOImpl.salvar(contac);

			}
		} else {

			contaDAOImpl.salvar(conta);
		}

	}

	public void excluirTodos(Conta conta) throws WarningException, ErrorException {

		List<Conta> contas = (List<Conta>) this.obterPorAgrupador(conta.getNumr_agrupador());

		for (Conta conta1 : contas) {
			if (conta1.getStatus().equals(EnumStatusConta.B) || this.somaParciais(conta) > 0) {
				continue;
			}
			this.exluir(conta1);
		}

	}

	public Double somaParciais(Conta conta) throws ErrorException {
		return somaParciais(conta, true);
	}

	public Double somaParciais(Conta conta, boolean apenasBaixadas) throws ErrorException {

		if (conta == null) {
			return 0.0;
		}
		List<Conta> contasFilhas = buscaContasFilhas(conta.getId());
		if (contasFilhas == null) {
			return 0.0;
		}

		Double totalParciais = 0.0;
		for (Conta contaFilha : contasFilhas) {
			if (apenasBaixadas) {
				if (contaFilha.getStatus().equals(EnumStatusConta.B)) {
					totalParciais += contaFilha.getValor();
				}
			} else {
				totalParciais += contaFilha.getValor();
			}
		}
		return totalParciais;

	}

	public Double somaParciais(List<Conta> contasFilhas) {

		Double totalParciais = 0.0;
		for (Conta contaFilha : contasFilhas) {
			if (contaFilha.getStatus().equals(EnumStatusConta.B)) {
				totalParciais += contaFilha.getValor();
			}
		}
		return totalParciais;

	}

	public void exluir(Conta conta) throws WarningException, ErrorException {

		if (conta.getContaPai() != null) {
			if (conta.getContaPai().getStatus().equals(EnumStatusConta.B)) {
				throw new WarningException("Conta Pai Baixada, verifique!");
			}

		}

		if (conta.getStatus().equals(EnumStatusConta.B)) {
			throw new WarningException("Conta já baixada, verifique!");
		}

		List<Conta> contasFilhas = buscaContasFilhas(conta.getId());
		if (contasFilhas != null) {
			for (Conta conta1 : contasFilhas) {
				if (conta1.getStatus().equals(EnumStatusConta.B)) {
					throw new WarningException("Existem parciais pagas, favor cancelar o pagamento!");
				}
			}
		}

		Double totalParciais = somaParciais(conta);
		if (totalParciais > 0) {
			if (conta.getTipo().equals(EnumTipoConta.D)) {
				conta.getUsuario().setSaldo(conta.getUsuario().getSaldo() + totalParciais);
			}
			if (conta.getTipo().equals(EnumTipoConta.R)) {
				conta.getUsuario().setSaldo(conta.getUsuario().getSaldo() - totalParciais);
			}
			usuarioDAOImpl.salvar(conta.getUsuario());
		}

		contaDAOImpl.removerTodos(contasFilhas);
		contaDAOImpl.remover(conta);
	}

	public List<Conta> buscaContaMes(Integer mesSelecionado, Integer anoSelecionado, Usuario usuario)
			throws WarningException, ErrorException {
		if (usuario == null || usuario.getId() == null) {
			throw new WarningException("Usuário inválido, verifique!");
		}
		List<Conta> listaContas = contaDAOImpl.buscaContaMes(mesSelecionado, anoSelecionado, usuario);
		for (Conta conta : listaContas) {
			conta.setValorParciais(this.somaParciais(conta));
		}
		return listaContas;
	}

	public List<Conta> buscaContasAcumuladoAberto(Integer mesSelecionado, Integer anoSelecionado, Usuario usuario) {
		return contaDAOImpl.buscaContasAcumuladoAberto(mesSelecionado, anoSelecionado, usuario);
	}

	public void baixarConta(Conta conta) throws WarningException, ErrorException {

		if (conta.getStatus().equals(EnumStatusConta.A)) {

			conta.setStatus(EnumStatusConta.B);
			Double totalParciais = 0.0;

			if (conta.getContaPai() == null) {
				// Atualiza Saldo
				totalParciais = somaParciais(conta);
				if (totalParciais != conta.getValor()) {
					if (conta.getTipo().equals(EnumTipoConta.D)) {
						conta.getUsuario().setSaldo(conta.getUsuario().getSaldo() - (conta.getValor() - totalParciais));
					}
					if (conta.getTipo().equals(EnumTipoConta.R)) {
						conta.getUsuario().setSaldo(conta.getUsuario().getSaldo() + (conta.getValor() - totalParciais));
					}
				} else {
					// Altera Valor da conta para o valor das parciais (caso
					// parciais maior que conta)
					totalParciais = somaParciais(conta.getContaPai(), false);
					if ((totalParciais) > conta.getContaPai().getValor()) {
						conta.getContaPai().setValor(totalParciais);
					}
				}

			} else {

				// Conta Filha
				if (conta.getContaPai().getStatus().equals(EnumStatusConta.B)) {
					throw new WarningException("Conta Pai já baixada, verifique!");
				}

				if (conta.getTipo().equals(EnumTipoConta.D)) {
					conta.getUsuario().setSaldo(conta.getContaPai().getUsuario().getSaldo() - (conta.getValor()));
				}
				if (conta.getTipo().equals(EnumTipoConta.R)) {
					conta.getUsuario().setSaldo(conta.getContaPai().getUsuario().getSaldo() + (conta.getValor()));
				}

			}

			contaDAOImpl.salvar(conta);

		}
	}

	public void abrirConta(Conta conta) throws ErrorException, WarningException {

		if (conta.getStatus().equals(EnumStatusConta.B)) {
			conta.setStatus(EnumStatusConta.A);

			if (conta.getContaPai() == null) {
				// Atualiza Saldo
				Double totalParciais = somaParciais(conta);
				if (totalParciais != conta.getValor()) {
					if (conta.getTipo().equals(EnumTipoConta.D)) {
						conta.getUsuario().setSaldo(conta.getUsuario().getSaldo() + (conta.getValor() - totalParciais));
					}
					if (conta.getTipo().equals(EnumTipoConta.R)) {
						conta.getUsuario().setSaldo(conta.getUsuario().getSaldo() - (conta.getValor() - totalParciais));
					}
				}

			} else {

				// Conta Filha

				if (conta.getContaPai().getStatus().equals(EnumStatusConta.B)) {
					throw new WarningException("Conta Pai já baixada, verifique!");
				}
				if (conta.getTipo().equals(EnumTipoConta.D)) {
					conta.getUsuario().setSaldo(conta.getContaPai().getUsuario().getSaldo() + (conta.getValor()));
				}
				if (conta.getTipo().equals(EnumTipoConta.R)) {
					conta.getUsuario().setSaldo(conta.getContaPai().getUsuario().getSaldo() - (conta.getValor()));
				}

			}
			contaDAOImpl.salvar(conta);

		}

	}

	public List<Conta> obterPorAgrupador(Long numr_agrupador) {
		return (List<Conta>) contaDAOImpl.obterPorAgrupador(numr_agrupador);
	}

	public Conta obter(Long id) {
		return contaDAOImpl.obter(id);
	}

	public void salvarTodos(Conta conta) throws ErrorException {
		List<Conta> contas = (List<Conta>) contaDAOImpl.obterPorAgrupador(conta.getNumr_agrupador());

		for (Conta conta2 : contas) {
			if (conta2.getStatus().equals(EnumStatusConta.B) || somaParciais(conta) > 0) {
				continue;
			}
			conta2.setDescricao(conta.getDescricao());
			conta2.setDia_vencimento(conta.getDia_vencimento());
			conta2.setFlag_comum(conta.getFlag_comum());
			conta2.setValor(conta.getValor());
			contaDAOImpl.salvar(conta2);
		}
	}

	public List<Conta> buscaContasFilhas(Long idPai) {
		return contaDAOImpl.buscaContasFilhas(idPai);

	}

}
