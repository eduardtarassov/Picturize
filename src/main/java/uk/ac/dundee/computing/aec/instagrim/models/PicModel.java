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
import java.util.Date;
import javax.imageio.ImageIO;

import static org.imgscalr.Scalr.*;

import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;

public class PicModel {

    //Cluster cluster;
    Connection conn;

    public void PicModel() {
        Connection conn;
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

            String picid = convertor.getTimeUUID().toString();


            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));

            output.write(imageB);
            System.out.println("This is your picid: " + picid);

            System.out.println("This is your imageB: " + imageB);


            byte[] thumbB = picresize(picid.toString(), types[1]);
            System.out.println("This is your thumbB: " + thumbB);
            byte[] processedB = picdecolour(picid.toString(), types[1]);


            Blob image = new javax.sql.rowset.serial.SerialBlob(imageB);

            Blob thumb = new javax.sql.rowset.serial.SerialBlob(thumbB);

            Blob processed = new javax.sql.rowset.serial.SerialBlob(processedB);


            System.out.println("This is your user: " + user);
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

            String strPsInsertPic = "INSERT INTO `pics`(picid,image,thumb,processed,user, " +
                    "interactiontime, imagelength, thumblength, processedlength, type, name) " +
                    "VALUES ('" + picid + "','" + image + "','" + thumb + "','" + processed + "','" + user + "','"
                    + interaction_time + "','" + image_length + "','" + thumb_length + "','" + processed_length +
                    "','" + type + "','" + name + "')";
            psInsertPic = conn.prepareStatement(strPsInsertPic);
            System.out.println(strPsInsertPic);
            psInsertPic.executeUpdate();

            String strPsInsertPicToUser = "INSERT INTO `userpiclist`(picid,user,interactiontime) " +
                    "VALUES ('" + picid + "','" + user + "','" + interaction_time + "')";
            psInsertPicToUser = conn.prepareStatement(strPsInsertPicToUser);
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
   /*
    public java.util.LinkedList<Pic> getPicsForUser(String User) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select picid from userpiclist where user =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        User));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for (Row row : rs) {
                Pic pic = new Pic();
                java.util.UUID UUID = row.getUUID("picid");
                System.out.println("UUID" + UUID.toString());
                pic.setUUID(UUID);
                Pics.add(pic);

            }
        }
        return Pics;
    }

    public Pic getPic(int image_type, java.util.UUID picid) {
        Session session = cluster.connect("instagrim");
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        try {
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
         
            if (image_type == Convertors.DISPLAY_IMAGE) {
                
                ps = session.prepare("select image,imagelength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_THUMB) {
                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                ps = session.prepare("select processed,processedlength,type from pics where picid =?");
            }
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));

            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");
                
                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }
                    
                    type = row.getString("type");

                }
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
            return null;
        }
        session.close();
        Pic p = new Pic();
        p.setPic(bImage, length, type);

        return p;

    }*/

}
