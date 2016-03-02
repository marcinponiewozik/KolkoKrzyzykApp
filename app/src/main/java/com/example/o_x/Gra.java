package com.example.o_x;

import com.example.o_x.ListViewGra.Wybor;
import com.example.o_x.db.GraDB;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 2016-02-03.
 */
@DatabaseTable
public class Gra {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField
    private Osoba gospodarz;
    @DatabaseField
    private Osoba przeciwnik;
    @DatabaseField
    private boolean zakonczona;
    @DatabaseField
    private boolean ruchGospodarza;
    @DatabaseField
    private Long idZwyciescy;
    @DatabaseField
    private List<Wybor> wybory;


    public Gra(GraDB gra) {
        this.zakonczona = gra.isZakonczona();
        this.ruchGospodarza = gra.isRuchGospodarza();
        this.gospodarz = gra.getGospodarz();
        this.przeciwnik = gra.getPrzeciwnik();
        this.idZwyciescy = gra.getIdZwyciescy();
        this.id = gra.getIdGra();
        this.wybory = new ArrayList<>();
        for (int i = 0; i < gra.getWybory().length; i++) {
            wybory.add(gra.getWybory()[i]);
        }
    }

    public Gra() {
        this.zakonczona = false;
        this.ruchGospodarza = true;
        this.gospodarz = null;
        this.przeciwnik = null;
        this.idZwyciescy = null;
        this.wybory = new ArrayList<>();
    }

    public List<Wybor> getWybory() {
        return wybory;
    }

    public void setWybory(List<Wybor> wyboryGraczy) {
        this.wybory = wyboryGraczy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Osoba getGospodarz() {
        return gospodarz;
    }

    public void setGospodarz(Osoba gospodarz) {
        this.gospodarz = gospodarz;
    }

    public Osoba getPrzeciwnik() {
        return przeciwnik;
    }

    public void setPrzeciwnik(Osoba przeciwnik) {
        this.przeciwnik = przeciwnik;
    }

    public boolean isZakonczona() {
        return zakonczona;
    }

    public void setZakonczona(boolean zakonczona) {
        this.zakonczona = zakonczona;
    }

    public boolean isRuchGospodarza() {
        return ruchGospodarza;
    }

    public void setRuchGospodarza(boolean ruchGospodarza) {
        this.ruchGospodarza = ruchGospodarza;
    }

    public Long getIdZwyciescy() {
        return idZwyciescy;
    }

    public void setIdZwyciescy(Long idZwyciescy) {
        this.idZwyciescy = idZwyciescy;
    }

    @Override
    public String toString() {
        return "Gra{" +
                "id=" + id +
                ", gospodarz=" + gospodarz +
                ", przeciwnik=" + przeciwnik +
                ", zakonczona=" + zakonczona +
                ", ruchGospodarza=" + ruchGospodarza +
                ", idZwyciescy=" + idZwyciescy +
                ", wybory=" + wybory +
                '}';
    }
}
