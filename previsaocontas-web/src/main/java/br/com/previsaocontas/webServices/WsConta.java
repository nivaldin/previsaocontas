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

import br.com.previsaocontas.exception.ErrorException;
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
	    resultado.put("mensagem", "Erro ao buscar contas, verifique!");
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
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (ErrorException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (Exception e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	request.getSession().setAttribute("usuario", u);
	resultado.put("mensagem", "Logado com sucesso!");
	return new ResponseEntity<>(resultado, HttpStatus.OK);

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> login() {

	try {

	    resultado.clear();
	    if (this.getUsuarioLogado() == null) {
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    return new ResponseEntity<>(this.getUsuarioLogado(), HttpStatus.OK);
	} catch (Exception e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}

    }

    private Usuario getUsuarioLogado() {
	try {

	    Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	    return usuario;

	} catch (Exception e) {
	    return null;
	}
    }

    @RequestMapping(value = "/contas/salvar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> salvar(@RequestBody Conta conta) {

	try {
	    resultado.clear();
	    conta.setUsuario(getUsuarioLogado());
	    contaServiceImpl.salvar(conta);
	} catch (WarningException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (ErrorException e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (Exception e) {
	    resultado.put("mensagem", e.getMessage());
	    return new ResponseEntity<>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	HashMap<String, String> mensagem = new HashMap<>();
	mensagem.put("mensagem", "Salvo com sucesso!");
	return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @RequestMapping(value = "/login/logout", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> logout() {
	resultado.clear();
	request.getSession().setAttribute("usuario", null);
	HashMap<String, String> mensagem = new HashMap<>();
	mensagem.put("mensagem", "Você saiu do sistema!");
	return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @RequestMapping(value = "/tiposConta", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> tiposConta() {

	resultado.clear();
	List<HashMap<String, String>> listaTiposConta = new ArrayList<>();

	HashMap<String, String> tipoConta = new HashMap<>();
	tipoConta.put("descricao", "Despesa");
	tipoConta.put("valor", "D");

	listaTiposConta.add(tipoConta);

	tipoConta = new HashMap<>();
	tipoConta.put("descricao", "Receita");
	tipoConta.put("valor", "R");

	listaTiposConta.add(tipoConta);

	resultado.put("tiposConta", listaTiposConta);
	return new ResponseEntity<>(resultado, HttpStatus.OK);
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
