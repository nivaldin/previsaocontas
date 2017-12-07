package br.com.previsaocontas.webServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.previsaocontas.exception.WarningException;
import br.com.previsaocontas.model.Conta;
import br.com.previsaocontas.model.Usuario;
import br.com.previsaocontas.services.ContaServiceImpl;
import br.com.previsaocontas.services.UsuarioServiceImpl;

@RestController
@RequestMapping(value = "/")
public class WsConta {
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UsuarioServiceImpl usuarioServiceImpl;

	@Autowired
	private ContaServiceImpl contaServiceImpl;

	private HashMap<String, Object> resultado = new HashMap<>();

	@RequestMapping(value = "/contas/{mes}/{ano}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> buscaContasMes(@PathVariable("mes") Integer mes, @PathVariable("ano") Integer ano) {

		resultado.clear();
		try {
			List<Conta> contas = new ArrayList<Conta>();
			contas = contaServiceImpl.buscaContaMes(mes, ano, this.getUsuarioLogado());
			return new ResponseEntity<>(contas, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultado.put("mensagem", "Erro ao buscar contas!");
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/contas/contasFilhas/{idPai}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> buscaContasMes(@PathVariable("idPai") Long idPai) {

		resultado.clear();
		try {
			List<Conta> contas = new ArrayList<Conta>();
			contas = contaServiceImpl.buscaContasFilhas(idPai);
			return new ResponseEntity<>(contas, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/contas/resumoContas/{mes}/{ano}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resumoContas(@PathVariable("mes") Integer mes, @PathVariable("ano") Integer ano,
			@RequestBody List<Conta> listaContas) {

		resultado.clear();
		try {
			HashMap<String, Double> listaValores = new HashMap<>();
			listaValores = contaServiceImpl.calculaResumoMes(mes, ano, listaContas, this.getUsuarioLogado());
			return new ResponseEntity<>(listaValores, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/login/{usuario}/{senha}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> validaLogin(@PathVariable("usuario") String usuario, @PathVariable("senha") String senha) {

		resultado.clear();

		Usuario u = new Usuario();
		try {
			u = usuarioServiceImpl.buscaUsuario(usuario, senha);
		} catch (WarningException e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		request.getSession().setAttribute("usuario", u);
		resultado.put("objeto", u);
		return new ResponseEntity<>(resultado, HttpStatus.OK);

	}

	@RequestMapping(value = "/login/usuarioLogado", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> usuarioSessao() {

		try {
			resultado.clear();
			Usuario usuario = this.getUsuarioLogado();
			if (usuario == null) {
				resultado.put("mensagem", "Usuário logado não encontrado!");
				return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
			}
			resultado.put("objeto", usuario);
			return new ResponseEntity<>(resultado, HttpStatus.OK);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private Usuario getUsuarioLogado() {
		try {

			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			usuario = usuarioServiceImpl.obter(usuario.getId());
			request.getSession().setAttribute("usuario", usuario);
			return usuario;

		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/contas/salvarTodos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> salvarTodos(@RequestBody Conta conta) {

		try {
			resultado.clear();
			contaServiceImpl.salvarTodos(conta);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		resultado.put("mensagem", "Todas as parcelas não pagas foram salvas iguais a parcela atual!");
		resultado.put("objeto", conta);
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@RequestMapping(value = "/contas/excluirTodos", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> excluirTodos(@RequestBody Conta conta) {

		try {
			resultado.clear();
			contaServiceImpl.excluirTodos(conta);
		} catch (WarningException e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HashMap<String, String> mensagem = new HashMap<>();
		mensagem.put("mensagem", "Todas as parcelas não pagas foram excluídas!");
		return new ResponseEntity<>(mensagem, HttpStatus.OK);
	}

	@RequestMapping(value = "/contas/excluir", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> excluir(@RequestBody Conta conta) {

		try {
			resultado.clear();
			contaServiceImpl.exluir(conta);
		} catch (WarningException e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HashMap<String, Object> mensagem = new HashMap<>();
		mensagem.put("mensagem", "A conta foi excluída!");
		mensagem.put("objecto", conta);
		return new ResponseEntity<>(mensagem, HttpStatus.OK);
	}

	@RequestMapping(value = "/contas/baixar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> baixar(@RequestBody Conta conta) {

		try {
			resultado.clear();
			contaServiceImpl.baixarConta(conta);
		} catch (WarningException e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HashMap<String, Object> mensagem = new HashMap<>();
		mensagem.put("mensagem", "A conta foi baixada!");
		mensagem.put("objecto", conta);
		return new ResponseEntity<>(mensagem, HttpStatus.OK);
	}

	@RequestMapping(value = "/contas/cancelarBaixa", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> cancelarBaixa(@RequestBody Conta conta) {

		try {
			resultado.clear();
			contaServiceImpl.abrirConta(conta);
		} catch (WarningException e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HashMap<String, Object> mensagem = new HashMap<>();
		mensagem.put("mensagem", "A baixa foi cancelada!");
		mensagem.put("objecto", conta);
		return new ResponseEntity<>(mensagem, HttpStatus.OK);
	}

	@RequestMapping(value = "/contas/salvar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> salvar(@RequestBody Conta conta) {

		try {
			resultado.clear();
			conta.setUsuario(getUsuarioLogado());
			contaServiceImpl.salvar(conta);
		} catch (WarningException e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		resultado.put("mensagem", "A conta foi salva!");
		resultado.put("objeto", conta);
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@RequestMapping(value = "/usuario/alterarSaldo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> alterarSaldo(@RequestBody Usuario usuario) {

		try {
			resultado.clear();
			usuarioServiceImpl.salvar(usuario);
		} catch (Exception e) {
			resultado.put("mensagem", e.getMessage());
			return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HashMap<String, String> mensagem = new HashMap<>();
		mensagem.put("mensagem", "O Saldo foi alterado!");
		return new ResponseEntity<>(mensagem, HttpStatus.OK);
	}

	@RequestMapping(value = "/login/logout", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> logout() {
		request.getSession().setAttribute("usuario", null);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/anos", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> anos() {

		resultado.clear();
		List<Integer> listaAnos = new ArrayList<>();
		Calendar calendario = Calendar.getInstance();
		calendario.add(Calendar.YEAR, -3);

		for (int i = 0; i < 5; i++) {
			calendario.add(Calendar.YEAR, 1);
			listaAnos.add(calendario.get(Calendar.YEAR));
		}
		resultado.put("listaAnos", listaAnos);
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

}
