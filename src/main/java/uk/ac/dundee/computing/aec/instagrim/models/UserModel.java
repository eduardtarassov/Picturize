/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.models;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;

/**
 * @author Administrator
 */
public class UserModel {

    private static final String LOGIN_QUERY = "select * from users where username=? and password=?";
    Connection conn;

    public UserModel() {
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }


    /* Registers user. Or updates existing user info*/
    public boolean registerUser(String[] strRequestParams, boolean initial) throws SQLException {
        AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        String encodedPassword = null;
        PreparedStatement ps = null;
        if (validateData(strRequestParams[0], strRequestParams[1])) {
            try {


                if (initial) {
                    strRequestParams[1] = sha1handler.SHA1(strRequestParams[1]);
                    ps = conn.prepareStatement("INSERT INTO users" +
                            "(username, password, first_name, last_name, email_address, secret_question, secret_answer) VALUES" +
                            "(?,?,?,?,?,?,?)");

                } else {
                    strRequestParams[0] = sha1handler.SHA1(strRequestParams[0]);
                    ps = conn.prepareStatement("UPDATE users SET password=?, first_name=?, last_name=?, email_address=?, secret_question=?, secret_answer=? WHERE username=?");
                }

                ps.setString(1, strRequestParams[0]);
                ps.setString(2, strRequestParams[1]);
                ps.setString(3, strRequestParams[2]);
                ps.setString(4, strRequestParams[3]);
                ps.setString(5, strRequestParams[4]);
                ps.setString(6, strRequestParams[5]);
                ps.setString(7, strRequestParams[6]);


                System.out.println("This is your ps: " + ps.toString());
                ps.executeUpdate();


            } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
                System.out.println("Can't check your password");
                return false;

            } catch (Exception e) {
                System.out.println("Error: Problem with connection.");
                e.printStackTrace();
                return false;
            } finally {
                ConnectionUtil.close(null, ps, null);
            }
        } else {
            System.out.println("ERROR: Username and password cannot be empty!");
            return false;
        }
        return true;
    }

    /*
        * fetches the request parameters and generates the insert query
        * which will be passed on to the executeQuery method of the ConnectionUtil.
        * Below is the code for the method
         */
    /*private String generateInsertQuery(String[] strRequestParams) {

        StringBuffer strQuery = new StringBuffer(REGISTER_QUERY_START);

        for (int i = 0; i < strRequestParams.length - 1; i++)
            strQuery.append(strRequestParams[i] + "', '");

        strQuery.append(strRequestParams[strRequestParams.length-1] + "')");
        /*strQuery.append(strRequestParams[0] + "', '" + strRequestParams[1] + "', '" + strRequestParams[2] +
                "', '" + strRequestParams[3] + "', '" + strRequestParams[4] + "', '" + strRequestParams[5] +
                "', '" + strRequestParams[6] + "')");*/

     /*   System.out.println("Insert query : " + strQuery.toString());

        return strQuery.toString();
    }
*/

    /**
     * Internally called to validate the request data. This method does a simple
     * check for userName and password to be entered. However
     * it can be extended to add more complex check at the server side like email address validity etc.
     * * * @return boolean indicating success / failure of the validation
     */
    private boolean validateData(String strUsername, String strPassword) {
        boolean isValid = false;
        if (strUsername != null && !strUsername.equals("") && strPassword != null && !strPassword.equals("")) {
            isValid = true;
        }
        return isValid;
    }


    public boolean IsValidUser(String strUsername, String strPassword) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;

        boolean isValid = false;
        AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        String encodedPassword;

        try {
            encodedPassword = sha1handler.SHA1(strPassword);

            ps = conn.prepareStatement(LOGIN_QUERY);
            ps.setString(1, strUsername);
            ps.setString(2, encodedPassword);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("User login and password are valid in DB");
                isValid = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(rs, ps, null);
        }

        return isValid;
    }

}
