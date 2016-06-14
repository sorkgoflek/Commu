<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.it.insert.AndroidInsertController"%>  
<%
	request.setCharacterEncoding("UTF-8");
	
	String id = request.getParameter("id");
	String result = "molla";

	
	if(id !=null){
		result = AndroidInsertController.androidIdCheck(id);
		System.out.println("체크 :" + result);
	}
	
	  
%>     
<html>
<body>
<%=result %>
</body>
</html>