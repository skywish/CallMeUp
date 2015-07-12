package com.example.skywish.imtest001.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by DYY-PC on 2015/7/4.
 */
public class DBRecord extends BmobObject{
    private String userName;
    private String fileUrl;
    private String date;
    private Long times;
    private String sex;
    public DBRecord() {
    }

    public DBRecord(String userName, String sex, String fileUrl, String date, Long times) {
        this.userName = userName;
        this.sex = sex;
        this.fileUrl = fileUrl;
        this.date = date;
        this.times = times;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFile(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTimes(){return times;}

    public void setTimes(Long times){this.times =times;}
    public String getSex(){
        return sex;
    }
    public void setSex(String sex){
        this.sex = sex;
    }
}