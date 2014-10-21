package uk.ac.dundee.computing.aec.instagrim.models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.websocket.Session;

import static org.imgscalr.Scalr.*;

import com.datastax.driver.core.BoundStatement;
import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

public class PicModel {

    //Cluster cluster;
    Connection conn = null;

    public void PicModel() {

    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void insertPic(byte[] imageB, String type, String name, String user) {
        PreparedStatement psInsertPic = null;
        PreparedStatement psInsertPicToUser = null;

        try {
            Convertors convertor = new Convertors();

            String types[] = Convertors.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(imageB);

            System.out.println("This is your types[0]: " + types[0]);
            System.out.println("This is your types[1]: " + types[1]);

            //String strpicid = convertor.getTimeUUID().toString();
           /* String picid = convertor.getTimeUUID().toString();
           picid = picid.replaceAll("-", "");*/

            String picid = Convertors.getTimeUUID().toString().replaceAll("-", "");

            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));


            output.write(imageB);

            System.out.println("This is your picid: " + picid);

            // System.out.println("This is your imageB: " + imageB);


            byte[] thumbB = picresize(picid, types[1]);
            System.out.print("TEEEEEEST2: ");
            for (int r = 0; r < thumbB.length; r++) {
                System.out.print(thumbB[r]);
            }
            System.out.println();


            //System.out.println("This is your thumbB: " + thumbB);
            byte[] processedB = picdecolour(picid, types[1]);


            Blob image = new SerialBlob(imageB);

            Blob thumb = new SerialBlob(thumbB);

            byte[] bdata = thumb.getBytes(1, (int) thumb.length());
            //String text = new String(bdata);
            System.out.print("TEEEEEEST3: ");
            for (int r = 0; r < bdata.length; r++) {
                System.out.print(bdata[r]);
            }
            System.out.println();


            Blob processed = new SerialBlob(processedB);


            // System.out.println("This is your user: " + user);
            // just value for testing
            //Date interaction_time = new Date();
            String interaction_time = new Date().toString();
            System.out.println("This is your interaction time: " + interaction_time);

            int image_length = imageB.length; // size of image in bytes
            System.out.println("This is your image in bytes length: " + image_length);

            int thumb_length = thumbB.length;
            System.out.println("This is your thumbnail in bytes length: " + thumb_length);

            int processed_length = processedB.length;
            System.out.println("This is your processed in bytes length: " + processed_length);

            System.out.println("This is your type: " + type);
            System.out.println("This is your name: " + name);


            psInsertPic = conn.prepareStatement("INSERT INTO pics" +
                            "(picid,image,thumb,processed,user,interactiontime,imagelength,thumblength,processedlength,type,name) VALUES" +
                    "(?,?,?,?,?,?,?,?,?,?,?)");
            psInsertPic.setString(1, picid);
            psInsertPic.setBlob(2,image);
            psInsertPic.setBlob(3,thumb);
            psInsertPic.setBlob(4,processed);
            psInsertPic.setString(5, user);
            psInsertPic.setString(6, interaction_time);
            psInsertPic.setInt(7, image_length);
            psInsertPic.setInt(8, thumb_length);
            psInsertPic.setInt(9, processed_length);
            psInsertPic.setString(10, type);
            psInsertPic.setString(11, name);
            psInsertPic.executeUpdate();



            psInsertPicToUser = conn.prepareStatement("INSERT INTO userpiclist" +
                    "(picid, user, interactiontime) VALUES" +
                    "(?,?,?)");
            psInsertPicToUser.setString(1, picid);
            psInsertPicToUser.setString(2, user);
            psInsertPicToUser.setString(3, interaction_time);
            psInsertPicToUser.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR: MySQL operation problem!");
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, psInsertPic, null);
            ConnectionUtil.close(null, psInsertPicToUser, null);
        }
    }

    public byte[] picresize(String picid, String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage thumbnail = createThumbnail(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }

    public byte[] picdecolour(String picid, String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage processed = createProcessed(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }

    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }

    public static BufferedImage createProcessed(BufferedImage img) {
        int Width = img.getWidth() - 1;
        img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(img, 4);
    }

    public LinkedList<Pic> getPicsForUser(String username) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String strPs = "SELECT * FROM userpiclist WHERE user='" + username + "'";
            ps = conn.prepareStatement(strPs);


            System.out.println("This is your prepared Statement: " + ps.toString());
            // BoundStatement boundStatement = new BoundStatement(ps);
            rs = ps.executeQuery(); // this is where the query is executed

            if (rs.first()) {
                do {
                    Pic pic = new Pic();
                    //String UUIDstr = rs.getString("picid");
                    String picid = rs.getString("picid");
                    //UUID artID = UUID.nameUUIDFromBytes(picid);
                    System.out.println("This is your picid from userpiclist table: " + picid);
                    //UUID UUID = UUIDb.nameUUIDFromBytes( UUIDb );
                    //System.out.println("This is your: UUIDb: " + UUID);
                    //java.util.UUID UUID = rs.getString("picid");

                    pic.setID(picid);
                    Pics.add(pic);
                } while (rs.next());

            } else {
                System.out.println("No images returned");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(rs, ps, null);
        }

        return Pics;
    }

    public Pic getPic(int image_type, String picid) {
        Blob bImage = null;
        String type = null;
        int length = 0;
        try {
            ResultSet rs = null;
            PreparedStatement ps = null;


            if (image_type == Convertors.DISPLAY_IMAGE) {
                ps = conn.prepareStatement("SELECT image,imagelength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT image,imagelength,type FROM pics WHERE picid ='" + picid + "'");

            } else if (image_type == Convertors.DISPLAY_THUMB) {
                ps = conn.prepareStatement("SELECT thumb,imagelength,thumblength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT thumb,imagelength,thumblength,type FROM pics WHERE picid ='" + picid + "'");
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                ps = conn.prepareStatement("SELECT processed,processedlength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT processed,processedlength,type WHERE pics WHERE picid = (?)");
            }
            ps.setString(1, picid);
            rs = ps.executeQuery();


            if (rs.first()) {
                System.out.println("LALALALA");
                do {
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        bImage = rs.getBlob("image");
                        length = rs.getInt("imagelength");
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        bImage = rs.getBlob("thumb");
                        length = rs.getInt("thumblength");

                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        bImage = rs.getBlob("processed");
                        length = rs.getInt("processedlength");
                    }

                    type = rs.getString("type");

                    bImage = new SerialBlob(bImage);
                    byte[] bdata = bImage.getBytes(1, (int) bImage.length());
                    //String text = new String(bdata);
                    System.out.print("TEEEEEEST4: ");
                    for (int r = 0; r < bdata.length; r++) {
                        System.out.print(bdata[r]);
                    }
                    System.out.println();
                } while (rs.next());

            } else {
                System.out.println("No images returned");
                return null;
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
            return null;
        }
        System.out.println("This is your retrieved picid: " + picid);
        System.out.print("This is your bimage: ");
        /*for (int i = 0; i < bImage.length; i++){
            System.out.print(bImage[i]);
        }*/

        System.out.println(bImage.toString());
        System.out.println("This is your retrieved length: " + length);
        System.out.println("This is your retrieved type: " + type);


        Pic p = new Pic();
        p.setPic(picid, bImage, length, type);

        return p;

    }

}
