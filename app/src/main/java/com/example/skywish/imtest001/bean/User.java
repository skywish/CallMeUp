package com.example.skywish.imtest001.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by skywish on 2015/6/27.
 */
public class User extends BmobChatUser{

    private static final long serialVersionUID = 1L;
    // 博客列表
    private BmobRelation blogs;
    // 拼音首字母
    private String sortLetters;
    // 性别 true男
    private Boolean sex;
    // 个性签名
    private String words;
    // 生日
    private String birthday;
    // 地区
    private String city;

    // 地理坐标
    private BmobGeoPoint location;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BmobRelation getBlogs() {
        return blogs;
    }
    public void setBlogs(BmobRelation blogs) {
        this.blogs = blogs;
    }
    public BmobGeoPoint getLocation() {
        return location;
    }
    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }
    public Boolean getSex() {
        return sex;
    }
    public void setSex(Boolean sex) {
        this.sex = sex;
    }
    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

}
