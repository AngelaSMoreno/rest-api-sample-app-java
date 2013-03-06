package com.paypal.util;

import com.paypal.core.ConfigManager;
import com.paypal.core.rest.OAuthTokenCredential;
import com.paypal.core.rest.PayPalRESTException;

public class AccessTokenGenerator {
	private static String accessToken;

	public static String getAccessToken() throws PayPalRESTException {
		// ###AccessToken
		// Retrieve the access token from
		// OAuthTokenCredential by passing in
		// ClientID and ClientSecret
		if (accessToken == null) {

			// ClientID and ClientSecret retrieved from configuration
			String clientID = ConfigManager.getInstance().getValue("clientID");
			String clientSecret = ConfigManager.getInstance().getValue("clientSecret");
			accessToken = new OAuthTokenCredential(clientID, clientSecret).getAccessToken();
		}
		return accessToken;
	}
}
