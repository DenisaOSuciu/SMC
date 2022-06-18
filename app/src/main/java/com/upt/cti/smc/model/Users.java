package com.upt.cti.smc.model;

import java.io.Serializable;

public class Users implements Serializable {
    public String name, prename, email, username, image, token, id;
   ;


    public void setDocumentID(String documentID) {
        this.id = documentID;
    }

    public String getDocumentID() {
        return id;
    }
}
