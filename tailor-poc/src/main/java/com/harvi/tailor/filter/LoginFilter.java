package com.harvi.tailor.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harvi.tailor.auth.AuthUtil;

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
		LOG.info("Requested Resource: " + uri);
		if (uri.equals("/webapi/login")) {
			chain.doFilter(request, response);
			return;
		}

		String reqHeaderVal = req.getHeader(AuthUtil.AUTH_HEADER_STRING);
		if (null != reqHeaderVal && reqHeaderVal.startsWith(AuthUtil.TOKEN_PREFIX)
				&& AuthUtil.isValidJWS(reqHeaderVal.split(" ")[1])) {
			chain.doFilter(request, response);
		} else {
			String errMsg = "Auth token is either missing or invalid";
			LOG.severe(errMsg);
			// TODO res.sendError(HttpServletResponse.SC_UNAUTHORIZED, errMsg);
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}
