<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   
<!--------------------------------------------------->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<%
pageContext.setAttribute("result","hello"); //현재 페이지에서 저장해 쓰는법
%>
<body>
  <%=request.getAttribute("result")%>입니다.<br>
  ${requestScope.result}입니다.<br> <!-- Scope : 범위지정 -->
  ${names[0] }<br>
  ${names[1] }<br>
  ${notice.title}<br> <!-- map -->
  ${notice.id }<br>
  ${result }<br>
  ${empty param.n?'값이 비어있습니다.':param.n}<br> 
  ${param.n/2 }<br> <!-- 결과는 소수점 -->
  <!-- param : 파라미터 값 
  el 연산자 (lt gt le ge eq ne and) 
  el 연산자 (empty : null이거나 빈문자열일경우)
  el 연산자 (not 아닐경우)-->
  ${header.accept }<!-- 헤더정보 (관리자모드 네트워크) -->

	<!-- pageContext의 el사용은 get빼주고 사용해야함 -->
	${pageContext.request.method }
   <%--pageContext.getRequest().getMethod(); --%>
</body>
</html>