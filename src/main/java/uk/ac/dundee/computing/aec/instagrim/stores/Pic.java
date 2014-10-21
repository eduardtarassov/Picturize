/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;


import java.sql.Blob;

/**
 *
 * @author Administrator
 */
public class Pic {

    private Blob bImage = null;
    private int length;
    private String type;
    //private java.util.UUID UUID = null;
    private String id = null;
    
    public void Pic() {

    }
    /*public void setUUID(java.util.UUID UUID){
        this.UUID =UUID;
    }
    public String getSUUID(){
        return UUID.toString();
    }*/

    public void setID(String id){
this.id = id;
    }
    public String getID(){
        return this.id;
    }

    public void setPic(String id, Blob bImage, int length, String type) {
        this.id = id;
        this.bImage = bImage;
        this.length = length;
        this.type=type;
    }

    public Blob getBlob() {
        return bImage;
    }

    public int getLength() {
        return length;
    }
    
    public String getType(){
        return type;
    }

}
