package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by Eduard on 26/10/2014.
 */

public class ImageProcess extends HttpServlet {

    private DataSource dataSource = null;
    private Connection conn;

    public void init(ServletConfig config) throws ServletException {
        // Get DataSource
        dataSource = ConnectionUtil.getMySQLDataSource();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] processCommands = request.getParameterValues("imageProcess");
        PicModel tm = new PicModel();
        RequestDispatcher rd = null;
        for (int i = 0; i < processCommands.length; i++)
            System.out.println("ProcessComand value: " + processCommands[i]);


        try {
            conn = dataSource.getConnection();
            tm.setConnection(this.conn);
            for (int i = 1; i < processCommands.length; i++) {
                if (processCommands[i].equals("Like"))
                    tm.likePic(processCommands[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
        }

        rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        }
}
