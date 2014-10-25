package uk.ac.dundee.computing.aec.instagrim.servlets;import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;import uk.ac.dundee.computing.aec.instagrim.models.PicModel;import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;import uk.ac.dundee.computing.aec.instagrim.containers.LoginState;import uk.ac.dundee.computing.aec.instagrim.containers.Pic;import java.io.*;import java.sql.Blob;import java.sql.Connection;import java.util.HashMap;import java.util.LinkedList;import javax.servlet.RequestDispatcher;import javax.servlet.ServletConfig;import javax.servlet.ServletException;import javax.servlet.annotation.MultipartConfig;import javax.servlet.annotation.WebServlet;import javax.servlet.http.HttpServlet;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import javax.servlet.http.HttpSession;import javax.servlet.http.Part;import javax.sql.DataSource;/** * Servlet implementation class Image */@WebServlet(urlPatterns = {        "/Image",        "/Image/*",        "/Thumb/*",        "/Images",        "/Images/*"})@MultipartConfigpublic class Image extends HttpServlet {    private static final long serialVersionUID = 1L;    private HashMap<String, Integer> CommandsMap = new HashMap<String, Integer>();    private DataSource dataSource = null;    private Connection conn;    public void init(ServletConfig config) throws ServletException {        // Get DataSource        dataSource = ConnectionUtil.getMySQLDataSource();    }    /**     * @see HttpServlet#HttpServlet()     */    public Image() {        super();        // TODO Auto-generated constructor stub        CommandsMap.put("Image", 1);        CommandsMap.put("Images", 2);        CommandsMap.put("Thumb", 3);    }    /**     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse     * response)     */    @Override    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        // TODO Auto-generated method stub        // Splitting and decoding the request path into arguments.        String args[] = Convertors.SplitRequestPath(request);        int display_choice = 99;        for (int i = args.length - 1; i >= 0; i--) {            if ((args[i].equals("Image")) || (args[i].equals("Images")) || (args[i].equals("Thumb"))) {                System.out.println("Your function is: " + args[i]);                display_choice = i;                break;            } else {                System.out.println("Not your choice at: " + i);            }        }       /* for (int i = 0; i < args.length; i++)            System.out.println("Argument number: " + i + " is: " + args[i]);*/        switch (args[display_choice]) {            case "Image":                DisplayImage(Convertors.DISPLAY_PROCESSED, args[display_choice + 1], response);                break;            case "Images":                DisplayImageList(args[display_choice + 1], request, response);                System.out.println("Finished showing image list");                break;            case "Thumb":                DisplayImage(Convertors.DISPLAY_THUMB, args[display_choice + 1], response);                break;            default:                error("Bad Operator", response);        }    }    private void DisplayImageList(String user, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        System.out.println("Display Image list");        PicModel tm = new PicModel();        RequestDispatcher rd = null;        try {            conn = dataSource.getConnection();            tm.setConnection(this.conn);            LinkedList<Pic> lsPics = tm.getPicsForUser(user);            System.out.println("This is your pictures array size: " + lsPics.size());            rd = request.getRequestDispatcher("/UsersPics.jsp");            request.setAttribute("user", user);            request.setAttribute("Pics", lsPics);            System.out.println("This is your test path info: " + request.getPathInfo());            //String args[] = Convertors.SplitRequestPath(request);            rd.forward(request, response);        } catch (Exception e) {            e.printStackTrace();        } finally {            ConnectionUtil.close(null, null, conn);        }    }    private void DisplayImage(int type, String picid, HttpServletResponse response) throws ServletException, IOException {System.out.println("Displaying image of type: " + type);        PicModel tm = new PicModel();        try {            conn = dataSource.getConnection();            tm.setConnection(this.conn);            System.out.println("This is your imageID: " + picid);            System.out.println("This is your type: " + type);            Pic p = tm.getPic(type, picid);            OutputStream out = response.getOutputStream();            response.setContentType(p.getType());            response.setContentLength(p.getLength());            Blob blob = p.getBlob();            byte[] bdata = blob.getBytes(1, (int) blob.length());            out.write(bdata, 0, bdata.length);            out.flush();        } catch (Exception e) {            e.printStackTrace();        } finally {            ConnectionUtil.close(null, null, conn);        }        //out.write(Image);    }    @Override    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        for (Part part : request.getParts()) {            System.out.println("Part Name " + part.getName());            String type = part.getContentType();            String filename = part.getSubmittedFileName();            System.out.println("This is the file type: " + type);            System.out.println("This is the filename: " + filename);            InputStream is = request.getPart(part.getName()).getInputStream();            //int i = is.available();            HttpSession session = request.getSession();            LoginState lg = (LoginState) session.getAttribute("LoggedIn");            String username = "null";            if (lg.getLoginState()) {                username = lg.getUsername();                System.out.println("Current user: " + username);            }            /*if (i > 0) {                byte[] b = new byte[i + 1];                is.read(b);                System.out.print("TEEEEEEST1: ");                for (int r = 0; r < b.length; r++) {                    System.out.print(b[r]);                }                System.out.println();                System.out.println("Length : " + b.length);*/            ByteArrayOutputStream baos = new ByteArrayOutputStream();            byte[] buff = new byte[1024];            for(int readNum; (readNum = is.read(buff)) !=-1 ; ){                baos.write(buff,0,readNum);            }            byte[] image = baos.toByteArray();            PicModel tm = new PicModel();            try {                conn = dataSource.getConnection();                tm.setConnection(conn);                tm.insertPic(image, type, filename, username);            } catch (Exception e) {                e.printStackTrace();            } finally {                ConnectionUtil.close(null, null, conn);            }            is.close();        }        RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");        rd.forward(request, response);    }    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {        PrintWriter out = null;        out = new PrintWriter(response.getOutputStream());        out.println("<h1>You have a na error in your input</h1>");        out.println("<h2>" + mess + "</h2>");        out.close();        return;    }}