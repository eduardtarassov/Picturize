/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 *
 * @author Administrator
 */
public class LoginState {
    private static boolean loginState = false;
    private static String username = null;
    
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }

    
    public void setLoginState(boolean logedin){
        this.loginState = logedin;
    }
    public boolean getLoginState(){
        return loginState;
    }
}
