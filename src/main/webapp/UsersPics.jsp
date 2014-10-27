<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.containers.*" %>
<!DOCTYPE html>
<html>
<body>
<%String username = (String) request.getAttribute("user");%>
<header>
    <h2>Picturize - Pictures of user <%=(String) request.getAttribute("user")%>
    </h2>

</header>
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
    <a href="Image/<%=p.getID()%>"><img src="Thumb/<%=p.getID()%>"></a><br/>
    <a href="Processed/<%=p.getID()%>">Show Processed image</a>

    <p>Total likes: <%=p.getLikes()%>
    </p></br>

    <% if (!p.getUser().equals(LoginState.getUsername())) { %>
    <form action="/imageProcess" method="Post">
        <input type="text" name="imageProcess" value="<%=p.getID()%>" style="visibility:hidden">

        <input type="checkbox" name="imageProcess" value="Like">Like<br>

        <input type="submit" name="submit" value="Apply to image">
    </form>
    </br>
    </br>
    <%
                }


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
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Picturize - User Pictures</title>
    <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css"/>
</head>
</html>
