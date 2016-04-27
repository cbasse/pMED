package com.example.pmed.mindfulnessmeditation;

import android.database.Cursor;

/**
 * Created by Jacky Sitzman on 3/7/2016.
 */
public class Subjects {

    Integer id;
    String uname, experimentName, progress;
    //Boolean isAdmin;
    //String globalPass;

    public void setId(Integer id) { this.id = id; }
    public Integer getId() { return this.id; }
    public void setUname(String uname) { this.uname = uname; }
    public String getUname() { return this.uname; }
    public void setExperimentName(String expId) { this.experimentName = expId; }
    public String getExperimentName() { return this.experimentName; }
    public void setProgress(String prog) { this.progress = prog; }
    public String getProgress() { return this.progress; }
    //public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }
    //public Boolean getIsAdmin() { return this.isAdmin; }
    //public void setGlobalPass(String globalPass) { this.globalPass = globalPass; }
    //public String getGlobalPass() { return this.globalPass; }
}
