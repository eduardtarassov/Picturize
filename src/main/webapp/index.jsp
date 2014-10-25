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
    <form method="POST" action="Search">
        <table border="0" width="300" align="center" bgcolor="#e9f">
            <tr><td colspan=2 style="font-size:12pt;" align="center">
                <h3>Search User</h3></td></tr>
            <tr><td ><b>User Name</b></td>
                <td>: <input  type="text" name="pid" id="pid">
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

    <li class="footer"><a href="index.jsp">Home</a></li>

    <%

        LoginState lg = (LoginState) session.getAttribute("LoginState");
        if (lg != null) {
            String UserName = lg.getUsername();
            if (lg.getLoginState()) {
    %>
    <li><a href="upload.jsp">Upload</a></li>
    <li><a href="Images/<%=lg.getUsername()%>">Your Images</a></li>
    <li><a href="Profile/<%=lg.getUsername()%>">Your Profile</a></li>
    <li><a href="Logout">Logout</a></li>
    <%
        }
    } else {
    %>
    <li><a href="register.jsp">Register</a></li>
    <li><a href="login.jsp">Login</a></li>

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
