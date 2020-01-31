package com.example.item;

import java.io.Serializable;

 
public class CommentList implements Serializable {

    private String cmt_id,cmt_name,cmt_text;

//    public CommentList(String cmt_id, String cmt_name, String cmt_text) {
//        this.cmt_id = cmt_id;
//        this.cmt_name = cmt_name;
//        this.cmt_text = cmt_text;
//    }

    public String getCmt_id() {
        return cmt_id;
    }

    public void setCmt_id(String cmt_id) {
        this.cmt_id = cmt_id;
    }

    public String getCmt_name() {
        return cmt_name;
    }

    public void setCmt_name(String cmt_name) {
        this.cmt_name = cmt_name;
    }

    public String getCmt_text() {
        return cmt_text;
    }

    public void setCmt_text(String cmt_text) {
        this.cmt_text = cmt_text;
    }




}
