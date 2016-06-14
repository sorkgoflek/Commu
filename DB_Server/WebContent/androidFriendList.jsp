<%@page import="java.util.ArrayList"%>
<%@page import="com.it.friendlist.AndroidFriendListController"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="utf-8"%>
<%
	request.setCharacterEncoding("UTF-8");
	ArrayList<String> list = new ArrayList<String>();

	list = AndroidFriendListController.androidFriendList();
%>
<html>
<body>
<%=list %>
</body>
</html>