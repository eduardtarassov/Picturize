
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
        java.util.LinkedList<Comm> lsComms = (java.util.LinkedList<Comm>) request.getAttribute("Comms");
        //request. = null;
        if (lsComms == null) {
    %>
    <p>No Comments found</p>
    <%
    } else {

        Iterator<Comm> iterator;
        iterator = lsComms.iterator();

        while (iterator.hasNext()) {
            Comm p = (Comm) iterator.next();

    %>


    <table style="width:100%">
        <caption>Comments to picture</caption>
        <tr>
            <th>User</th>
            <th>Comment</th>
        </tr>
        <tr>
            <td><%=p.getUser()%></td>
            <td><%=p.getComment()%></td>
        </tr>
    </table>
    </br>
    </br>

    <%
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


