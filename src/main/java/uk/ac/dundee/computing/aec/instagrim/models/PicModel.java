package uk.ac.dundee.computing.aec.instagrim.models;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;

import static org.imgscalr.Scalr.*;

import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.aec.instagrim.containers.Comm;
import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.models.utils.ConnectionUtil;
import uk.ac.dundee.computing.aec.instagrim.containers.Pic;

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

            String picid = Convertors.getTimeUUID().toString().replaceAll("-", "");

            //The following is a quick and dirty way of doing this, will fill the disk quickly !
          // FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));


           // output.write(imageB);

            System.out.println("This is your picid: " + picid);

            // System.out.println("This is your imageB: " + imageB);
            ImageIO.setUseCache(false);

            byte[] thumbB = picresize(imageB, types[1]);


            byte[] processedB = picdecolour(imageB, types[1]);

            byte[] sepiaB = picToSepia(imageB, types[1], 150);


            Blob image = new SerialBlob(imageB);


            Blob thumb = new SerialBlob(thumbB);

            byte[] bdata = thumb.getBytes(1, (int) thumb.length());
            //String text = new String(bdata);



            Blob processed = new SerialBlob(processedB);
            Blob sepia = new SerialBlob(sepiaB);


            String interaction_time = new Date().toString();
            System.out.println("This is your interaction time: " + interaction_time);

            int image_length = imageB.length; // size of image in bytes
            System.out.println("This is your image in bytes length: " + image_length);

            int thumb_length = thumbB.length;
            System.out.println("This is your thumbnail in bytes length: " + thumb_length);

            int processed_length = processedB.length;
            System.out.println("This is your processed in bytes length: " + processed_length);

            int sepia_length = sepiaB.length;
            System.out.println("This is your sepia in bytes length: " + sepia_length);

            System.out.println("This is your type: " + type);
            System.out.println("This is your name: " + name);


            psInsertPic = conn.prepareStatement("INSERT INTO pics" +
                    "(picid,image,thumb,processed,user,interactiontime,imagelength,thumblength,processedlength,type,name,sepia,sepialength) VALUES" +
                    "(?,?,?,?,?,?,?,?,?,?,?,?,?)");
            psInsertPic.setString(1, picid);
            psInsertPic.setBlob(2, image);
            psInsertPic.setBlob(3, thumb);
            psInsertPic.setBlob(4, processed);
            psInsertPic.setString(5, user);
            psInsertPic.setString(6, interaction_time);
            psInsertPic.setInt(7, image_length);
            psInsertPic.setInt(8, thumb_length);
            psInsertPic.setInt(9, processed_length);
            psInsertPic.setString(10, type);
            psInsertPic.setString(11, name);
            psInsertPic.setBlob(12, sepia);
            psInsertPic.setInt(13, sepia_length);
            psInsertPic.executeUpdate();


            psInsertPicToUser = conn.prepareStatement("INSERT INTO userpiclist" +
                    "(picid, user, interactiontime, likes) VALUES" +
                    "(?,?,?,?)");
            psInsertPicToUser.setString(1, picid);
            psInsertPicToUser.setString(2, user);
            psInsertPicToUser.setString(3, interaction_time);
            psInsertPicToUser.setInt(4, 0);
            psInsertPicToUser.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR: MySQL operation problem!");
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, psInsertPic, null);
            ConnectionUtil.close(null, psInsertPicToUser, null);
        }
    }

    public byte[] picresize(byte[] imageB, String type) {
        try {
            BufferedImage BI = createImageFromBytes(imageB);
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

    public byte[] picdecolour(byte[] imageB, String type) {
        try {
            BufferedImage BI = createImageFromBytes(imageB);
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

    public byte[] picToSepia(byte[] imageB, String type, int sepiaIntensity) {
        try {
            BufferedImage BI = createImageFromBytes(imageB);
            BufferedImage sepia = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_INT_RGB);
            // Play around with this.  20 works well and was recommended
            //   by another developer. 0 produces black/white image
            int sepiaDepth = 20;

            int w = BI.getWidth();
            int h = BI.getHeight();

            WritableRaster raster = sepia.getRaster();

            // We need 3 integers (for R,G,B color values) per pixel.
            int[] pixels = new int[w * h * 3];
            BI.getRaster().getPixels(0, 0, w, h, pixels);

            //  Process 3 ints at a time for each pixel.  Each pixel has 3 RGB
            //    colors in array
            for (int i = 0; i < pixels.length; i += 3) {
                int r = pixels[i];
                int g = pixels[i + 1];
                int b = pixels[i + 2];

                int gry = (r + g + b) / 3;
                r = g = b = gry;
                r = r + (sepiaDepth * 2);
                g = g + sepiaDepth;

                if (r > 255) {
                    r = 255;
                }
                if (g > 255) {
                    g = 255;
                }
                if (b > 255) {
                    b = 255;
                }

                // Darken blue color to increase sepia effect
                b -= sepiaIntensity;

                // normalize if out of bounds
                if (b < 0) {
                    b = 0;
                }
                if (b > 255) {
                    b = 255;
                }

                pixels[i] = r;
                pixels[i + 1] = g;
                pixels[i + 2] = b;
            }
            raster.setPixels(0, 0, w, h, pixels);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(sepia, type, baos);
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

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            ps = conn.prepareStatement("SELECT picid,likes FROM userpiclist WHERE user=?");

            ps.setString(1, username);
            System.out.println("This is your prepared Statement: " + ps.toString());
            // BoundStatement boundStatement = new BoundStatement(ps);
            rs = ps.executeQuery(); // this is where the query is executed

            if (rs.first()) {
                do {
                    Pic pic = new Pic();
                    //String UUIDstr = rs.getString("picid");
                    String picid = rs.getString("picid");
                    int likes = rs.getInt("likes");                    //UUID artID = UUID.nameUUIDFromBytes(picid);
                    System.out.println("This is your picid from userpiclist table: " + picid);
                    //UUID UUID = UUIDb.nameUUIDFromBytes( UUIDb );
                    //System.out.println("This is your: UUIDb: " + UUID);
                    //java.util.UUID UUID = rs.getString("picid");
                    pic.setLikes(likes);
                    pic.setID(picid);
                    pic.setUser(username);
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

    public LinkedList<Comm> getComments(String picid) {
        java.util.LinkedList<Comm> Comms = new java.util.LinkedList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT user,text FROM comments WHERE picid=?");

            ps.setString(1, picid);
            System.out.println("This is your prepared Statement: " + ps.toString());
            // BoundStatement boundStatement = new BoundStatement(ps);
            rs = ps.executeQuery(); // this is where the query is executed

            if (rs.first()) {
                do {
                    Comm comm = new Comm();
                    //String UUIDstr = rs.getString("picid");
                    String username = rs.getString("user");
                    String text = rs.getString("text");                    //UUID artID = UUID.nameUUIDFromBytes(picid);
                    //UUID UUID = UUIDb.nameUUIDFromBytes( UUIDb );
                    //System.out.println("This is your: UUIDb: " + UUID);
                    //java.util.UUID UUID = rs.getString("picid");
                    comm.setComment(picid, username, text);


                    Comms.add(comm);
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

        return Comms;
    }


    /*public LinkedList<Comm> getComments(String picid) {
        java.util.LinkedList<Comm> Comms = new java.util.LinkedList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT picid,likes FROM userpiclist WHERE user=?");

            ps.setString(1, username);
            System.out.println("This is your prepared Statement: " + ps.toString());
            // BoundStatement boundStatement = new BoundStatement(ps);
            rs = ps.executeQuery(); // this is where the query is executed

            if (rs.first()) {
                do {
                    Pic pic = new Pic();
                    //String UUIDstr = rs.getString("picid");
                    String picid = rs.getString("picid");
                    int likes = rs.getInt("likes");                    //UUID artID = UUID.nameUUIDFromBytes(picid);
                    System.out.println("This is your picid from userpiclist table: " + picid);
                    //UUID UUID = UUIDb.nameUUIDFromBytes( UUIDb );
                    //System.out.println("This is your: UUIDb: " + UUID);
                    //java.util.UUID UUID = rs.getString("picid");
                    pic.setLikes(likes);
                    pic.setID(picid);
                    pic.setUser(username);
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
    }*/

    public Pic getPic(int image_type, String picid) {
        Blob bImage = null;
        String type = null;
        int length = 0;
        try {
            ResultSet rs = null;
            PreparedStatement ps = null;


            if (image_type == Convertors.DISPLAY_IMAGE) {
                ps = conn.prepareStatement("SELECT image,imagelength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT image,imagelength,type,likes FROM pics WHERE picid ='" + picid + "'");

            } else if (image_type == Convertors.DISPLAY_THUMB) {
                ps = conn.prepareStatement("SELECT thumb,thumblength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT thumb,imagelength,thumblength,type,likes FROM pics WHERE picid ='" + picid + "'");
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                ps = conn.prepareStatement("SELECT processed,processedlength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT processed,processedlength,type WHERE pics WHERE picid = (?)");
            } else if (image_type == Convertors.DISPLAY_SEPIA) {
                ps = conn.prepareStatement("SELECT sepia,sepialength,type FROM pics WHERE picid = (?)");
                System.out.println("This is your statement: " + "SELECT sepia,sepialength,type WHERE pics WHERE picid = (?)");
            }
            ps.setString(1, picid);
            rs = ps.executeQuery();

            System.out.println("Check");
            if (rs.first()) {
                do {
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        bImage = rs.getBlob("image");
                        length = rs.getInt("imagelength");
                        System.out.println("DISPLAY IMAGE");
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        bImage = rs.getBlob("thumb");
                        length = rs.getInt("thumblength");
                        System.out.println("DISPLAY THUMB");

                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        bImage = rs.getBlob("processed");
                        length = rs.getInt("processedlength");
                        System.out.println("DISPLAY PROCESSED");
                    }
                    else if (image_type == Convertors.DISPLAY_SEPIA) {
                        bImage = rs.getBlob("sepia");
                        length = rs.getInt("sepialength");
                        System.out.println("DISPLAY SEPIA");
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

    public void likePic(String picid) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("UPDATE userpiclist SET likes=likes+1 WHERE picid = ?");

            ps.setString(1, picid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, ps, null);
        }
    }

    public void picComment(String comment, String picid, String user) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("INSERT INTO comments" +
                    "(picid,user,text) VALUES" +
                    "(?,?,?)");

            ps.setString(1, picid);
            ps.setString(2, user);
            ps.setString(3, comment);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, ps, null);
        }
    }

}
