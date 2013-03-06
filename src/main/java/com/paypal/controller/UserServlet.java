package com.paypal.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.paypal.api.payments.CreditCard;
import com.paypal.core.ConfigManager;
import com.paypal.core.LoggingManager;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.core.rest.PayPalResource;
import com.paypal.dao.DBConnection;
import com.paypal.dao.Dao;
import com.paypal.model.CreditCardDetail;
import com.paypal.model.ErrorMessage;
import com.paypal.model.User;
import com.paypal.model.UserPaymentDetail;
import com.paypal.util.AppConstants;
import com.paypal.util.AppHelper;
import com.paypal.util.WebHelper;

/**
 * <code>UserServlet</code> handles user management. It keeps track of sessions,
 * sign-ups, log-in, log-out order descriptions and profile updates
 * 
 * @author tkanta
 * 
 */
public class UserServlet extends HttpServlet {

	private static final long serialVersionUID = 1231435434634644452L;

	/**
	 * Initialize SDK configuration file and database connection
	 */
	@Override
	public void init() throws ServletException {

		// initialize sdk configuration
		InputStream is = PaymentServlet.class
				.getResourceAsStream("/sdk_config.properties");
		try {
			PayPalResource.initConfig(is);
		} catch (PayPalRESTException pex) {
			LoggingManager.debug(PaymentServlet.class, pex.getMessage());
			throw new ServletException(pex);
		}

		// Initialize the database drivers
		DBConnection.getConnection();

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// check if session is valid
		if (!WebHelper.checkSessionValidity(request)) {
			WebHelper.forward(request, response, AppConstants.SHOW_HOME);
		} else {
			if (request.getRequestURI().contains(AppConstants.HOME)) {

				// ------ Show home page ----------
				WebHelper.forward(request, response, AppConstants.SHOW_HOME);
			} else if (request.getRequestURI().contains(AppConstants.SIGNUP)) {

				// ------ Show signup page ----------
				WebHelper.forward(request, response, AppConstants.SHOW_SIGNUP);
			} else if (request.getRequestURI().contains(AppConstants.SIGNIN)) {

				// ------ Show signin page ----------
				WebHelper.forward(request, response, AppConstants.SHOW_SIGNIN);
			} else if (request.getRequestURI().contains(AppConstants.PROFILE)) {

				// ------ Show profile page ----------
				handleDisplayProfilePage(request, response);
			} else if (request.getRequestURI().contains(
					AppConstants.SHOW_ORDERS)) {

				// ------ Show order page ----------
				handleDisplayOrderPage(request, response);
			} else if (request.getRequestURI().contains(AppConstants.SIGNOUT)) {

				// ------ Handle signout ----------
				handleSignout(request, response);
			} else {
				throw new ServletException("Unknown resource requested");
			}

		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// check if session is valid
		if (!WebHelper.checkSessionValidity(request)) {
			WebHelper.redirect(request, response, AppConstants.SHOW_SIGNIN);
		} else {
			if (request.getRequestURI().contains(AppConstants.PLACE_ORDER)) {

				// -------- User order ---------
				handleOrder(request, response);
			} else if (request.getRequestURI().contains(AppConstants.SIGNUP)) {

				// -------- User signup ---------
				handleSignup(request, response);
			} else if (request.getRequestURI().contains(AppConstants.SIGNIN)) {

				// -------- User signin ---------
				handleSignin(request, response);
			} else if (request.getRequestURI().contains(AppConstants.PROFILE)) {

				// -------- User profile ---------
				handleUpdateProfile(request, response);
			} else {
				throw new ServletException("Unknown resource requested");
			}

		}
	}

	/*
	 * Handle user Sign out
	 */
	private void handleSignout(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		session.invalidate();

		// forward user to home
		WebHelper.forward(request, response, AppConstants.SHOW_HOME);
	}

	/*
	 * display order page
	 */
	private void handleDisplayOrderPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Get Logged in User detail
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("user");
		User user = null;
		try {
			user = Dao.getUser(email);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}

		List<UserPaymentDetail> paymentList = null;
		try {

			// Get User payments list
			paymentList = Dao.getOrdersByUserId(user.getId());

		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
		request.setAttribute("paymentList", paymentList);
		WebHelper.forward(request, response, AppConstants.SHOW_ORDER);
	}

	/*
	 * display profile page
	 */
	private void handleDisplayProfilePage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("user");
		User user = null;
		try {
			user = Dao.getUser(email);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}

		// Get Credit Card detail
		CreditCardDetail cardDetail = getCreditCardDetail(request, response,
				user);
		request.setAttribute("cardDetail", cardDetail);
		WebHelper.forward(request, response, AppConstants.SHOW_PROFILE);
	}

	/*
	 * Get Credit Card detail
	 */
	private CreditCardDetail getCreditCardDetail(HttpServletRequest request,
			HttpServletResponse response, User user) throws ServletException,
			IOException {
		CreditCardDetail cardDetail = null;
		try {
			cardDetail = AppHelper.getCreditCardDetail(user.getCreditCardId());
		} catch (PayPalRESTException pex) {
			if (pex.getMessage().contains(AppConstants.VALIDATION_ERROR)) {
				WebHelper.formErrorMessage(request, pex);
				WebHelper.forward(request, response, AppConstants.SHOW_PROFILE);
			} else {
				LoggingManager.debug(PaymentServlet.class, pex.getMessage());
				throw new ServletException(pex);
			}
		}
		return cardDetail;
	}

