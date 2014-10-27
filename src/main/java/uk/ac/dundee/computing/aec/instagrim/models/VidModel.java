package uk.ac.dundee.computing.aec.instagrim.models;

import org.imgscalr.Scalr;
import uk.ac.dundee.computing.aec.instagrim.containers.Pic;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
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

import static org.imgscalr.Scalr.*;
import static org.imgscalr.Scalr.pad;

/**
 * Created by Eduard on 26/10/2014.
 */
public class VidModel {

    Connection conn = null;

    public void VidModel() {

    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void insertVid(byte[] videoB, String type, String name, String user) {
        PreparedStatement psInsertVid = null;
        PreparedStatement psInsertVidToUser = null;

        try {
            Convertors convertor = new Convertors();

            String types[] = Convertors.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(videoB);

            System.out.println("This is your types[0]: " + types[0]);
            System.out.println("This is your types[1]: " + types[1]);

            String vidid = Convertors.getTimeUUID().toString().replaceAll("-", "");

            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + vidid));


            output.write(videoB);

            System.out.println("This is your vidid: " + vidid);

            // System.out.println("This is your imageB: " + imageB);


            /*byte[] thumbB = picresize(picid, types[1]);
            System.out.print("TEEEEEEST2: ");
            for (int r = 0; r < thumbB.length; r++) {
                System.out.print(thumbB[r]);
            }
            System.out.println();*/


            /*byte[] processedB = picdecolour(picid, types[1]);
*/

            Blob video = new SerialBlob(videoB);

           /* Blob thumb = new SerialBlob(thumbB);*/

            /*byte[] bdata = thumb.getBytes(1, (int) thumb.length());*/
            //String text = new String(bdata);
           /* System.out.print("TEEEEEEST3: ");
            for (int r = 0; r < bdata.length; r++) {
                System.out.print(bdata[r]);
            }
            System.out.println();


            Blob processed = new SerialBlob(processedB);*/


            // System.out.println("This is your user: " + user);
            // just value for testing
            //Date interaction_time = new Date();
            String interaction_time = new Date().toString();
            System.out.println("This is your interaction time: " + interaction_time);

            int video_length = videoB.length; // size of image in bytes
            System.out.println("This is your image in bytes length: " + video_length);

           /* int thumb_length = thumbB.length;
            System.out.println("This is your thumbnail in bytes length: " + thumb_length);

            int processed_length = processedB.length;
            System.out.println("This is your processed in bytes length: " + processed_length);
*/
            System.out.println("This is your type: " + type);
            System.out.println("This is your name: " + name);


            psInsertVid = conn.prepareStatement("INSERT INTO vids" +
                    "(vidid,video,user,interactiontime,videolength,type,name) VALUES" +
                    "(?,?,?,?,?,?,?)");
            psInsertVid.setString(1, vidid);
            psInsertVid.setBlob(2, video);
           /* psInsertVid.setBlob(3, thumb);
            psInsertVid.setBlob(4, processed);*/
            psInsertVid.setString(3, user);
            psInsertVid.setString(4, interaction_time);
            psInsertVid.setInt(5, video_length);
            // psInsertPic.setInt(8, thumb_length);
            //psInsertPic.setInt(9, processed_length);
            psInsertVid.setString(6, type);
            psInsertVid.setString(7, name);
            psInsertVid.executeUpdate();

            psInsertVidToUser = conn.prepareStatement("INSERT INTO uservidlist" +
                    "(vidid, user, interactiontime) VALUES" +
                    "(?,?,?)");

            psInsertVidToUser.setString(1, vidid);
            psInsertVidToUser.setString(2, user);
            psInsertVidToUser.setString(3, interaction_time);
            psInsertVidToUser.executeUpdate();
        } catch (Exception e) {
            System.out.println("ERROR: MySQL operation problem!");
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, psInsertVid, null);
            ConnectionUtil.close(null, psInsertVidToUser, null);
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
        img = resize(img, Scalr.Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
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
            ps = conn.prepareStatement("SELECT * FROM userpiclist WHERE user='" + username + "'");


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
                ps = conn.prepareStatement("SELECT thumb,thumblength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT thumb,imagelength,thumblength,type FROM pics WHERE picid ='" + picid + "'");
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                ps = conn.prepareStatement("SELECT processed,processedlength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT processed,processedlength,type WHERE pics WHERE picid = (?)");
            }
            ps.setString(1, picid);
            rs = ps.executeQuery();


            if (rs.first()) {
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
                    /*for (int r = 0; r < bdata.length; r++) {
                        System.out.print(bdata[r]);
                    }
                    System.out.println();*/
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

