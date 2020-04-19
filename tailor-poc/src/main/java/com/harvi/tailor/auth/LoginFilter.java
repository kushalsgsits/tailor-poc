package com.harvi.tailor.auth;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/*" })
public class LoginFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(LoginFilter.class.getName());

	public LoginFilter() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String uri = req.getRequestURI();
		LOG.info("Requested Resource::" + uri);
		if (true) {
			chain.doFilter(request, response);
			return;
		}
		
		String reqHeaderVal = req.getHeader(AuthUtil.AUTH_HEADER_STRING);
		// consume JWT i.e. execute signature validation
//		if (null != reqHeaderVal && reqHeaderVal.startsWith(JWTUtil.TOKEN_PREFIX)
//				&& JWTUtil.isValidJWS(reqHeaderVal.split(" ")[1])) {
//			if (uri.contains("login")) {
//				String redirect = req.getContextPath() + "/hello";
//				res.sendRedirect(redirect);
//			} else {
//				// pass the request along the filter chain
//				chain.doFilter(request, response);
//			}
//		} else {
//			if (uri.contains("login")) {
//				// pass the request along the filter chain
//				chain.doFilter(request, response);
//			} else {
//				LOG.info("Unauthorized access request");
//				res.sendRedirect("/login.html");
//			}
//		}

		if (null != reqHeaderVal && reqHeaderVal.startsWith(AuthUtil.TOKEN_PREFIX)
				&& AuthUtil.isValidJWS(reqHeaderVal.split(" ")[1])) {
			chain.doFilter(request, response);
		} else {
			if (uri.contains("login")) {
				// pass the request along the filter chain
				chain.doFilter(request, response);
			} else {
				LOG.info("Unauthorized access request");
				res.sendRedirect("/login");
			}
		}
	}

	public void destroy() {
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}