	/*
	 * Handle display of user payment options [ CreditCard , PayPal]
	 */
	private void handleOrder(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		if (session != null && session.getAttribute("user") != null) {
			String orderString = request.getQueryString();
			String[] orderArray = orderString.split("&");
			String orderAmount = orderArray[0].split("=")[1];
			String orderDesc = orderArray[1].split("=")[1];

			request.setAttribute("orderAmount", orderAmount);
			request.setAttribute("orderDesc", orderDesc);

			WebHelper.forward(request, response, AppConstants.SHOW_PLACE_ORDER);

		} else {
			response.sendRedirect(WebHelper.getContextPath(request) + "/signin");
			return;
		}
	}

	/*
	 * Handle user sign up
	 */
	private void handleSignup(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("user_email");
		String password = request.getParameter("user_password");
		User user = null;
		CreditCard createdCreditCard = null;

		// Retrieve User
		try {
			user = Dao.getUser(email);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}

		// Check for User existence
		if (user != null) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.add(AppConstants.USER_EXIST);
			request.setAttribute("error", errorMsg);
			WebHelper.forward(request, response, AppConstants.SHOW_SIGNUP);
			return;
		}

		// create credit card
		try {
			createdCreditCard = AppHelper.createCreditCard(request);
		} catch (PayPalRESTException pex) {
			if (pex.getMessage().contains(AppConstants.VALIDATION_ERROR)) {
				WebHelper.formErrorMessage(request, pex);
				WebHelper.forward(request, response, AppConstants.SHOW_SIGNUP);
				return;
			} else {
				LoggingManager.debug(PaymentServlet.class, pex.getMessage());
				throw new ServletException(pex);
			}

		}

		// Insert User
		try {
			Dao.insertUser(email, password, createdCreditCard.getId());
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
		HttpSession session = request.getSession();
		session.setAttribute("isSessionActive", true);
		session.setAttribute("user", email);

		// forward User to home
		WebHelper.forward(request, response, AppConstants.SHOW_HOME);
	}

	/*
	 * Handle user sign in
	 */
	private void handleSignin(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// validate User
		String email = request.getParameter("user_email");
		String password = request.getParameter("user_password");
		User user = null;
		try {
			user = Dao.getUser(email);
			if (user == null
					|| ((user != null) && !user.getPassword().equals(password))) {
				ErrorMessage errMsg = new ErrorMessage();
				errMsg.add("User Doesnot Exist.");
				request.setAttribute("error", errMsg);
				WebHelper.forward(request, response, AppConstants.SHOW_SIGNIN);
			} else {
				HttpSession session = request.getSession();
				session.setAttribute("isSessionActive", true);
				session.setAttribute("user", user.getEmail());

				// forward User to home
				WebHelper.forward(request, response, AppConstants.SHOW_HOME);
			}
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
	}

	/*
	 * Handle User profile updates
	 */
	private void handleUpdateProfile(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ErrorMessage errMsg = new ErrorMessage();

		// validate User existence
		String email = request.getParameter("user_email");
		String password = request.getParameter("user_current_password");
		User user = null;
		try {
			user = Dao.getUser(email);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
		if (user == null
				|| ((user != null) && !user.getPassword().equals(password))) {
			errMsg.add(AppConstants.USER_PSW_INCORRECT);
		}

		// Set credit card details for display
		HttpSession session = request.getSession(false);
		String useremail = (String) session.getAttribute("user");
		User userFromSession = null;
		try {
			userFromSession = Dao.getUser(useremail);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
		CreditCardDetail cardDetail = getCreditCardDetail(request, response,
				userFromSession);
		request.setAttribute("cardDetail", cardDetail);

		// Check for password matching
		String update_password = request.getParameter("update_password") != "" ? request
				.getParameter("update_password") : AppConstants.EMPTY_STRING;
		String update_password_confirmation = request
				.getParameter("update_password_confirmation") != "" ? request
				.getParameter("update_password_confirmation")
				: AppConstants.EMPTY_STRING;
		if (!update_password.equals(update_password_confirmation)) {
			errMsg.add(AppConstants.PASSWORD_NOT_MATCH);
		}

		// If error exist , display in UI
		if (errMsg.getMessageList().size() > 0) {
			request.setAttribute("error", errMsg);
			WebHelper.forward(request, response, AppConstants.SHOW_PROFILE);
		} else {
			updateProfile(request, response, user, update_password_confirmation);
		}
	}

	/*
	 * Update User profile
	 */
	private void updateProfile(HttpServletRequest request,
			HttpServletResponse response, User user,
			String update_password_confirmation) throws ServletException,
			IOException {

		// Do a profile update
		try {

			// create credit card and update User table with new
			// creditcardId and new password
			CreditCard newCreditCard = AppHelper.createCreditCard(request);
			user.setCreditCardId(newCreditCard.getId());
			user.setPassword(update_password_confirmation);
			Dao.updateUser(user);
		} catch (PayPalRESTException pex) {
			if (pex.getMessage().contains(AppConstants.VALIDATION_ERROR)) {
				WebHelper.formErrorMessage(request, pex);
				WebHelper.forward(request, response, AppConstants.SHOW_PROFILE);
				return;
			} else {
				LoggingManager.debug(PaymentServlet.class, pex.getMessage());
				throw new ServletException(pex);
			}
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
		WebHelper.forward(request, response, AppConstants.SHOW_HOME);
	}

}
