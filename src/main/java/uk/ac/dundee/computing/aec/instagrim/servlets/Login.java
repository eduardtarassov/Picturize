/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

/**
 * This class handles the Business logic associated with the request.
 *
 * @author Administrator
 */
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {
    private static final String DBNAME = "picturizedb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    private static final String LOGIN_QUERY = "select * from users where username=? and password=?";
    private static final String HOME_PAGE = "/index.jsp";
    private static final String LOGIN_PAGE = "/login.jsp";

    //Cluster cluster = null;


    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
       // cluster = CassandraHosts.getCluster();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String strUserName = request.getParameter("username");
        String strPassword = request.getParameter("password");
        String strErrMsg = null;
        HttpSession session = request.getSession();
        boolean isValidLogon = false;
        try {
            isValidLogon = authenticateLogin(strUserName, strPassword);
            if(isValidLogon) {
                System.out.println("heelllooo");
                session.setAttribute("username", strUserName);
            } else {
                strErrMsg = "User name or Password is invalid. Please try again.";
            }
        } catch(Exception e) {
            strErrMsg = "Unable to validate user / password in database";
        }

        if(isValidLogon) {
            System.out.println("HEEEY");
            response.sendRedirect(HOME_PAGE);
        } else {
            session.setAttribute("errorMsg", strErrMsg);
            response.sendRedirect(LOGIN_PAGE);
        }

    }


    private boolean authenticateLogin(String strUserName, String strPassword) throws Exception {
        boolean isValid = false;
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement prepStmt = conn.prepareStatement(LOGIN_QUERY);
            prepStmt.setString(1, strUserName);
            prepStmt.setString(2, strPassword);
            ResultSet rs = prepStmt.executeQuery();
            if(rs.next()) {
                System.out.println("User login is valid in DB");
                isValid = true;
            }
        } catch(Exception e) {
            System.out.println("validateLogon: Error while validating password: "+e.getMessage());
            throw e;
        } finally {
            conn.close(); //closing the MySQL connection
        }
        return isValid;
    }

    private Connection getConnection() throws Exception {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/"+DBNAME+"?user="+DB_USERNAME+"&password="+DB_PASSWORD + "&useUnicode=true&characterEncoding=UTF-8";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url);
        } catch (SQLException sqle) {
            System.out.println("SQLException: Unable to open connection to db: "+sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        } catch(Exception e) {
            System.out.println("Exception: Unable to open connection to db: "+e.getMessage());
            e.printStackTrace(); //to get more information about the exception!
            throw e;
        }
        return conn;
    }





    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

