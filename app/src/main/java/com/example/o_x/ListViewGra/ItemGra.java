package com.example.o_x.ListViewGra;

import com.example.o_x.Gra;

/**
 * Created by Marcin on 2016-02-03.
 */
public class ItemGra {
    Long id;
    String loginGospodarza;
    String procentZakonczonychGier;

    Gra gra;

    public ItemGra() {
    }


    public String getProcentZakonczonychGier() {
        return procentZakonczonychGier;
    }

    public void setProcentZakonczonychGier(String procentZakonczonychGier) {
        this.procentZakonczonychGier = procentZakonczonychGier;
    }

    public Gra getGra() {
        return gra;
    }

    public void setGra(Gra gra) {
        this.gra = gra;
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
}
