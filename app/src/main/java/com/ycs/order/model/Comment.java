package com.ycs.order.model;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject {
    private String star;
    private String order_id;
    private String user_id;
    private String shop_id;
    private String text;

    public int getZnum() {
        return znum;
    }

    public void setZnum(int znum) {
        this.znum = znum;
    }

    private int znum;





    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
