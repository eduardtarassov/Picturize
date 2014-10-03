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


import uk.ac.dundee.computing.aec.instagrim.db.utils.ConnectionUtil;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    private static final String REGISTRATION_PAGE = "/register.jsp";
    private static final String HOME_PAGE = "/index.jsp";
    private static final String INSERT_QUERY_START = "insert into users values ('";

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
    }

    /**
     * Internally called to validate the request data. This method does a simple
     * check for userName and password to be entered. However
     * it can be extended to add more complex check at the server side like email address validity etc.
     * @param request - HttpServletRequest object
     * @return boolean indicating success / failure of the validation
     */
    private boolean validateData(HttpServletRequest request) {
        boolean isValid = false;
        String strUserName = request.getParameter("username");
        String strPassword = request.getParameter("password");

        if(strUserName!=null && !strUserName.equals("") && strPassword!=null && !strPassword.equals("")) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Fancy way of looking through all the request parameters and setting in request Attributes.
     * @param request
     */
    private void setRequestAttributes(HttpServletRequest request) {
        Enumeration<String>  enumKeys =  request.getParameterNames();
        while(enumKeys.hasMoreElements()) {
            String key  = enumKeys.nextElement();
            request.setAttribute(key, request.getParameter(key))  ;
        }
    }




    /*
    * fetches the request parameters and generates the insert query
    * which will be passed on to the executeQuery method of the ConnectionUtil.
    * Below is the code for the method
     */
    private String generateInsertQuery(HttpServletRequest request) {
        String strUserName = request.getParameter("username");
        String strPassword = request.getParameter("password");
        String strFirstName = request.getParameter("first_name");
        String strLastName = request.getParameter("last_name");
        String strEmail = request.getParameter("email_address");
        String strSecretQuestion = request.getParameter("secret_question");
        String strSecretAnswer = request.getParameter("secret_answer");

        StringBuffer strQuery = new StringBuffer(INSERT_QUERY_START);
        strQuery.append(strUserName);
        strQuery.append("', '");
        strQuery.append(strPassword);
        strQuery.append("', '");
        strQuery.append(strFirstName);
        strQuery.append("', '");
        strQuery.append(strLastName);
        strQuery.append("', '");
        strQuery.append(strEmail);
        strQuery.append("', '");
        strQuery.append(strSecretQuestion);
        strQuery.append("', '");
        strQuery.append(strSecretAnswer);
        strQuery.append("')");

        System.out.println("Insert query : " + strQuery.toString());

        return strQuery.toString();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {

        String strUserMsg = null;
        HttpSession session = request.getSession();
        RequestDispatcher reqDisp =  request.getRequestDispatcher(REGISTRATION_PAGE);

        try {
            //Check if data is valid
            if(validateData(request)) {
                ConnectionUtil.executeQuery(generateInsertQuery(request));
                System.out.println("Insert into database successful");
                session.setAttribute("username", request.getParameter("username"));
                response.sendRedirect(HOME_PAGE);
            } else {//If data is invalid
                strUserMsg = "Username and Password cannot be empty";

                setRequestAttributes(request);
                request.setAttribute("userMsg", strUserMsg);
                reqDisp.forward(request, response);
            }

        } catch(SQLException sqle ) {
            System.out.println("Unable to register user: "+sqle.getMessage());
            sqle.printStackTrace();
            //Check if we are getting duplicate key exception on userName
            if(sqle.getMessage().indexOf("Duplicate entry")!=-1) {
                System.out.println("User already exists");
                strUserMsg = "User name "+request.getParameter("username")+" already " +
                        "exists. Please try another user name.";
            } else { //If other SQLException than dup key exception
                strUserMsg = "Unable to register user "+request.getParameter("userName")+
                        ". Please try again later.";
            }
            setRequestAttributes(request);
            request.setAttribute("userMsg", strUserMsg);
            reqDisp.forward(request, response);

        } catch(Exception e) {//If it goes into Exception other than SQLException
            System.out.println("Unable to register user: "+e.getMessage());
            strUserMsg = "Unable to register user "+request.getParameter("userName")
                    +". Please try again later.";
            setRequestAttributes(request);
            request.setAttribute("userMsg", strUserMsg);
            reqDisp.forward(request, response);

        }



    }
}