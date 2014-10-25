package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.containers.ProfileInfo;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.ProfileModel;
import uk.ac.dundee.computing.aec.instagrim.models.UserModel;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by Eduard on 15/10/2014.
 */
@WebServlet(urlPatterns = {
        "/Profile/*",
        "/UpdateProfileInformation/*",
})
@MultipartConfig

public class Profile extends HttpServlet {
    private DataSource dataSource = null;
    private Connection conn;
    private ProfileModel pModel;

    private static final String HOME_PAGE = "/index.jsp";

    public void init(ServletConfig config) throws ServletException {
        // Get DataSource
        dataSource = ConnectionUtil.getMySQLDataSource();

    }

    private String[] generateRequestParams(HttpServletRequest request) {

        String[] strRequestParams = new String[7];

        strRequestParams[0] = request.getParameter("password");
        strRequestParams[1] = request.getParameter("first_name");
        strRequestParams[2] = request.getParameter("last_name");
        strRequestParams[3] = request.getParameter("email_address");
        strRequestParams[4] = request.getParameter("secret_question");
        strRequestParams[5] = request.getParameter("secret_answer");
        strRequestParams[6] = request.getParameter("username");

        return strRequestParams;
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] strRequestParams = generateRequestParams(request);
        UserModel us = new UserModel();

        try {
            conn = dataSource.getConnection();
            us.setConnection(conn);
            us.registerUser(strRequestParams, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
        }

        System.out.println("Insert into database successful");
        response.sendRedirect(HOME_PAGE);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String args[] = Convertors.SplitRequestPath(request);
        try {
            conn = dataSource.getConnection();
            pModel = new ProfileModel();
            pModel.setConnection(this.conn);

                    displayProfile(args[1], request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
        }

    }


    private void displayProfile(String username, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Display Profile information");
        RequestDispatcher rd = null;
        ProfileInfo proInfo = pModel.getProfileInfo(username);
        rd = request.getRequestDispatcher("/profile.jsp");
        request.setAttribute("ProfileInfo", proInfo);

        rd.forward(request, response);
    }
}
