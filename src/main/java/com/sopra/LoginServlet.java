
package com.sopra;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
		
	@EJB
	private UsersManagement userManagement;
	
	// Initialisation du message d'erreur, permettant d'avertir l'utilisateur si
	// celui-ci ne remplie pas les conditions pour se connecter.
	// La classe "Constants.java" contient la liste des messages d'erreur
	// disponible.

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//Vérification de la session: si un user (presume VALIDE) est connecte, on le redirige vers home.html
		if(req.getSession().getAttribute(Constants.CONNECTED_USER_ATTRIBUTE)!=null){
			resp.sendRedirect(Constants.HOME_PAGE);
		}
		req.setAttribute(Constants.ALERT_ATTRIBUTE, Alert.getAlert());
		
		req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// Recuperer le login et le password envoyes
		String login = req.getParameter("login");
		String password = req.getParameter("password");

		// Verifier si on l'a dans la BDD
		User user = userManagement.findByLogin(login);
		if (user == null) {
			Alert.setAlert(Constants.BAD_PASSWORD_OR_LOGIN_ALERT);
			resp.sendRedirect(Constants.LOGIN_PAGE);
		}
		Password myPassword = new Password();
		if (myPassword.toHex(user.getPassword())
				.equals(myPassword.toHex(myPassword.generateStorngPasswordHash(password, user)))) {
			req.getSession().setAttribute(Constants.CONNECTED_USER_ATTRIBUTE, user);
			resp.sendRedirect(Constants.HOME_PAGE);
		} else {
			Alert.setAlert(Constants.BAD_PASSWORD_OR_LOGIN_ALERT);
			resp.sendRedirect(Constants.LOGIN_PAGE);
		}
	}

}
