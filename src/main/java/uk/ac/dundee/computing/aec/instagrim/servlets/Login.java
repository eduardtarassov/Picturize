/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This class handles the Business logic associated with the request.
 *
 * @author Administrator
 */
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {
    private static final String DB_NAME = "picturizedb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";


    private static final String HOME_PAGE = "/index.jsp";
    private static final String LOGIN_PAGE = "/login.jsp";

    //Cluster cluster = null;


    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        // cluster = CassandraHosts.getCluster();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String strUsername = request.getParameter("username");
        String strPassword = request.getParameter("password");
        String strErrMsg = null;
        HttpSession session = request.getSession();
        User us = new User();
        boolean isValidLogon = false;
        try {
            isValidLogon = us.IsValidUser(strUsername, strPassword);
            if(isValidLogon) {
                session.setAttribute("username", strUsername);
            } else {
                System.out.println("Username or Password is invalid. Please try again.");
            }
        } catch(Exception e) {
            System.out.println("Unable to validate user / password in database");
        }




        // Maybe it is better to put it into upper if statement
        if(isValidLogon) {
            session.setAttribute("Username", strUsername);
            response.sendRedirect(HOME_PAGE);
        } else {
            session.setAttribute("errorMsg", strErrMsg);
            response.sendRedirect(LOGIN_PAGE);
        }
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
