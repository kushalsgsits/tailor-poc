package com.harvi.tailor.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/loginbackup")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(LoginServlet.class.getName());
	private final String userID = "Admin";
	private final String password = "Admin";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Get request parameters for user and password
		String user = request.getParameter("uname");
		String pwd = request.getParameter("pwd");

		if (userID.equals(user) && password.equals(pwd)) {
//			String jws = AuthUtil.createJWT("ID", "Gujrati Tailors", user, 1 * 60 * 60 * 1000);
//			response.addHeader(AuthUtil.AUTH_HEADER_STRING, AuthUtil.TOKEN_PREFIX + jws);
			// TODO redirect to requested page
//			response.sendRedirect(request.getContextPath() + "/hello");
		} else {
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
			PrintWriter out = response.getWriter();
			out.println("<font color=red>Either user name or password is wrong.</font>");
			rd.include(request, response);
		}

	}

}
