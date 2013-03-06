<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="com.paypal.model.UserPaymentDetail" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang='en'>
  <head>
    <meta charset='utf-8'>
    <meta content='IE=Edge,chrome=1' http-equiv='X-UA-Compatible'>
    <meta content='width=device-width, initial-scale=1.0' name='viewport'>
    <title>PizzaShop</title>
    <meta content="authenticity_token" name="csrf-param" />
    <meta content="ldNYASxRj3d4z7aSLVOISQHfst5yH7GrxA8WpkPa2CI=" name="csrf-token" />
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
      <script src="//cdnjs.cloudflare.com/ajax/libs/html5shiv/3.6.1/html5shiv.js" type="text/javascript"></script>
    <![endif]-->
    <link href="css/application.css" media="all" rel="stylesheet" type="text/css" />
    <link href="css/favicon.ico" rel="shortcut icon" type="image/vnd.microsoft.icon" />
  </head>
  
  <body>
  
  <jsp:include page="header.jsp"/>
  <%
      List<UserPaymentDetail>  paymentList = (ArrayList<UserPaymentDetail>) request.getAttribute("paymentList");
  %>
    <div class='container' id='content'>
      <h2>Orders</h2>
      <table class='table'>
        <thead>
          <tr>
            <th>Payment ID</th>
            <th>Amount($)</th>
            <th>Payment Status</th>
            <th>Time</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>
         <% if(paymentList!=null && paymentList.size()>0){ 
         	for(UserPaymentDetail dtl:paymentList){
          %>
          <tr>
            <td><%= dtl.getPaymentId() %></td>
            <td><%= dtl.getPaymentAmount() %></td>
            <td><%= dtl.getPaymentStatus() %></td>
            <td><%= dtl.getPaymentDate() %></td>
            <td><%= dtl.getPaymentdescription()%></td>
          </tr>
         <% } } %> 
        </tbody>
      </table>
    </div>
    
   <jsp:include page="footer.jsp"/>
   
    <script src="js/application.js" type="text/javascript"></script>
  </body>
</html>
