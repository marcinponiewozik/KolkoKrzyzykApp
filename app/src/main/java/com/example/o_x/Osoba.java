package com.example.o_x;

import java.io.Serializable;

/**
 * Created by Marcin on 2016-02-03.
 */
public class Osoba implements Serializable {
    private Long id;

    private String login;
    private String haslo;
    private String email;
    private int pkt;

    private int liczbaRozegranychGier;
    private int liczbaWygranych;
    private int liczbaRemisow;
    private int liczbaPorazek;
    private int liczbaSkonczonychGier;

    private byte[] logo;

    public Osoba() {
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPkt() {
        return pkt;
    }

    public void setPkt(int pkt) {
        this.pkt = pkt;
    }

    public int getLiczbaRozegranychGier() {
        return liczbaRozegranychGier;
    }

    public void setLiczbaRozegranychGier(int liczbaRozegranychGier) {
        this.liczbaRozegranychGier = liczbaRozegranychGier;
    }

    public int getLiczbaWygranych() {
        return liczbaWygranych;
    }

    public void setLiczbaWygranych(int liczbaWygranych) {
        this.liczbaWygranych = liczbaWygranych;
    }

    public int getLiczbaRemisow() {
        return liczbaRemisow;
    }

    public void setLiczbaRemisow(int liczbaRemisow) {
        this.liczbaRemisow = liczbaRemisow;
    }

    public int getLiczbaPorazek() {
        return liczbaPorazek;
    }

    public void setLiczbaPorazek(int liczbaPorazek) {
        this.liczbaPorazek = liczbaPorazek;
    }

    public int getLiczbaSkonczonychGier() {
        return liczbaSkonczonychGier;
    }

    public void setLiczbaSkonczonychGier(int liczbaSkonczonychGier) {
        this.liczbaSkonczonychGier = liczbaSkonczonychGier;
    }


    @Override
    public String toString() {
        return "Osoba{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", haslo='" + haslo + '\'' +
                ", email='" + email + '\'' +
                ", pkt=" + pkt +
                ", liczbaRozegranychGier=" + liczbaRozegranychGier +
                ", liczbaWygranych=" + liczbaWygranych +
                ", liczbaRemisow=" + liczbaRemisow +
                ", liczbaPorazek=" + liczbaPorazek +
                ", liczbaSkonczonychGier=" + liczbaSkonczonychGier +
                ", logo=" + logo +
                '}';
    }
}
