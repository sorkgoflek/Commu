<%@page import="com.it.update.AndroidPasswordChangeController"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
  
<%
  	request.setCharacterEncoding("UTF-8");
  	
  	String id = request.getParameter("id");
  	String pass = request.getParameter("pass");
  	String result = "molla";
    
  	System.out.println(id);
  	System.out.println(pass);
  	
  	if(id !=null && pass != null ){
  		result = AndroidPasswordChangeController.androidPasswordChange(id,pass);
  	}
  %>  
<html>
<body>
<%=result %>
</body>
</html>