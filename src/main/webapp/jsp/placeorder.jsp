<!DOCTYPE html>
<html lang='en'>
  <head>
    <meta charset='utf-8'>
    <meta content='IE=Edge,chrome=1' http-equiv='X-UA-Compatible'>
    <meta content='width=device-width, initial-scale=1.0' name='viewport'>
    <title>PizzaShop</title>
    <meta content="authenticity_token" name="csrf-param" />
    <meta content="qwrDGHkE+0nFupMGhPsldQdBEqHi/JljGIsIcGWNiVo=" name="csrf-token" />
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
      <script src="//cdnjs.cloudflare.com/ajax/libs/html5shiv/3.6.1/html5shiv.js" type="text/javascript"></script>
    <![endif]-->
    <link href="css/application.css" media="all" rel="stylesheet" type="text/css" />
  </head>
  <body>
    
    <jsp:include page="header.jsp"/>
    
    <div class='container' id='content'>
      
      <h2>Place Order</h2>
      <form accept-charset="UTF-8" action="pay" class="simple_form form-horizontal new_order" id="new_order" method="post" novalidate="novalidate"><div style="margin:0;padding:0;display:inline"><input name="utf8" type="hidden" value="&#x2713;" /><input name="authenticity_token" type="hidden" value="qwrDGHkE+0nFupMGhPsldQdBEqHi/JljGIsIcGWNiVo=" /></div>
        
        <div class='control-group'>
          <label class="string optional control-label" for="order_amount">Amount</label>
          <div class='controls'>
            <label class='checkbox'>
              <%= request.getAttribute("orderAmount") %>
            </label>
            <input id="orderAmount" name="orderAmount" type="hidden" value="<%= request.getAttribute("orderAmount") %>" />
          </div>
        </div>
        <div class='control-group'>
          <label class="string optional control-label" for="ship_amount">Shipping</label>
          <div class='controls'><label class='checkbox'> 2.00</label></div>
        </div>
        <div class='control-group'>
          <label class="string optional control-label" for="tax_amount">Tax</label>
          <div class='controls'><label class='checkbox'> 1.00</label></div>
        </div>
        <div class='control-group'>
          <label class="string optional control-label" for="order_description">Description</label>
          <div class='controls'>
            <label class='checkbox'>
             <%= request.getAttribute("orderDesc") %>
            </label>
            <input id="orderDesc" name="orderDesc" type="hidden" value="<%= request.getAttribute("orderDesc") %>" />
          </div>
        </div>
        <div class="control-group select optional"><label class="select optional control-label" for="order_payment_method">Payment method</label>
        <div class="controls">
        	<select class="select optional" id="order_payment_method" name="order_payment_method">
        		<option value="credit_card">credit_card</option>
        		<option value="paypal">paypal</option>
        	</select>
        	<p class="help-block">Update your credit card in <a href='profile'>Profile</a> page</p>
        </div>
        </div>
        <div class='form-actions'>
          <input class="btn btn btn-primary" name="commit" type="submit" value="Place Order" />
        </div>
      </form>
    </div>
    
   <jsp:include page="footer.jsp"/>
   
    <script src="js/application.js" type="text/javascript"></script>
  </body>
</html>
