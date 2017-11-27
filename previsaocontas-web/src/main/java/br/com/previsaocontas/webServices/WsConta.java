package br.com.previsaocontas.webServices;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/contas/{mes}/{ano}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity buscaContasMes(@PathVariable("mes") Integer mes, @PathVariable("ano") Integer ano) {

		try {
			List<Conta> contas = new ArrayList<Conta>();
			contas = contaServiceImpl.buscaContaMes(mes, ano,
					(Usuario) request.getSession(false).getAttribute("usuario"));
			return new ResponseEntity(contas, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Erro ao buscar contas, verifique!", HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/login/{usuario}/{senha}", method = RequestMethod.GET)
	public ResponseEntity login(@PathVariable("usuario") String usuario, @PathVariable("senha") String senha) {

		if (usuario.equals("") || senha.equals("")) {
			return new ResponseEntity("Usuário ou senha inválidos, verifique!", HttpStatus.BAD_REQUEST);
		}

		Usuario u = new Usuario();
		try {
			u = usuarioServiceImpl.buscaUsuario(usuario, senha);
		} catch (WarningException e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (ErrorException e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		request.getSession().setAttribute("usuario", u);
		return new ResponseEntity("Logado com sucesso!", HttpStatus.OK);

	}

}
