package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.sql.DataSource;

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = {
        "/Image",
        "/Image/*",
        "/Thumb/*",
        "/Images",
        "/Images/*"
})
@MultipartConfig

public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private HashMap<String, Integer> CommandsMap = new HashMap<String, Integer>();

    private DataSource dataSource = null;
    private Connection conn;


    public void init(ServletConfig config) throws ServletException {
        // Get DataSource
        dataSource = ConnectionUtil.getMySQLDataSource();

    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() {
        super();

        // TODO Auto-generated constructor stub
        CommandsMap.put("Image", 1);
        CommandsMap.put("Images", 2);
        CommandsMap.put("Thumb", 3);

    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

       // TODO Auto-generated method stub
        System.out.println("This is your request path: " + request);
        // Splitting and decoding the request path into arguments.
        String args[] = Convertors.SplitRequestPath(request);

        System.out.println(args[0]);

        for (String name: CommandsMap.keySet()){

            String value = CommandsMap.get(name).toString();
            System.out.println(name + " ------ " + value);


        }

        switch (args[0]) {
            case "Image":
                DisplayImage(Convertors.DISPLAY_PROCESSED,args[1], response);
                break;
            case "Images":
                DisplayImageList(args[1], request, response);
                break;
            case "Thumb":
                DisplayImage(Convertors.DISPLAY_THUMB,args[1],  response);
                break;
            default:
                error("Bad Operator", response);
        }
    }

    private void DisplayImageList(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Display Image list");
        PicModel tm = new PicModel();
        RequestDispatcher rd = null;
        try {
            conn = dataSource.getConnection();
            tm.setConnection(this.conn);
            LinkedList<Pic> lsPics = tm.getPicsForUser(User);
             rd = request.getRequestDispatcher("/UsersPics.jsp");
            request.setAttribute("Pics", lsPics);
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, null, conn);
        }


        rd.forward(request, response);

   }

    private void DisplayImage(int type, String Image, HttpServletResponse response) throws ServletException, IOException {
       /* PicModel tm = new PicModel();
        tm.setCluster(cluster);
  
        
        Pic p = tm.getPic(type,java.util.UUID.fromString(Image));
        
        OutputStream out = response.getOutputStream();

        response.setContentType(p.getType());
        response.setContentLength(p.getLength());
        //out.write(Image);
        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
        }
        out.close();*/
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());

            String type = part.getContentType();
            String filename = part.getSubmittedFileName();

            System.out.println("This is the file type: " + type);
            System.out.println("This is the filename: " + filename);

            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            String username = "null";
            if (lg.getlogedin()) {
                username = lg.getUsername();
                System.out.println("Current user: " + username);
            }
            if (i > 0) {
                byte[] b = new byte[i + 1];
                is.read(b);
                System.out.println("Length : " + b.length);
                PicModel tm = new PicModel();
                try {
                    conn = dataSource.getConnection();
                    tm.setConnection(conn);
                    tm.insertPic(b, type, filename, username);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ConnectionUtil.close(null, null, conn);
                }



                is.close();
            }
            RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");
            rd.forward(request, response);
        }

    }

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have a na error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
        return;
    }
}
