<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<script src="https://cdn.bootcss.com/react/15.4.2/react.min.js"></script>
<script src="https://cdn.bootcss.com/react/15.4.2/react-dom.min.js"></script>
<script
	src="https://cdn.bootcss.com/babel-standalone/6.22.1/babel.min.js"></script>

<style>
.App {
	text-align: center;
}

.App-logo {
	animation: App-logo-spin infinite 20s linear;
	height: 40vmin;
	pointer-events: none;
}

.App-header {
	background-color: #282c34;
	min-height: 100vh;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	font-size: calc(10px + 2vmin);
	color: white;
}

.App-link {
	color: #61dafb;
}

@
keyframes App-logo-spin {from { transform:rotate(0deg);
	
}

to {
	transform: rotate(360deg);
}

}
.Header {
	background-color: mediumaquamarine;
	padding: 20px 0px;
	margin-bottom: 20px
}

.Body {
	
}

.LeftBody {
	background-color: mediumaquamarine;
	float: left;
	width: 200px;
	margin-top: 20px;
}

.RightBody {
	float: left;
	width: calc(100% - 200px);
}

h1 {
	margin: 0px;
	text-align: center;
}

h2 {
	text-align: center;
}

.info {
	margin: 20px 40px;
	border: 5px solid mediumaquamarine;
}

.clear {
	clear: both;
}

ul {
	margin-block: 0em;
}

.menu {
	text-align: center;
}

.info1 {
	margin: 20px;
}

#result {
	color: #4CAF50;
}

#innerinfo {
}

.button {
  background-color: mediumaquamarine; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
}

.upload-btn-wrapper {
  position: relative;
  overflow: hidden;
  display: inline-block;
}

.btn {
  border: 2px solid gray;
  color: gray;
  background-color: white;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 20px;
  font-weight: bold;
}

.upload-btn-wrapper input[type=file] {
  font-size: 100px;
  position: absolute;
  left: 0;
  top: 0;
  opacity: 0;
}
</style>
</head>
<body style="margin: 0px;">
	<div>
		<div class="Header">
			<h1>CSV Checker</h1>
		</div>

		<div class="Body">
			<div class="LeftBody">
				<div>
					<h3 class="menu">Menu</h3>
					<ul>
						<li><a href="index.jsp">Upload</a></li>
						<li><a href="compare.jsp">Compare</a></li>
					</ul>
				</div>
			</div>

			<div class="RightBody">
				<div class="info">
					<div class="info1">
						<div id="result">
							<h3>${requestScope["message1"]}</h3>
						</div>
						<div class="innerinfo">
							<h3>Choose Files to Upload</h3>
							<form action="upload" method="post" enctype='multipart/form-data'>
							
								<input type="file" name="file" multiple /> <input type="submit"
									value="upload" />
							</form>
							<%
								if(request.getAttribute("download") != null){
									String secondFile = request.getAttribute("download").toString();
									out.println("<form action='download' method='post'>");
									out.println("<input type='hidden' name='sFile' value='"+secondFile+"'>");
									out.println("<input class='button' type='submit' name='submit' value='download' />");
									out.println("</form>");
								}
								%>
						</div>
						
					</div>
				</div>
			</div>
			<div class="clear"></div>


		</div>
	</div>

	<div></div>

</body>
</html>