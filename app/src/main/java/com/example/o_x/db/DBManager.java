package com.example.o_x.db;

import android.content.Context;

import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Marcin on 2015-12-10.
 */
public class DBManager {

    private static DBManager instance;

    static public void init(Context context) {
        if (null == instance) {
            instance = new DBManager(context);
        }
    }

    static public DBManager getInstance() {
        return instance;
    }

    public DBManager(Context context) {
        helper = new DBHelper(context);
    }

    private DBHelper helper;

    private DBHelper getHelper() {
        return helper;
    }

    public List<GraDB> wszystkieGry() {
        List<GraDB> graList = null;
        try {
            graList = getHelper().getGraDao().queryForAll();
            getHelper().close();

//            wishLists = getHelper().getPersonDao().queryBuilder().selectColumns("id","login").where().eq("id",1L).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return graList;
    }

    public List<GraDB> listaGier(boolean zakonczona) {
        List<GraDB> graList = null;
        try {
            graList = getHelper().getGraDao().queryForEq("zakonczona", zakonczona);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return graList;
    }

    public void addGra(GraDB gra) {
        try {
            getHelper().getGraDao().create(gra);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeGra(GraDB gra) {
        try {
            getHelper().getGraDao().deleteById(gra.getId());
        } catch (SQLException e) {

        }
    }

    public void updateGra(GraDB gra) {
        try {
            UpdateBuilder<GraDB, Integer> updateBuilder = getHelper().getGraDao().updateBuilder();
            updateBuilder.where().eq("idGra", gra.getIdGra());
            updateBuilder.updateColumnValue("gospodarz", gra.getGospodarz());
            updateBuilder.updateColumnValue("przeciwnik", gra.getPrzeciwnik());
            updateBuilder.updateColumnValue("zakonczona", gra.isZakonczona());
            updateBuilder.updateColumnValue("ruchGospodarza", gra.isRuchGospodarza());
            updateBuilder.updateColumnValue("idZwyciescy", gra.getIdZwyciescy());
            updateBuilder.updateColumnValue("wybory", gra.getWybory());
            updateBuilder.updateColumnValue("data", gra.getData());

            updateBuilder.update();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GraDB getGra(Long id) {
        try {
            List<GraDB> list = getHelper().getGraDao().queryForAll();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getIdGra().equals(id))
                    return list.get(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
