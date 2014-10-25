<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.containers.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Picturize - User Pictures</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
    </head>
    <body>
    <%String username = (String) request.getAttribute("user");%>
    <header>
        <h2>Picturize - Pictures of user</h2>
    </header>
    <h2><%=(String) request.getAttribute("user")%></h2>
        <article>
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            //request. = null;
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
        } else {

            Iterator<Pic> iterator;
            iterator = lsPics.iterator();

            while (iterator.hasNext()) {
                Pic p = (Pic) iterator.next();

        %>
            <p>This is the picture</p>
        <a href="Image/<%=p.getID()%>" ><img src="Thumb/<%=p.getID()%>"></a><br/><%

            }
            }
        %>
        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
