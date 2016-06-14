<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta charset="UTF-8">
<title>Test</title>
<style type="text/css">
body{
	margin: 0px;
	padding: 0px;
	background: #00D8FF;
	font-family:'맑은 고딕';
}
.wrap{
	width: 100%;
	margin: 0px;
	padding: 0px;
	height: 100%;
	background: #00D8FF; 
}
.header{
	min-width: 1700px;
	max-width: 1700px;
	min-height: 100px;
	max-height: 100px;
	background: white;
	border-bottom-left-radius: 30px;
	border-bottom-right-radius: 30px;
	margin-left: auto;
	margin-right: auto;
}
.main{
	min-width: 1700px;
	max-width: 1700px;
	min-height: 700px;
	margin-left: auto;
	margin-right: auto;
	border-top-left-radius: 30px;
	border-top-right-radius: 30px;
	border-bottom-left-radius: 30px;
	border-bottom-right-radius: 30px;
	background: white;
	margin-top: 30px;
	margin-bottom: 30px;
}
.footer{
	min-width: 1700px;
	max-width: 1700px;
	min-height: 100px;
	max-height: 100px;
	margin-left: auto;
	margin-right: auto;
	border-top-left-radius: 30px;
	border-top-right-radius: 30px;
	background: white;
	text-align: center;
}
.header h1{
	margin-left: 30px;
	width: 150px;
	padding-right: 0px;
	margin-right: 0px;
}
a:LINK {
	color: black;
	text-decoration: none;
}
a:HOVER {
	color: black;
	text-decoration: none;
}
a:VISITED {
	color: black;
	text-decoration: none;
}
.header span{
	float: right;
	margin-right: 10px;
}
.menu{
	width: 970px;
	float: right;
	margin-right: 40px;
}
.menu ul li{
	list-style: none;
	float: left;
	display: inline-block;
	text-align: center;
	width: 150px;
	border-right: 1px solid #ddd;
}
.menu ul li h3{
	height: 50px;
	line-height: 50px;
	display: block;
	margin: 0px;
	padding: 0px;	
}
.menu ul li h3 a{
	width: 150px;
	display: block;
}
.menu ul li h3 a:HOVER {
	background: #62D843;
}

.menuOption{
	width: 970px;
	float: right;
	margin-right: 40px;
}
.menuOption ul li{
	list-style: none;
	float: left;
	display: inline-block;
	text-align: center;
	width: 150px;
	border-right: 1px solid #ddd;
}
.menuOption ul li h3{
	height: 40px;
	line-height: 40px;
	display: block;
	margin: 0px;
	padding: 0px;	
}
.menuOption ul li h3 a{
	width: 150px;
	display: block;
}
.menuOption ul li h3 a:HOVER {
	background: #62D843;
}
</style>



</head>
<body>
	<div class="wrap">
		<div class="header">
			<c:choose>
			<c:when test="${sessionScope.loginId eq null }">
					<div class="menu" align="right">
							<br/>
							<form action="/lance/member/login.it" method="post" name="log" onsubmit="return logValueCheck()">
								<b>ID </b><input type="text" size="10" name="id" />
								<b>PASS </b><input type="password" size="12" name="pass"/>
								<input type="submit" value="로그인"/>
								<input type="button" value="회원가입" onclick="location.href= '/lance/member/register.it'">
							</form>
						
					</div>
				</c:when>
				<c:otherwise>
					<div class="menuOption" >
					<ul>
					   
						<li><h3><a href="/lance/gallery/list.it">갤러리</a></h3></li>
						<li><h3><a href="/lance/gallery/write.it">갤러리등록</a></h3></li>	
						<li><h3><a href="/lance/member/login.it">로그아웃</a></h3></li>
					</ul>
					</div>
				</c:otherwise>
				</c:choose>
			<br />
				
			<h1><a href="/lance/index.it">Commu</a></h1>
		</div>
		<div class="main">
			
<style type="text/css">
.regForm{
	width: 1000px;
	max-width: 1000px;
	margin-left: auto;
	margin-right: auto;
}
.regForm table{
	width: 100%;
	max-width: 1000px;
	border-bottom: 2px double #ddd;
	border-top: 2px double #ddd;
}
.regForm table tr{
	height: 50px;
	line-height: 50px;
}
</style>
<script type="text/javascript">
function joinValueCheck(){
	var id = document.join.id.value;
	var pass = document.join.pass.value;
	var name = document.join.name.value;
	var phone_number = document.join.phone_number.value;
	
	if(id.length == 0 || id.trim().length == 0){
		alert("아이디 입력");
		document.join.id.focus();
		return false;
	}
	
	if(pass.length == 0 || pass.trim().length == 0){
		alert("패스워드 입력");
		document.join.pass.focus();
		return false;
	}
	
	if(name.length == 0 || name.trim().length == 0){
		alert("이름 입력");
		document.join.name.focus();
		return false;
	}
	
	if(phone_number.length == 0 || phone_number.trim().length == 0){
		alert("핸드폰번호 입력");
		document.join.phone_number.focus();
		return false;
	}
	
}
function idcheck(){
	//alert("아이디체크");
	var idConfirm = document.join.id.value;
	
	if(idConfirm.length == 0 || idConfirm.trim().length == 0){
		alert("아이디 입력 후 중복확인하세요");
		document.join.id.focus();
		return false;
	}
	else{
		location.href = "/lance/member/idcheck.it?id=" + idConfirm ;
	}
}
</script>

<div align="center" class="regForm">
	<br />
	<br />
	<h1>회원가입</h1>
	<form method="post" action="/lance/member/register.it" name="join" onsubmit="return joinValueCheck()">
		<table>
			<tr>
				<td class="td1"><b>아이디&nbsp;&nbsp;<font color="orange">*</font></b></td>
				<td class="td2"><input id="id" name="id" type="text" type="text" value=""/></td>
				<td ><input type="button" value="아이디 중복확인" onclick="idcheck()"/></td>
			</tr>
			<tr>
				<td class="td1"><b>비밀번호&nbsp;&nbsp;<font color="orange">*</font></b></td>
				<td class="td2"><input id="pass" name="pass" type="password" type="text" value=""/></td>
			</tr>
			<tr>
				<td class="td1"><b>이름&nbsp;&nbsp;<font color="orange">*</font></b></td>
				<td class="td2"><input id="name" name="name" type="text" type="text" value=""/></td>
			</tr>
			<tr>
				<td class="td1"><b>핸드폰번호&nbsp;&nbsp;<font color="orange">*</font></b></td>
				<td class="td2"><input id="phone_number" name="phone_number" type="text" type="text" value=""/></td>
				<td class="td3"><font style="color:red; font-size: 13px; float: left;">번호에 '-'를 붙여주세요</font></td>
			</tr>
			
			<tr>
				<td class="td1"><b>성별&nbsp;&nbsp;<font color="orange">*</font></b></td>
				<td class="td2"><input type="radio" name="gender" value="여자">여자&nbsp; 
								<input type="radio" name="gender" value="남자" checked="checked">남자</td>
			</tr>
			
		</table>
			<input type="submit" value="가입신청" />&nbsp;&nbsp;
			<input type="reset" value="재작성" />
	</form>
	<br />
	<br />
</div>
		</div>
		
		<div class="footer">
			<br />
			<br />
			
		</div>
	</div>
</body>
</html>