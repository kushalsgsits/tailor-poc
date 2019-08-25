package com.harvi.tailor.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(LoginServlet.class.getName());
	private final String userID = "Admin";
	private final String password = "Admin";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get request parameters for user and password
		String user = request.getParameter("user");
		String pwd = request.getParameter("pwd");

		if (userID.equals(user) && password.equals(pwd)) {
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			// setting session to expiry in 30 mins
			session.setMaxInactiveInterval(30 * 60);
			Cookie userNameCookie = new Cookie("user", user);
			userNameCookie.setMaxAge(30 * 60);
			response.addCookie(userNameCookie);
			response.sendRedirect(request.getContextPath() + "/");
		} else {
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
			PrintWriter out = response.getWriter();
			out.println("<font color=red>Either user name or password is wrong.</font>");
			rd.include(request, response);
		}

	}

}
