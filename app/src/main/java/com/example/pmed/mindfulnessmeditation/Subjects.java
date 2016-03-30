package com.example.pmed.mindfulnessmeditation;

import android.database.Cursor;

/**
 * Created by Jacky Sitzman on 3/7/2016.
 */
public class Subjects {

    Integer id;
    String uname, pass;
    //Boolean isAdmin;
    //String globalPass;

    public void setId(Integer id) { this.id = id; }
    public Integer getId() { return this.id; }
    public void setUname(String uname) { this.uname = uname; }
    public String getUname() { return this.uname; }
    public void setPass(String pass) { this.pass = pass; }
    public String getPass() { return this.pass; }
    //public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }
    //public Boolean getIsAdmin() { return this.isAdmin; }
    //public void setGlobalPass(String globalPass) { this.globalPass = globalPass; }
    //public String getGlobalPass() { return this.globalPass; }
}
