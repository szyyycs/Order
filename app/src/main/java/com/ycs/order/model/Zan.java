package com.ycs.order.model;

import cn.bmob.v3.BmobObject;

public class Zan extends BmobObject {
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    private String commentid;

}
