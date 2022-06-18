package com.upt.cti.smc.model;


import java.io.Serializable;

public class Order implements Serializable {

    public String product_photo, product_name, product_size, buyer_name, buyer_address1, buyer_addres2, buyer_city, buyer_state, buyer_zipcode, buyer_phone;

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String documentID;









}
