package com.myteam.mrdoc;

public class Users {
    public String name;
    public String image;



    public String userid;
    public Users(){

    }

    public Users(String name, String image,String userid) {
        this.name = name;
        this.image = image;
        this.userid = userid;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
