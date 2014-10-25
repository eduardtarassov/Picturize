package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.containers.ProfileInfo;
import uk.ac.dundee.computing.aec.instagrim.models.SearchModel;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;
import uk.ac.dundee.computing.aec.instagrim.stores.ProfileInformation;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class Search extends HttpServlet {
    private DataSource dataSource = null;
    private Connection conn;

    public void init(ServletConfig config) throws ServletException {
        // Get DataSource
        dataSource = ConnectionUtil.getMySQLDataSource();

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html");
        //PrintWriter out = response.getWriter();

        try {
            conn = dataSource.getConnection();
            SearchModel searchModel = new SearchModel();
            searchModel.setConnection(conn);
            LinkedList<ProfileInfo> profiles = null;
            profiles = searchModel.getUsersForSearch(request.getParameter("username"));

            request.setAttribute("profiles_found", profiles);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
        }
        RequestDispatcher rd = request.getRequestDispatcher("/search.jsp");
        rd.forward(request, response);
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