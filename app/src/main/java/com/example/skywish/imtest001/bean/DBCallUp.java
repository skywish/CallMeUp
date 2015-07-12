package com.example.skywish.imtest001.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by DYY-PC on 2015/7/5.
 */
public class DBCallUp extends BmobObject {
    private String userName;
    private String callUpName;
    private String callUpSex;
    private String callUpRecordPath;
    public DBCallUp(){}
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getCallUpName(){
        return callUpName;
    }
    public void setCallUpName(String callUpName){
        this.callUpName = callUpName;
    }
    public String getCallUpSex(){
        return callUpSex;
    }
    public void setCallUpSex(String callUpSex){
        this.callUpSex = callUpSex;
    }
    public String getCallUpRecordPath(){
        return callUpRecordPath;
    }
    public void setCallUpRecordPath(String callUpRecordPath){
        this.callUpRecordPath = callUpRecordPath;
    }
}
