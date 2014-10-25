<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Picturize - Registration</title>
       <!-- <link rel="stylesheet" type="text/css" href="Styles.css" />-->
    </head>
    <body>
        <header>
            <h2>Picturize - Register</h2>
        </header>
        <article>
            <h3>Register as user</h3>
            <form method="POST" action="Register">
                <ul>
                    <li>User Name <input type="text" name="username"></li>
                    <li>Password <input type="password" name="password"></li>
                    <!--<li>Repeat Password <input type="password" name="password_repeat"></li>-->
                    <li>First Name <input type="text" name="first_name"></li>
                    <li>Last Name <input type="text" name="last_name"></li>
                    <li>Email Address <input type="text" name="email_address"></li>
                    <li>Secret Question <input type="text" name="secret_question"></li>
                    <li>Secret Answer <input type="text" name="secret_answer"></li>
                </ul>
                <br/>
                <input type="submit" value="Register">
            </form>

        </article>
        <footer>
            <ul>
                <li class="footer"><a href="index.jsp">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
