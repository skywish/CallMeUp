package com.example.skywish.imtest001.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by DYY-PC on 2015/7/2.
 */
public class DBClock extends BmobObject{
    private Integer hour;
    private Integer minute;
    private String musicPath;
    private int repeatTime;
    private String userName;
    private Long clockID;
    private Boolean isGirl;
    private Boolean isSystem;
    private Boolean isOpen;

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public DBClock(){}
    public Integer getHour(){
        return hour;
    }
    public void setHour(Integer hour){
        this.hour = hour;
    }
    public Integer getMinute(){
        return minute;
    }
    public void setMinute(Integer minute){
        this.minute = minute;
    }
    public String getMusicPath(){
        return musicPath;
    }
    public void setMusicPath(String musicPath){
        this.musicPath = musicPath;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public int getRepeatTime(){
        return repeatTime;
    }
    public void setRepeatTime(int repeatTime){
        this.repeatTime = repeatTime;
    }
    public Long getClockID(){
        return clockID;
    }
    public void setClockID(Long clockID){
        this.clockID = clockID;
    }
    public Boolean getIsGirl(){
        return isGirl;
    }
    public void setIsGirl(Boolean isGirl){
        this.isGirl = isGirl;
    }
    public Boolean getIsSystem(){
        return isSystem;
    }
    public void setIsSystem(Boolean isSystem){
        this.isSystem = isSystem;
    }

}