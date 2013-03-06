<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
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
     
    <div class='container' id='content'>
      
      <h2>Sign in</h2>
      <form accept-charset="UTF-8" class="simple_form form-horizontal new_user" id="new_user" method="post" novalidate="novalidate"><div style="margin:0;padding:0;display:inline"><input name="utf8" type="hidden" value="&#x2713;" /><input name="authenticity_token" type="hidden" value="ldNYASxRj3d4z7aSLVOISQHfst5yH7GrxA8WpkPa2CI=" /></div>
        <div class="control-group email optional"><label class="email optional control-label" for="user_email">Email</label><div class="controls"><input autofocus="autofocus" class="string email optional" id="user_email" name="user_email" size="50" type="email" value="" /></div></div>
        <div class="control-group password optional"><label class="password optional control-label" for="user_password">Password</label><div class="controls"><input class="password optional" id="user_password" name="user_password" size="50" type="password" /></div></div>

        <div class='form-actions'>
          <input class="btn btn btn-primary" name="commit" type="submit" value="Sign in" />
        </div>
      </form>
      
        <a href="signup">Sign up</a><br />
      
       <!-- <a href="/users/password/new">Forgot your password?</a><br />  --> 
    </div>
  
   <jsp:include page="footer.jsp"/>
   
   <script src="js/application.js" type="text/javascript"></script>
  </body>
</html>
