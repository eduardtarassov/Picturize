package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 * Created by Eduard on 21/10/2014.
 */
public class ProfileInformation {
    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



    /**
     *
     * @author Administrator
     */
        private static boolean logedin = false;
        private static String username = null;
        private static String firstname = null;
        private static String lastname = null;
        private static String email = null;
        private static String question = null;
        private static String answer = null;



        public void ProfileInformation(){

        }

        public void setUsername(String username){
            this.username = username;
        }
        public String getUsername(){
            return username;
        }

        public void setFirstname(String firstname){
            this.firstname = firstname;
        }
        public String getFirstname(){
            return firstname;
        }

        public void setLastname(String lastname){
            this.lastname = lastname;
        }
        public String getLastname(){
            return lastname;
        }

        public void setEmailaddress(String email){
            this.email = email;
        }
        public String getEmail(){
            return email;
        }

        public void setQuestion(String question){
            this.question = question;
        }
        public String getQuestion(){
            return question;
        }

        public void setAnswer(String answer){
            this.answer = answer;
        }
        public String getAnswer(){
            return answer;
        }

        public void setLoginState(boolean logedin){
            this.logedin=logedin;
        }
        public boolean getlogedin(){
            return logedin;
        }
    }

