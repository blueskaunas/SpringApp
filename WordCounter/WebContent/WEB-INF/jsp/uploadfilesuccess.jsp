<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Upload</title>
<style type="text/css">
body {
	background-image:
		url('http://cdn3.crunchify.com/wp-content/uploads/2013/03/Crunchify.bg_.300.png');
}
</style>
</head>
<body>
	<br>
	<br>
	<div align="center">
		<h1>Upload</h1>
		<p>Files uploaded successfully.</p>
		<ol>
			<c:forEach items="${files}" var="file">
           - ${file} <br>
			</c:forEach>
		</ol>
		<br /> <br />
	</div>
</body>
</html>