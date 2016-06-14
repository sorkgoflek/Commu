<%@page import="com.it.delete.AndroidDeleteController"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	request.setCharacterEncoding("UTF-8");
	
	String id = request.getParameter("id");
	System.out.println("회원탈퇴아디:"+id);
	int result = 999;
  
	if(id !=null ){
		result = AndroidDeleteController.androidDelete(id);
		
	}
	
	
%>  
<html>
<body>
<%=result %>
</body>
</html>