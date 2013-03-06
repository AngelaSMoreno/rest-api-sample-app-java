package com.paypal.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.paypal.api.payments.Link;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.core.LoggingManager;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.dao.Dao;
import com.paypal.model.User;
import com.paypal.model.Order;
import com.paypal.model.UserPaymentDetail;
import com.paypal.util.AppConstants;
import com.paypal.util.AppHelper;
import com.paypal.util.AccessTokenGenerator;
import com.paypal.util.WebHelper;

/**
 * <code>PaymentServlet</code> handles the processing of payments for the sample
 * application. Handles Credit-Card and PayPal payment options.
 * 
 * @author tkanta
 * 
 */
public class PaymentServlet extends HttpServlet {

	private static final long serialVersionUID = 2130981231231233L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		/*
		 * Payment made using PayPal as the payment option. PayPal calls back
		 * our sample application through a GET call to the already set return
		 * URL. The return URL contains information such as PayerID and EC-Token
		 * that are required by the application to close a order payment.
		 */
		if (request.getRequestURI().contains(AppConstants.EXECUTE_PAYMENT)) {
			handleExecutePayment(request, response);
		}

		/*
		 * PayPal calls back our sample application through a GET call to the
		 * already set cancel URL, if the user prefers to cancel his/her order
		 * during PayPal authorization
		 */
		if (request.getRequestURI().contains(AppConstants.CANCEL_PAYMENT)) {
			handleCancelPayment(request, response);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// --------- User Payment using options [CreditCard , PayPal] ------
		if (request.getRequestURI().contains(AppConstants.PAY)) {
			handlePayment(request, response);
		}
	}

	/*
	 * Handle PayPal call back for canceling a payment
	 */
	private void handleCancelPayment(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Order Id used for internal tracking purpose
		String orderId = (String) request.getParameter("orderId");

		try {
			Dao.updateOrderStatus(orderId, "cancelled");
			request.setAttribute("paymentcancel", "paymentcancel");
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
		WebHelper.forward(request, response, AppConstants.SHOW_CURRENT_ORDER);
	}

	/*
	 * Handle PayPal call back for payment completion , when a payment is made
	 * using PayPal as payment option
	 */
	private void handleExecutePayment(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Retrieve PayerID form PayPal GET call
		String payerId = request.getParameter("PayerID");

		// Order Id used for internal tracking purpose
		String orderId = (String) request.getParameter("orderId");
		UserPaymentDetail userPaymentDetails = new UserPaymentDetail();
		try {
			userPaymentDetails = Dao.getOrderByOrderId(orderId);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}

		// Construct a payment for complete payment execution
		Payment payment = new Payment();
		payment.setId(userPaymentDetails.getPaymentId());
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		try {

			// set access token
			String accessToken = AccessTokenGenerator.getAccessToken();
			String requestId = UUID.randomUUID().toString();
			APIContext apiContext = new APIContext(accessToken, requestId);
			payment = payment.execute(apiContext, paymentExecute);
		} catch (PayPalRESTException pex) {
			if (pex.getMessage().contains(AppConstants.VALIDATION_ERROR)) {
				WebHelper.formErrorMessage(request, pex);
				WebHelper.forward(request, response, AppConstants.SHOW_PROFILE);
				return;
			} else {
				LoggingManager.debug(PaymentServlet.class, pex.getMessage());
				throw new ServletException(pex);
			}
		}
		UserPaymentDetail userPaymentDetail = null;
		try {
			Dao.updateOrderStatus(orderId, payment.getState());
			userPaymentDetail = Dao.getOrderByOrderId(orderId);
			request.setAttribute("paymentDetails", userPaymentDetail);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
		WebHelper.forward(request, response, AppConstants.SHOW_CURRENT_ORDER);
	}

	/*
	 * Handle Payment using options [CreditCard , PayPal]
	 */
	private void handlePayment(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			UnsupportedEncodingException, IOException {
		HttpSession session = request.getSession(false);
		String paymentMethod = request.getParameter("order_payment_method");

		// Get Logged in User detail
		String email = (String) session.getAttribute("user");
		User user = null;
		try {
			user = Dao.getUser(email);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}

		// create User Order
		String orderId = null;
		try {
			orderId = Dao.createOrder(user.getId());
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}

		// prepare Order
		Order orderDetail = createOrderDetailFormRequest(request, orderId);

		// create a Payment object
		Payment payment = null;
		try {

			// Create distinct Payment objects for create payment that
			// holds the appropriate payment methods (credit-card, PayPal)set
			// in the payment object
			payment = AppHelper.createPayment(user.getCreditCardId(),
					orderDetail);
		} catch (PayPalRESTException pex) {
			LoggingManager.debug(PaymentServlet.class, pex.getMessage());
			throw new ServletException(pex);
		}

		// save User Order details
		try {
			Dao.insertOrder(orderId, user.getId(), payment);
		} catch (SQLException sqlex) {
			throw new ServletException(sqlex);
		}
		if (paymentMethod.equalsIgnoreCase(AppConstants.PAYPAL)) {

			// redirect to PayPal for authorization
			String redirectUrl = getApprovalURL(payment);
			response.sendRedirect(redirectUrl);
			return;
		} else {
			UserPaymentDetail paymentDetails = null;
			try {

				// Get User payments list
				paymentDetails = Dao.getOrderByOrderId(orderId);
			} catch (SQLException sqlex) {
				throw new ServletException(sqlex);
			}
			request.setAttribute("paymentDetails", paymentDetails);
			// redirect user to order page
			WebHelper.forward(request, response,
					AppConstants.SHOW_CURRENT_ORDER);
		}
	}

	/*
	 * Retrieve approval URL form payment object
	 */
	private String getApprovalURL(Payment payment)
			throws UnsupportedEncodingException {
		String redirectUrl = null;
		List<Link> links = payment.getLinks();
		for (Link l : links) {
			if (l.getRel().equalsIgnoreCase("approval_url")) {
				redirectUrl = URLDecoder
						.decode(l.getHref(), AppConstants.UTF_8);
				break;
			}
		}
		return redirectUrl;
	}

	/*
	 * Create a Order for database store using the request parameters
	 */
	private Order createOrderDetailFormRequest(HttpServletRequest request,
			String orderId) {
		String OrderAmount = request.getParameter("orderAmount");
		String orderDesc = request.getParameter("orderDesc");
		String paymentMethod = request.getParameter("order_payment_method");

		// create Order
		Order orderDetail = new Order();
		orderDetail.setOrderAmount(OrderAmount.trim());
		orderDetail.setOrderDesc(orderDesc);
		orderDetail.setPaymentMethod(paymentMethod.trim());
		orderDetail.setPaymentIntent("sale");
		orderDetail.setCurrency("USD");
		orderDetail.setShipping("2");
		orderDetail.setTax("1");
		if (paymentMethod.equalsIgnoreCase(AppConstants.PAYPAL)) {
			orderDetail.setCancelUrl(WebHelper.getContextPath(request) + "/"
					+ AppConstants.CANCEL_PAYMENT + "?orderId=" + orderId);
			orderDetail.setReturnUrl(WebHelper.getContextPath(request) + "/"
					+ AppConstants.EXECUTE_PAYMENT + "?orderId=" + orderId);
		}
		return orderDetail;
	}
}
