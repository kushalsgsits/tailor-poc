package com.harvi.tailor.auth;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/*" })
public class LoginFilter implements Filter {

	private static final Logger log = Logger.getLogger(LoginFilter.class.getName());

	public LoginFilter() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String uri = req.getRequestURI();
		log.info("Requested Resource::" + uri);

		HttpSession session = req.getSession(false);

		if ((session == null || session.getAttribute("user") == null) && !uri.endsWith("html")
				&& !uri.endsWith("login")) {
			log.info("Unauthorized access request");
			res.sendRedirect("/login.html");
		} else {
			// pass the request along the filter chain
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
