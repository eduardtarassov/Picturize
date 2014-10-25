package uk.ac.dundee.computing.aec.instagrim.models;

import uk.ac.dundee.computing.aec.instagrim.containers.ProfileInfo;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

/**
 * Created by Eduard on 25/10/2014.
 */
public class SearchModel {

    Connection conn;

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public LinkedList<ProfileInfo> getUsersForSearch(String username) {
        LinkedList<ProfileInfo> profiles = new LinkedList<ProfileInfo>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ProfileInfo proInfo = null;
            ps = conn.prepareStatement("SELECT * FROM users WHERE username=?");
            ps.setString(1, username);

            System.out.println("This is your prepared Statement: " + ps.toString());
            // BoundStatement boundStatement = new BoundStatement(ps);
            rs = ps.executeQuery(); // this is where the query is executed

            if (rs.first()) {
                do {
                    proInfo = new ProfileInfo();
                    proInfo.setUsername(rs.getString("username"));
                    proInfo.setLastname(rs.getString("first_name"));
                    proInfo.setFirstname(rs.getString("last_name"));
                    proInfo.setEmailaddress(rs.getString("email_address"));

                    System.out.println("User found");
                    profiles.add(proInfo);

                } while (rs.next());

            } else {
                System.out.println("No users found by this name");
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(rs, ps, null);
        }

        return profiles;
    }
}
