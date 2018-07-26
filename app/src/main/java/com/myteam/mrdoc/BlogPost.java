package com.myteam.mrdoc;


import java.util.Date;

public class BlogPost extends BlogPostId
{
    public String image_url;
    public String image_thumb;
    public String desc;
    public Date timestamp;
    public String user_id;
    public BlogPost(){

    }

    public BlogPost(String image_url, String image_thumb, String desc, String user_id,Date timestamp) {
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.desc = desc;
        this.user_id = user_id;
        this.timestamp = timestamp;

    }



    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }




}
