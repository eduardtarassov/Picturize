package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.containers.Comm;
import uk.ac.dundee.computing.aec.instagrim.containers.Pic;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
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
import java.util.LinkedList;

/**
 * Created by Eduard on 06/11/2014.
 */

@WebServlet(urlPatterns = {
        "/Images/Comments/*"
})
@MultipartConfig

public class Comment extends HttpServlet {

    private DataSource dataSource = null;
    private Connection conn;


    public void init(ServletConfig config) throws ServletException {
        // Get DataSource
        dataSource = ConnectionUtil.getMySQLDataSource();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] processCommands = request.getParameterValues("comment");
        PicModel tm = new PicModel();
        RequestDispatcher rd = null;
        for (int i = 0; i < processCommands.length; i++)
            System.out.println("ProcessComand value: " + processCommands[i]);


        try {
            conn = dataSource.getConnection();
            tm.setConnection(this.conn);

            tm.picComment(processCommands[0], processCommands[1], processCommands[2]);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
        }

        rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);

        displayCommentsList(args[1], request, response);

    }


    private void displayCommentsList(String picid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Display Comments list");
        PicModel tm = new PicModel();
        RequestDispatcher rd = null;
        try {
            conn = dataSource.getConnection();
            tm.setConnection(this.conn);
            LinkedList<Comm> lsComms = tm.getComments(picid);
            rd = request.getRequestDispatcher("/comments.jsp");
            request.setAttribute("Comms", lsComms);
            System.out.println("This is your test path info: " + request.getPathInfo());
            rd.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
        }
    }
   /* private void displayCommentsList(String picid, HttpServletRequest request, HttpServletResponse response){
        System.out.println("Display COmments list");
        PicModel tm = new PicModel();
        RequestDispatcher rd = null;
        try {
            conn = dataSource.getConnection();
            tm.setConnection(this.conn);
            //LinkedList<Comm> lsComms = tm.getComments(picid);
            rd = request.getRequestDispatcher("/comments.jsp");
            request.setAttribute("Comments", lsComms);
            System.out.println("This is your test path info: " + request.getPathInfo());
            //String args[] = Convertors.SplitRequestPath(request);
            rd.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
        }
    }*/
}

