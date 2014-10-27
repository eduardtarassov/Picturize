<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>


<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="uk.ac.dundee.computing.aec.instagrim.containers.*" %>

<!DOCTYPE html>
<html>
<head>
    <title>Picturize - Home</title>
    <link rel="stylesheet" type="text/css" href="/Styles.css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body class="home">

<div class="search">
    <form method="POST" action="/Search">
        <table border="0" width="300" bgcolor="#e9f">
            <tr><td colspan=2 style="font-size:12pt;" align="center">
                <h3>Search User</h3></td></tr>
            <tr><td ><b>User Name</b></td>
                <td>: <input  type="text" name="username" id="username">
                </td></tr>
            <tr><td colspan=2 align="center">
                <input  type="submit" name="submit" value="Search"></td></tr>
        </table>
    </form>
</div>

<header>
    <h2>Picturize - Home</h2>
</header>
<!--<nav>-->
<ul>

    <li class="footer"><a href="/index.jsp">Home</a></li>

    <%

       // LoginState lg = (LoginState) session.getAttribute("LoginState");
        if (LoginState.getUsername() != null) {
            //String UserName = lg.getUsername();
            if (LoginState.getLoginState()) {
    %>
    <li><a href="/FileUpload.jsp">Upload Images and Videos</a></li>
    <li><a href="Images/<%=LoginState.getUsername()%>">Your Images</a></li>
    <li><a href="Profile/<%=LoginState.getUsername()%>">Your Profile</a></li>
    <li><a href="/Logout">Logout</a></li>
    <%
        }
    } else {
    %>
    <li><a href="/register.jsp">Register</a></li>
    <li><a href="/login.jsp">Login</a></li>

    <%


        }%>
</ul>

<!--</nav>-->
<footer>
    <ul>
        <!--<li class="footer"><a href="index.jsp">Home</a></li>      Changed the link-->
        <li>&COPY; Eduard Tarassov</li>
    </ul>
</footer>
</body>
</html>
