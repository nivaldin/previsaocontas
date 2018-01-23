package br.com.previsaocontas.webServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
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

    @RequestMapping(value = "/contas/{mes}/{ano}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buscaContasMes(@PathVariable("mes") Integer mes, @PathVariable("ano") Integer ano) {
    	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    List<Conta> contas = new ArrayList<Conta>();
	    contas = contaServiceImpl.buscaContaMes(mes, ano, this.getUsuarioLogado());
	    resultado.put("objeto", contas);
	    return new ResponseEntity<>(resultado, HttpStatus.OK);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", "Erro ao buscar contas!");
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @RequestMapping(value = "/contas/contasFilhas/{idPai}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buscaContasMes(@PathVariable("idPai") Long idPai) {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    List<Conta> contas = new ArrayList<Conta>();
	    contas = contaServiceImpl.buscaContasFilhas(idPai);
	    return new ResponseEntity<>(contas, HttpStatus.OK);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @RequestMapping(value = "/contas/resumoContas/{mes}/{ano}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resumoContas(@PathVariable("mes") Integer mes, @PathVariable("ano") Integer ano, @RequestBody List<Conta> listaContas) {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    HashMap<String, Double> listaValores = new HashMap<>();
	    listaValores = contaServiceImpl.calculaResumoMes(mes, ano, listaContas, this.getUsuarioLogado());
	    return new ResponseEntity<>(listaValores, HttpStatus.OK);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @RequestMapping(value = "/login/{usuario}/{senha}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validaLogin(@PathVariable("usuario") String usuario, @PathVariable("senha") String senha) {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();

	Usuario u = new Usuario();
	try {
	    u = usuarioServiceImpl.buscaUsuario(usuario, senha);
	} catch (WarningException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	request.getSession().setAttribute("usuario", u);
	resultado.put("objeto", u);
	return new ResponseEntity<>(resultado, HttpStatus.OK);

    }

    @RequestMapping(value = "/login/usuarioLogado", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> usuarioSessao() {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	Usuario usuario = null;
	try {
	    try {
		usuario = this.getUsuarioLogado();
	    } catch (Exception e) {
		resultado.put("mensagem", "");
		return new ResponseEntity<>(resultado, HttpStatus.CONFLICT);
	    }

	    resultado.put("objeto", usuario);
	    return new ResponseEntity<>(resultado, HttpStatus.OK);

	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}

    }

    private Usuario getUsuarioLogado() throws Exception {

	Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	if (usuario == null) {
	    throw new Exception("Usuário não logado");
	}
	usuario = usuarioServiceImpl.obter(usuario.getId());
	request.getSession().setAttribute("usuario", usuario);
	return usuario;

    }

    @RequestMapping(value = "/contas/salvarTodos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> salvarTodos(@RequestBody Conta conta) {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    contaServiceImpl.salvarTodos(conta);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	resultado.put("mensagem", "Todas as parcelas não pagas foram salvas iguais a parcela atual!");
	resultado.put("objeto", conta);
	return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @RequestMapping(value = "/contas/excluirTodos", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> excluirTodos(@RequestBody Conta conta) {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    contaServiceImpl.excluirTodos(conta);
	} catch (WarningException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	HashMap<String, String> mensagem = new HashMap<>();
	mensagem.put("mensagem", "Todas as parcelas não pagas foram excluídas!");
	return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @RequestMapping(value = "/contas/excluir", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> excluir(@RequestBody Conta conta) {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    contaServiceImpl.exluir(conta);
	} catch (WarningException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	HashMap<String, Object> mensagem = new HashMap<>();
	mensagem.put("mensagem", "A conta '" + conta.getDescricao() + "' foi excluída!");
	mensagem.put("objecto", conta);
	return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @RequestMapping(value = "/contas/baixar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> baixar(@RequestBody Conta conta) {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    contaServiceImpl.baixarConta(conta);
	} catch (WarningException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
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
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    contaServiceImpl.abrirConta(conta);
	} catch (WarningException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
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
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    conta.setUsuario(getUsuarioLogado());
	    contaServiceImpl.salvar(conta);
	} catch (WarningException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.NOT_ACCEPTABLE);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	resultado.put("mensagem", "A conta foi salva!");
	resultado.put("objeto", conta);
	return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @RequestMapping(value = "/usuario/alterarSaldo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> alterarSaldo(@RequestBody Usuario usuario) {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    usuarioServiceImpl.salvar(usuario);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	HashMap<String, String> mensagem = new HashMap<>();
	mensagem.put("mensagem", "O Saldo foi alterado!");
	return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @RequestMapping(value = "/login/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout() {
	request.getSession().setAttribute("usuario", null);
	return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/anos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> anos() {
	Logger logger = Logger.getLogger(WsConta.class);
	HashMap<String, Object> resultado = new HashMap<>();
	try {
	    List<Integer> listaAnos = new ArrayList<>();
		Calendar calendario = Calendar.getInstance();
		calendario.add(Calendar.YEAR, -3);

		for (int i = 0; i < 5; i++) {
		    calendario.add(Calendar.YEAR, 1);
		    listaAnos.add(calendario.get(Calendar.YEAR));
		}
		resultado.put("listaAnos", listaAnos);
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
    }

}
