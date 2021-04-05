<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>
<%@page import="jdbc.connection.ConnectionProvider"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	try(Connection conn = ConnectionProvider.getConnection()){
		out.print("커넥션 연결 성공");
	}catch(SQLException ex){
		out.print("커넥션 연결 실패" + ex.getMessage());
	}
%>
</body>
</html>