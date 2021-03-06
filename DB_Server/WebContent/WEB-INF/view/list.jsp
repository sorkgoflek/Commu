<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
<meta charset="UTF-8">
<title>Test</title>
<style type="text/css">


.img{
	width: 300px; 
	height: 300px;

}

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


.main_gallery{
	min-width: 1000px;
	margin-left: auto;
	margin-right: auto;
}
.main_gallery table{
	min-width: 100%;
	border-collapse: 0;
	border-spacing: 0;
	text-align: center;
}
.main_gallery table tr{
	height: 50px;
}
.main_gallery table th{
	border-top: 2px double #ddd;
	border-bottom: 2px double #ddd;
}
.main_gallery table td{
	border-bottom: 1px solid #ddd;
}
.main_gallery button{
	border: 0px;
	background: white;
	width: 100px;
	height: 50px;
	line-height: 50px;
}

</style>

</head>
<body>
	<div class="wrap">
		<div class="header" >
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
						<div class="hello" align="right">
							<b><i>${sessionScope.loginId }</i></b> 님 안녕하세요
						</div>
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
			

	<script type="text/javascript" src='/script/jquery-1.10.2.js'></script>
	<script type="text/javascript" src='/script/downloadProc.js'></script>


<div class="main_gallery">
	<br />
		
		<div align="right">
		<input type="button" value="갤러리 등록"
			onclick="location.href='/lance/gallery/write.it'">
		</div>
		
		<div align="center" >
			<table  class="f">
				<tr>
				
				<% int i=-1; %>
				<c:forEach var="vo" items="${list }">	
					<c:if test="${sessionScope.loginId == vo.writer }">
					<% i++;%>
					
					<%if(i != 0){ %>
						<%if(i%5 == 0){ %>
						 <tr>
						 <tr>
						<%} %>
					<% }%>
					<td>
						제목: ${vo.title }<br/>
						<a href="/lance/gallery/view.it?num=${vo.num }">
						
						<c:if test = "${vo.link1 eq null && vo.link2 ne null}">
							<img src="/lance${vo.link2 }" class="img" />
						</c:if>

						<c:if test = "${vo.link1 ne null && vo.link2 eq null}">
							<img src="/lance${vo.link1 }" class="img" />
						</c:if>
						
						<c:if test = "${vo.link1 ne null && vo.link2 ne null}">
							<img src="/lance${vo.link1 }" class="img" />
						</c:if>
						
						<c:if test = "${vo.link1 eq null && vo.link2 eq null}">
							${vo.content }
						</c:if>
						
						</a>
						<br />
						작성자: ${vo.writer }<br />
						작성일: <fmt:formatDate value="${vo.regDate }" type="date" pattern="yyyy.MM.dd aa hh:mm:ss" /><br />
					</td>	
					</c:if>
				</c:forEach>
				
				</tr>
			</table>
		</div>
		
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