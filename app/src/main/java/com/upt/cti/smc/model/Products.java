package com.upt.cti.smc.model;

import java.io.Serializable;

public class Products implements Serializable {

    public String nume, imagine, descriere, pret, marime, seller, id, salePrice, categorie, sellerID;
    public String documentID;


    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getDocumentID() {
        return documentID;
    }
}