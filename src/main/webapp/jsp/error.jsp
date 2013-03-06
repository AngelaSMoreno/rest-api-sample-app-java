<%@ page isErrorPage="true" import="java.io.*" contentType="text/html"%>
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
      <div class="alert fade in alert-success"><button class="close" data-dismiss="alert">&times;</button>OOPS! an Error occured.  </div>
      <h2>Message:</h2>
      	<%=exception.getMessage()%>
    </div>
    
   <jsp:include page="footer.jsp"/>
   
    <script src="js/application.js" type="text/javascript"></script>
  </body>
</html>