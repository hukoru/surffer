<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html class="no-js" lang="en">
<head>
	<title>Product</title>
</head>
<body>
	<h1>Product List:</h1>
	<ul>
		<c:forEach items="${product}" var="prod">
			<li>${prod.siteProdId} - ${prod.siteProdUrl}- ${prod.price}</li>
		</c:forEach>
	</ul>
	<a href="<%= request.getContextPath() %>">home</a>
</body>
</html>