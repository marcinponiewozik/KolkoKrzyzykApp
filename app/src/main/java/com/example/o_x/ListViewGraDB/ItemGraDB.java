package com.example.o_x.ListViewGraDB;

import com.example.o_x.db.GraDB;

/**
 * Created by Marcin on 2016-02-03.
 */
public class ItemGraDB {
    Long id;
    String loginGospodarza;
    String data;
    GraDB graDB;

    public ItemGraDB() {
    }


    public GraDB getGraDB() {
        return graDB;
    }

    public void setGraDB(GraDB graDB) {
        this.graDB = graDB;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginGospodarza() {
        return loginGospodarza;
    }

    public void setLoginGospodarza(String loginGospodarza) {
        this.loginGospodarza = loginGospodarza;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
