/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;

/**
 * @author Administrator
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    private static final String REGISTRATION_PAGE = "/register.jsp";
    private static final String HOME_PAGE = "/index.jsp";


    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
    }


    /**
     * Fancy way of looking through all the request parameters and setting in request Attributes.
     *
     * @param request
     */
    private void setRequestAttributes(HttpServletRequest request) {
        Enumeration<String> enumKeys = request.getParameterNames();
        while (enumKeys.hasMoreElements()) {
            String key = enumKeys.nextElement();
            request.setAttribute(key, request.getParameter(key));
        }
    }


    /*
        * fetches the request parameters and generates the insert query
        * which will be passed on to the executeQuery method of the ConnectionUtil.
        * Below is the code for the method
         */
    private String[] generateRequestParams(HttpServletRequest request) {

        String[] strRequestParams = new String[7];

        strRequestParams[0] = request.getParameter("username");
        strRequestParams[1] = request.getParameter("password");
        strRequestParams[2] = request.getParameter("first_name");
        strRequestParams[3] = request.getParameter("last_name");
        strRequestParams[4] = request.getParameter("email_address");
        strRequestParams[5] = request.getParameter("secret_question");
        strRequestParams[6] = request.getParameter("secret_answer");

        return strRequestParams;
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {

       /* String strErrMsg = null;
        HttpSession session = request.getSession();
        RequestDispatcher reqDisp = request.getRequestDispatcher(REGISTRATION_PAGE);*/

        String[] strRequestParams = generateRequestParams(request);
        User us = new User();

        try {
            us.registerUser(strRequestParams);
        } catch (Exception e) {
            System.out.println("Error: Problem with connection.");
        }

        System.out.println("Insert into database successful");
        response.sendRedirect(HOME_PAGE);

    }
}