<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	<%@page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<script src="https://cdn.bootcss.com/react/15.4.2/react.min.js"></script>
<script src="https://cdn.bootcss.com/react/15.4.2/react-dom.min.js"></script>
<script
	src="https://cdn.bootcss.com/babel-standalone/6.22.1/babel.min.js"></script>

</head>
<body>
	<div>
		<h3>Choose Files to Upload</h3>
		<form action="upload" method="post" enctype="multipart/form-data">
			<input type="file" name="file" multiple /> <input type="submit"
				value="upload" />
		</form>
		<div id="result">
			<h3>${requestScope["message"]}</h3>
		</div>
		<form action="compare" method="post" enctype="multipart/form-data">
		<%if (request.getAttribute("files") != null)
		{
			ArrayList<String> fileList= (ArrayList<String>) request.getAttribute("files");
			
			out.println("<h3>Select The Main File</h3>");
  			for (String file: fileList) 
  			{   
				out.println("<input type='radio' name='name' value='"+ file +"'/>" + file + "<br>");
			}	
		}
		%>
		<input type="textbox" name="mainfile" value=""/>
			<input type="submit" name="submit" value="compare" />
		</form>
	</div>
</body>
</html>