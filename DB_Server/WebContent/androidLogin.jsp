<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.it.login.AndroidLoginController"%>   
<%
	request.setCharacterEncoding("UTF-8");
	
	String id = request.getParameter("id");
	String pass = request.getParameter("pass");
	String result = "molla";
  
	
	if(id !=null && pass != null ){
		result = AndroidLoginController.androidLogin(id,pass);
		
	}
	
	
%>  
<html>
<body>
<%=result %>
</body>
</html>