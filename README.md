Pizza App using PayPal REST API
===============================

Overview
--------

This is a simple pizza store app that showcases the features of PayPal's REST APIs. The application uses the SDKs provided by PayPal.  The app demonstrates how you
   
    * Save a credit card with paypal for future payments.
    * Make a payment using a saved credit card id.
    * Make a payment using paypal as the payment method.

Pre-requisites
--------------

   * Java 5.0 or Higher
   * Apache maven 3.x for building
  
Running the app
---------------

   * Configure the service.EndPoint, clientID and clientSecret to appropriate values.
   * Execute the command 'mvn jetty:run' to deploy and run the app using maven jetty server plugin. (OR)
   * Run 'mvn clean package' to build and create 'rest-api-sample-app-java-1.0.war' file under the target directory.
   * Deploy the rest-api-sample-app-java-1.0.war to your favourite servlet container.
   * You are ready. Bring up http://localhost:<port>/rest-api-sample-app-java-1.0 on your favourite browser.	
	
References
----------

   * Github repository for the Java REST API SDK - https://github.com/paypal/rest-api-sdk-java.git

	 
