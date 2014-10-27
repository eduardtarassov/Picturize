package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.containers.LoginState;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.VidModel;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

/**
 * Created by Eduard on 26/10/2014.
 */

@MultipartConfig
public class Upload extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private DataSource dataSource = null;
    private Connection conn;


    public void init(ServletConfig config) throws ServletException {
        // Get DataSource
        dataSource = ConnectionUtil.getMySQLDataSource();

    }

    public Upload() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());

            String type = part.getContentType();
            String filename = part.getSubmittedFileName();

            System.out.println("This is the file type: " + type);
            System.out.println("This is the filename: " + filename);

            InputStream is = request.getPart(part.getName()).getInputStream();


            //int i = is.available();
            HttpSession session = request.getSession();
            String username = "null";
            if (LoginState.getLoginState()) {
                username = LoginState.getUsername();
                System.out.println("Current user: " + username);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            for (int readNum; (readNum = is.read(buff)) != -1; ) {
                baos.write(buff, 0, readNum);
            }
            byte[] file = baos.toByteArray();

            try {

                conn = dataSource.getConnection();
                if (type.contains("image")) {
                    PicModel pm = new PicModel();
                    pm.setConnection(conn);
                    pm.insertPic(file, type, filename, username);
                } else {
                    VidModel vm = new VidModel();
                    vm.setConnection(conn);
                    vm.insertVid(file,type,filename, username);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ConnectionUtil.close(null, null, conn);
            }


            is.close();
        }
        RequestDispatcher rd = request.getRequestDispatcher("/FileUpload.jsp");
        rd.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
