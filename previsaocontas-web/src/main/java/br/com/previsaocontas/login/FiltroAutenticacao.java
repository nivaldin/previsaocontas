package br.com.previsaocontas.login;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.previsaocontas.model.Usuario;

/**
 * Servlet Filter implementation class FiltroAutenticacao
 */
@WebFilter("/*")
public class FiltroAutenticacao implements Filter {

	/**
	 * Default constructor.
	 */
	public FiltroAutenticacao() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		HttpSession session = req.getSession();

		Usuario usuario = (Usuario) session.getAttribute("usuario");

		String reqURI = ((HttpServletRequest) req).getRequestURI();

		if (reqURI.contains("login.xhtml") || reqURI.contains("contas-angularjs.html") || reqURI.contains("rest/login")) {

			chain.doFilter(req, resp);

		}else if (reqURI.contains("resources") || reqURI.contains("javax.faces.resource")) {

		    chain.doFilter(req, resp);
		    
		} else {

			if (usuario == null) {

				// req.getRequestDispatcher("/login.xhtml?faces-redirect=true").forward(req,
				// resp);
				req.getRequestDispatcher("/contas/inicial.xhtml?faces-redirect=true").forward(req, resp);

			} else if (reqURI.equals("/previsaocontas-web/")) {

				req.getRequestDispatcher("/contas/inicial.xhtml?faces-redirect=true").forward(req, resp);

			} else {

				chain.doFilter(req, resp);

			}

		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
