package com.example.o_x.db;

import com.example.o_x.Gra;
import com.example.o_x.ListViewGra.Wybor;
import com.example.o_x.Osoba;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Marcin on 2016-02-03.
 */
@DatabaseTable
public class GraDB {
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField(columnName = "idGra")
    Long idGra;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "gospodarz")
    private Osoba gospodarz;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "przeciwnik")
    private Osoba przeciwnik;
    @DatabaseField(columnName = "zakonczona")
    private boolean zakonczona;
    @DatabaseField(columnName = "ruchGospodarza")
    private boolean ruchGospodarza;
    @DatabaseField(columnName = "idZwyciescy")
    private Long idZwyciescy;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "wybory")
    private Wybor[] wybory;
    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy/MM/dd HH:mm", columnName = "data")
    private Date data;

    public GraDB() {
        this.zakonczona = false;
        this.ruchGospodarza = true;
        this.gospodarz = null;
        this.przeciwnik = null;
        this.idZwyciescy = null;
    }

    public GraDB(Gra gra) {
        this.zakonczona = gra.isZakonczona();
        this.ruchGospodarza = gra.isRuchGospodarza();
        this.gospodarz = gra.getGospodarz();
        this.przeciwnik = gra.getPrzeciwnik();
        this.idZwyciescy = gra.getIdZwyciescy();
        this.idGra = gra.getId();
        Wybor[] wybory = gra.getWybory().toArray(new Wybor[gra.getWybory().size()]);
        this.wybory = wybory;
        this.data = new Date();
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Wybor[] getWybory() {
        return wybory;
    }

    public void setWybory(Wybor[] wybory) {
        this.wybory = wybory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getIdGra() {
        return idGra;
    }

    public void setIdGra(Long idGra) {
        this.idGra = idGra;
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
                ", wyboryGraczy=" + wybory +
                '}';
    }
}
