<%@ page import="java.util.*" %>
<%@ page import="com.paypal.model.ErrorMessage" %>

<%
  boolean isSessionActive = false;
  if(session.getAttribute("isSessionActive")!=null){
  	isSessionActive = (Boolean)session.getAttribute("isSessionActive");
  }
  
  ErrorMessage error = (ErrorMessage)request.getAttribute("error");
  
%>

<div class='navbar navbar-static-top'>
	<div class='navbar-inner'>
		<div class='container'>
			<a class='btn btn-navbar' data-target='.nav-collapse'
				data-toggle='collapse'> <span class='icon-bar'></span> <span
				class='icon-bar'></span> <span class='icon-bar'></span>
			</a>
			<ul class='nav pull-right'>
			  <% if(isSessionActive){%>
			  	<li><a href="profile">Profile</a></li>
                <li><a href="signout">SignOut</a></li>
			  <%}else{%>
			  	<li><a href="signin">SignIn</a></li>
				<li><a href="signup">SignUp</a></li>
			   <%}%>	
			</ul>
			<a class='brand' href='#'>PizzaShop</a>
			<div class='nav-collapse'>
				<ul class='nav'>
					<li><a href="home">Home</a></li>
					<% if(isSessionActive){%>
						<li><a href="showorders">Orders</a></li>
					<%}%>
				</ul>
			</div>
		</div>
	</div>
</div>
<%if(error !=null){ %>
<div class='container'>
 	<div class="alert fade in alert-error">
 	<ul>
 	<% Iterator itr =error.getMessageList().iterator();
		while(itr.hasNext()){ %> 
		
		 <li><%= itr.next() %></li>
		
	<%	}%>
	</ul>
	</div>
</div>

<%}%>