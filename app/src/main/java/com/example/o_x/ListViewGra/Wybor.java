package com.example.o_x.ListViewGra;

import java.io.Serializable;

/**
 * Created by Marcin on 2016-02-04.
 */
public class Wybor implements Serializable {
    Long idOsoba;
    int pole;

    public Long getIdOsoba() {
        return idOsoba;
    }

    public void setIdOsoba(Long idOsoba) {
        this.idOsoba = idOsoba;
    }

    public int getPole() {
        return pole;
    }

    public void setPole(int pole) {
        this.pole = pole;
    }

    @Override
    public String toString() {
        return "Wybor{" +
                "idOsoba=" + idOsoba +
                ", pole=" + pole +
                '}';
    }
}
