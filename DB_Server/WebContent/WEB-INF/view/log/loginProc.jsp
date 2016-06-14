<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<c:choose>
	<c:when test="${rst eq true }">
		<script type="text/javascript">
			alert("로그인 성공");
			location.href("/it/index.it");
		</script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript">
			alert("아이디 또는 패스워드를 다시 확인하세요");
			history.back();
		</script>
	</c:otherwise>
</c:choose>
</html>

