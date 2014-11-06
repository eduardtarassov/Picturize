package uk.ac.dundee.computing.aec.instagrim.containers;


/**
 * Created by Eduard on 06/11/2014.
 */
public class Comm {

    private String picid;
    private String username;
    private String comment;

    public void Pic() {

    }


    public String getID() {
        return this.picid;
    }

    public void setComment(String picid, String username, String comment) {
        this.picid = picid;
        this.username = username;
        this.comment = comment;
    }

    public String getUser() {
        return this.username;
    }

    public String getComment() {
        return comment;
    }

    public String getPicid() {
        return picid;
    }

}



