package com.ycs.order.model;

import java.lang.reflect.Array;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class Order<i> extends BmobObject {
    private String buyer;
    private String shop_id;
    private ArrayList<String> goods_list;
    private String sum;
    private String receiver;
    private String receive_address;
    private String receive_num;
    private String status;
    private String star;

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceive_address() {
        return receive_address;
    }

    public void setReceive_address(String receive_address) {
        this.receive_address = receive_address;
    }

    public String getReceive_num() {
        return receive_num;
    }

    public void setReceive_num(String receive_num) {
        this.receive_num = receive_num;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public ArrayList<String> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(ArrayList<String> goods_list) {
        this.goods_list = goods_list;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }



}
