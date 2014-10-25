/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.models.UserModel;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;
import uk.ac.dundee.computing.aec.instagrim.containers.LoginState;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;


/**
 * This class handles the Business logic associated with the request.
 *
 * @author Administrator
 */
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {


    private static final String HOME_PAGE = "/index.jsp";
    private static final String LOGIN_PAGE = "/login.jsp";

    private DataSource dataSource = null;
    private Connection conn;

    public void init(ServletConfig config) throws ServletException {
        // Get DataSource
        dataSource = ConnectionUtil.getMySQLDataSource();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String strUsername = request.getParameter("username");
        String strPassword = request.getParameter("password");
        String strErrMsg = null;
        HttpSession session = request.getSession();
        UserModel us = new UserModel();
        boolean isValidLogon = false;
        try {
            conn = dataSource.getConnection();
            us.setConnection(conn);
            isValidLogon = us.IsValidUser(strUsername, strPassword);
            if (isValidLogon) {
                session.setAttribute("username", strUsername);
            } else {
                System.out.println("Username or Password is invalid. Please try again.");
            }


            // Maybe it is better to put it into upper if statement
            if (isValidLogon) {
                LoginState lg = new LoginState();
                lg.setLoginState(true);
                lg.setUsername(strUsername);

                session.setAttribute("LoginState", lg);
                RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
                rd.forward(request, response);
            } else {
                session.setAttribute("errorMsg", strErrMsg);
                response.sendRedirect(LOGIN_PAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
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
