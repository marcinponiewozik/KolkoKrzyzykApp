package com.example.o_x.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.o_x.Dane;
import com.example.o_x.GraActivity;
import com.example.o_x.ListViewGraDB.ItemGraDB;
import com.example.o_x.ListViewGraDB.ListViewAdapterDB;
import com.example.o_x.R;
import com.example.o_x.db.DBManager;
import com.example.o_x.db.GraDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 2016-02-14.
 */
public class FragmentZakonczone extends Fragment implements AbsListView.MultiChoiceModeListener {
    private ListView lvMojeGry;
    private Context context;
    private ListViewAdapterDB adapter;

    MenuItem item;
    private List<GraDB> doUsuniecia;

    public FragmentZakonczone() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listagier, container, false);

        context = getContext();
        lvMojeGry = (ListView) view.findViewById(R.id.lvMojeGry);
        InitLayout initLayout = new InitLayout();
        initLayout.execute();
        doUsuniecia = new ArrayList<>();
        lvMojeGry.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvMojeGry.setMultiChoiceModeListener(this);
        return view;
    }

    private void initListView() {
        lvMojeGry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemGraDB item = (ItemGraDB) parent.getAdapter().getItem(position);
                Intent intent = new Intent(context, GraActivity.class);
                intent.putExtra("idGra", item.getId());
                intent.putExtra("pobierzZBazyDanych", true);
                startActivity(intent);
            }
        });
        DBManager manager = new DBManager(context);
        List<GraDB> lista = manager.listaGier(true);

        wypelnijListe(lista);
    }

    private void wypelnijListe(List<GraDB> lista) {
        List<ItemGraDB> temp = new ArrayList<>();
        ItemGraDB item = new ItemGraDB();
        for (int i = 0; i < lista.size(); i++) {
            item = new ItemGraDB();
            item.setId(lista.get(i).getIdGra());
            item.setLoginGospodarza(lista.get(i).getGospodarz().getLogin());
            item.setGraDB(lista.get(i));
            item.setData(Dane.wyswietlDate(lista.get(i).getData()));
            temp.add(item);

        }
        ItemGraDB[] items = new ItemGraDB[temp.size()];
        items = temp.toArray(items);
        adapter = new ListViewAdapterDB(context, R.layout.item_gra_db, items);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            doUsuniecia.add(adapter.getItem(position).getGraDB());
        } else {
            doUsuniecia.remove(adapter.getItem(position).getGraDB());
        }
        item.setTitle(doUsuniecia.size() + " zaznaczonych");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
        item = menu.findItem(R.id.menu_tekst);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                DBManager manager = new DBManager(getContext());
                for (GraDB gra : doUsuniecia) {
                    manager.removeGra(gra);
                }
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        doUsuniecia.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_gra, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.odswiez) {
            initListView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class InitLayout extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            initListView();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            lvMojeGry.setAdapter(adapter);
        }
    }
}
