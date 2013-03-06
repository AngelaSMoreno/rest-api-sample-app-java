Pizza App using PayPal RESTful API
==================================

Overview
--------

This is a simple pizza store app that showcases the features of PayPal's RESTful APIs. The application uses the SDKs provided by PayPal.  The app demonstrates how you
   
    * Save a credit card with paypal for future payments.
    * Make a payment using a saved credit card id.
    * Make a payment using paypal as the payment method.

Pre-requisites
--------------

   * Java 5.0 or Higher
   * Apache maven 3.x for building
  
Running the app
---------------

   * Copy the file privatestore found under src/main/resources to your favourite location, and paste the location in the sdk_config.properties file found in the same directory. Ex: if you have copied the file privatestore in C:/Foo folder you have to update the parameter as trustStorePath=C:/Foo/privatestore in the sdk_config.properties. This validates the self-signed certificates used in our staging servers.
   * Change the dbPath parameter to point to any convenient folder location. Ex: if the configuration looks like dbPath=C:/PaymentDB/testpaymentdb, 'PaymentDB' is the name of folder you should create, database 'testpaymentdb' will be created by the application.
   * Configure the service.EndPoint, oauth.EndPoint, clientID and clientSecret to appropriate values.
   * Run 'mvn clean package' to build and create 'rest-api-sample-app-java-1.0.war' file under the target directory.
   * Deploy the rest-api-sample-app-java-1.0.war to your favourite servlet container.
   * You are ready. Bring up http://localhost:<port>/rest-api-sample-app-java-1.0 on your favourite browser.	
	
References
----------

   * RESTful API SDK repository - https://github.paypal.com/DevTools/rest-api-sdk-java.git

	 