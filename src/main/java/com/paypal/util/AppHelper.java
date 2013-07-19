package com.paypal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.CreditCardToken;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.FundingInstrument;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.model.CreditCardDetail;
import com.paypal.model.Order;

public class AppHelper {
	/**
	 * Retrieve Credit card details with creditCardId
	 * @param creditCardId
	 * @return
	 * @throws PayPalRESTException
	 */
	public static CreditCardDetail getCreditCardDetail(String creditCardId)
			throws PayPalRESTException {
		// Get Credit Card detail
		String accessToken = AccessTokenGenerator.getAccessToken();
		CreditCard creditcard = CreditCard.get(accessToken, creditCardId);
		CreditCardDetail cardDetail = new CreditCardDetail();
		cardDetail.setCvv(creditcard.getCvv2());
		cardDetail.setNumber(creditcard.getNumber());
		cardDetail.setType(creditcard.getType());
		cardDetail.setExpMonth(Integer.toString(creditcard.getExpireMonth()));
		cardDetail.setExpYear(Integer.toString(creditcard.getExpireYear()));

		return cardDetail;
	}

	/**
	 *  Creates credit card
	 * @param request
	 * @return
	 * @throws PayPalRESTException
	 */
	public static CreditCard createCreditCard(HttpServletRequest request)
			throws PayPalRESTException {

		String accessToken = AccessTokenGenerator.getAccessToken();
		CreditCard creditCard = new CreditCard();
		creditCard.setExpireMonth(Integer.parseInt(request.getParameter("expire_month").trim()));
		creditCard.setExpireYear(Integer.parseInt(request.getParameter("expire_year").trim()));
		creditCard.setNumber(request.getParameter("credit_card_number").trim());
		creditCard.setType(request.getParameter("credit_card_type").trim());
		creditCard.setCvv2(request.getParameter("credit_card_cvv2").trim());

		return creditCard.create(accessToken);
	}

	/**
	 * Creates payment using creditCardId
	 * @param creditCardId
	 * @param order
	 * @return
	 * @throws PayPalRESTException
	 */
	public static Payment createPayment(String creditCardId, Order order)
			throws PayPalRESTException {

		Payment payment = new Payment();

		// Create a payment object using PayPal as a payment method
		// This will involve PayPal redirection , where user authorization is
		// required
		if (AppConstants.PAYPAL.equalsIgnoreCase(order.getPaymentMethod())) {
			Details amountDetails = new Details();
			amountDetails.setShipping(order.getShipping());
			amountDetails.setSubtotal(order.getOrderAmount());
			amountDetails.setTax(order.getTax());

			Amount amount = new Amount();
			amount.setCurrency(order.getCurrency());
			Double total = Double.parseDouble(order.getTax())
					+ Double.parseDouble(order.getShipping())
					+ Double.parseDouble(order.getOrderAmount());
			amount.setTotal(String.format("%.2f", total));
			amount.setDetails(amountDetails);

			RedirectUrls redirectUrls = new RedirectUrls();
			redirectUrls.setCancelUrl(order.getCancelUrl());
			redirectUrls.setReturnUrl(order.getReturnUrl());

			Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			transaction.setDescription(order.getOrderDesc());
			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.add(transaction);

			Payer payer = new Payer();
			payer.setPaymentMethod(order.getPaymentMethod());

			payment.setIntent(order.getPaymentIntent());
			payment.setPayer(payer);
			payment.setRedirectUrls(redirectUrls);
			payment.setTransactions(transactions);
		}

		// Create a payment object using credit card as a payment method
		if (AppConstants.CREDIT_CARD.equalsIgnoreCase(order.getPaymentMethod())) {
			Details amountDetails = new Details();
			amountDetails.setShipping(order.getShipping());
			amountDetails.setSubtotal(order.getOrderAmount());
			amountDetails.setTax(order.getTax());

			Amount amount = new Amount();
			amount.setCurrency(order.getCurrency());
			Double total = Double.parseDouble(order.getTax())
					+ Double.parseDouble(order.getShipping())
					+ Double.parseDouble(order.getOrderAmount());
			amount.setTotal(String.format("%.2f", total));
			amount.setDetails(amountDetails);

			Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			transaction.setDescription(order.getOrderDesc());

			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.add(transaction);

			FundingInstrument fundingInstrument = new FundingInstrument();
			CreditCardToken creditCardToken = new CreditCardToken();
			creditCardToken.setCreditCardId(creditCardId);
			fundingInstrument.setCreditCardToken(creditCardToken);

			List<FundingInstrument> fundingInstrumentList = new ArrayList<FundingInstrument>();
			fundingInstrumentList.add(fundingInstrument);

			Payer payer = new Payer();
			payer.setFundingInstruments(fundingInstrumentList);
			payer.setPaymentMethod(order.getPaymentMethod());

			payment.setIntent(order.getPaymentIntent());
			payment.setPayer(payer);
			payment.setTransactions(transactions);
		}

		// set access token
		String accessToken = AccessTokenGenerator.getAccessToken();
		String requestId = UUID.randomUUID().toString();
		APIContext apiContext = new APIContext(accessToken, requestId);
		return payment.create(apiContext);
	}
}
