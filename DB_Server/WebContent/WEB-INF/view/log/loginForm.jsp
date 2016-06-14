<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>

<script type="text/javascript">
function logValueCheck(){
	var id = document.log.id.value;
	var pass = document.log.pass.value;
	
	if(id.length == 0 || id.trim().length == 0){
		alert("로그인할 아이디값 입력");
		document.log.id.focus();
		return false;
	}
	
	if(pass.length == 0 || pass.trim().length == 0){
		alert("로그인할 패스워드값 입력");
		document.log.pass.focus();
		return false;
	}
	
}
</script>

</head>
<body>
	<div align="right">
		<c:choose>
			<c:when test="${sessionScope.loginId eq null }">
				<form action="/it/member/login.it" method="post" name="log" onsubmit="return logValueCheck()">
					<b>ID</b><input type="text" size="10" name="id" />
					<b>PASS</b><input type="password" size="12" name="pass"/>
					<input type="submit" value="로그인"/>
					<input type="button" value="회원가입" onclick="location.href= '/it/member/register.it'">
				</form>
			</c:when>
			
			<c:otherwise>
				<b><i>${sessionScope.loginId }</i></b> 님 안녕하세요
				<input type="button" value="갤러리" onclick="location.href='/it/gallery/list.it'"/>
				<input type="button" value="갤러리 쓰기" onclick="location.href='/it/gallery/write.it'"/>
				<input type="button" value="업로드" onclick="location.href='/it/upload/test.it'"/>
				<input type="button" value="로그아웃" onclick="location.href= '/it/member/login.it'"/>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>