<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<c:choose>
	<c:when test="${rst eq true }">
		<script type="text/javascript">
			alert("축하합니다 회원가입이 완료되었습니다");
			//history.back();
			location.href("/lance/index.it");
		</script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript">
			alert("ID 중복입니다");
			history.back();
		</script>
	</c:otherwise>
</c:choose>

</html>