<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Picturize</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body class="home">
<div class="root">
    <div class="page">
<header class="top-bar">
    <div class="wrapper">
        <h1 class="logo">
            <a href="/">Picturize</a>
        </h1>

        <!--<div class="top-bar-left">
            <ul class="top-bar-actions">
<li>
    <h1 class="top-bar-home active-link">
        <a href="/">Home button</a>
    </h1>
</li>
            </ul>
        </div>-->
    </div>
</header>




    </div>
    </div>








        <header>
            <h1>InstaGrim</h1>
        </header>
        <!--<nav>-->
            <ul>

                <li class="footer"><a href="index.jsp">Home</a></li>

                    <%
                        
                        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                        if (lg != null) {
                            String UserName = lg.getUsername();
                            if (lg.getlogedin()) {
                    %>
                <li><a href="upload.jsp">Upload</a></li>
                <li><a href="UsersPics.jsp<%=lg.getUsername()%>">Your Images</a></li>
                    <%}
                            }else{
                                %>
                 <li><a href="register.jsp">Register</a></li>
                <li><a href="login.jsp">Login</a></li>

                <%
                                        
                            
                    }%>
                <li><a href="/Instagrim/Images/majed">Sample Images</a></li>
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
