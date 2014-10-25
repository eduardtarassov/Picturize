package uk.ac.dundee.computing.aec.instagrim.models.utils;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by Eduard on 03/10/2014.
 */
public class ConnectionUtil {
    private static final String DBNAME = "picturizedb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    private static final String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/picturizedb";

    public static DataSource getMySQLDataSource() {
        MysqlDataSource mysqlDS = null;
            mysqlDS = new MysqlDataSource();
            mysqlDS.setURL(MYSQL_DB_URL);
            mysqlDS.setUser(DB_USERNAME);
            mysqlDS.setPassword(DB_PASSWORD);
        return mysqlDS;
    }

    /*public static Connection getConnection() throws Exception {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/"+DBNAME+"?user="+DB_USERNAME+"&password="+DB_PASSWORD + "&useUnicode=true&characterEncoding=UTF-8";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url);
        } catch (SQLException sqle) {
            System.out.println("SQLException: Unable to open connection to db: "+sqle.getMessage());
            throw sqle;
        } catch(Exception e) {
            System.out.println("Exception: Unable to open connection to db: "+e.getMessage());

            throw e;
        }
        return conn;
    }*/

    public static void close(ResultSet rs, PreparedStatement ps, Connection conn)
    {
        if (rs!=null)
        {
            try
            {
                rs.close();

            }
            catch(SQLException e)
            {
                System.out.println("The result set cannot be closed.");
            }
        }
        if (ps != null)
        {
            try
            {
                ps.close();
            } catch (SQLException e)
            {
                System.out.println("The statement cannot be closed.");
            }
        }
        if (conn != null)
        {
            try
            {
                conn.close();
            } catch (SQLException e)
            {
                System.out.println("The data source connection cannot be closed.");
            }
        }

    }

    /*
    * The executeQuery can take an insert / update query and execute it. It would internally call getConnection() and closeConnection() to fetch and close a connection.
     */
   /* public static void executeQuery(String strQuery, Connection conn) throws Exception {
        //Connection conn = null;

        try {

            Statement stmt  = conn.createStatement();
            stmt.executeUpdate(strQuery);

        } catch (SQLException sqle) {
            System.out.println("SQLException: Unable to execute query : "+strQuery);
            throw sqle;
        } catch (Exception e) {
            System.out.println("Exception: Unable to execute query: "+strQuery);
            e.printStackTrace();
            throw e;
        } finally {
            conn.close();
        }
    }*/



}
