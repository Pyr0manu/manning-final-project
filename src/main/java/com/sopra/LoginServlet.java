
package com.sopra;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	@EJB
	private UsersManagement userManagement;

	private String alert = "";

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.getSession().removeAttribute(Constants.CONNECTED_USER_ATTRIBUTE);
		req.setAttribute(Constants.ALERT_ATTRIBUTE, getAlert());

		// Renvoie vers login.jsp
		req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		//Recuperer le login/email et le password envoyes

		String connexionField = req.getParameter("connexionField");
		String password = req.getParameter("password");

		//Detecter le type de connexion (login / email)
		User user = new User(); 
		
		if (connexionField.contains("@")){
			 user = userManagement.findByEmail(connexionField);
		}else{
			 user = userManagement.findByLogin(connexionField);
		}		
	
		if (user == null) {
			setAlert(Constants.BAD_PASSWORD_OR_LOGIN_ALERT);
			resp.sendRedirect(Constants.LOGIN_PAGE);
		}
		
		
		
		Password myPassword = new Password();
		if (myPassword.toHex(user.getPassword())
				.equals(myPassword.toHex(myPassword.generateStorngPasswordHash(password, user)))) {
			req.getSession().setAttribute(Constants.CONNECTED_USER_ATTRIBUTE, user);
			resp.sendRedirect(Constants.HOME_PAGE);
		} else {
			setAlert(Constants.BAD_PASSWORD_OR_LOGIN_ALERT);
			resp.sendRedirect(Constants.LOGIN_PAGE);
		}
	}
}
