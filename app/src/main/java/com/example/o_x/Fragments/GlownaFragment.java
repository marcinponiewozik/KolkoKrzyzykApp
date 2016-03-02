package com.example.o_x.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.o_x.Dane;
import com.example.o_x.Gra;
import com.example.o_x.GraActivity;
import com.example.o_x.ListViewGra.ItemGra;
import com.example.o_x.ListViewGra.ListViewAdapter;
import com.example.o_x.ListViewGra.Wybor;
import com.example.o_x.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GlownaFragment extends Fragment {

    private ListView lvGry;


    Context context;
    View view;
    ListViewAdapter adapter;

    public GlownaFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Kolko-krzyzyk");
        view = inflater.inflate(R.layout.fragment_glowna, container, false);
        lvGry = (ListView) view.findViewById(R.id.lvOtwarteGry);
        initListView();


        Get_GRA_List get = new Get_GRA_List();
        if (isOnline())
            get.execute();
        else
            Snackbar.make(view, "Brak połączenia z internetem", Snackbar.LENGTH_SHORT).show();
        return view;
    }

    private void initListView() {
        lvGry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemGra item = (ItemGra) parent.getAdapter().getItem(position);
                Gra gra = new Gra();
                gra = item.getGra();
                gra.setPrzeciwnik(Dane.osobaZalogowana);
                gra.setWybory(new ArrayList<Wybor>());
                PutGRA putGRA = new PutGRA();
                putGRA.execute(gra);
            }
        });
    }

    private class PutGRA extends AsyncTask<Gra, Void, Integer> {
        ProgressDialog dialog;
        Long idGra = 0L;

        @Override
        protected void onPreExecute() {
            if (dialog != null)
                dialog = null;
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Gra... gra) {

            String url = Dane.URL + "/gra/dolacz/" + gra[0].getId() + "/" + Dane.osobaZalogowana.getId();
            idGra = gra[0].getId();
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Gra> requestEntity = new HttpEntity<Gra>(gra[0], requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            } catch (HttpClientErrorException e) {
                return e.getStatusCode().value();
            }

            return responseEntity.getStatusCode().value();
        }

        @Override
        protected void onPostExecute(Integer s) {
            dialog.dismiss();
            if (s != 200) {
                Toast.makeText(getContext(), "Gra nieaktualna", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getContext(), GraActivity.class);
                intent.putExtra("idGra", idGra);
                startActivity(intent);
            }
        }
    }

    private class Get_GRA_List extends AsyncTask<String, Void, Gra[]> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (dialog != null)
                dialog = null;
            dialog = new ProgressDialog(getContext());
            dialog.show();
        }

        @Override
        protected Gra[] doInBackground(String... params) {
            String url;
            if (params.length > 0) {
                url = Dane.URL + "/gra/gracz/" + params[0];
            } else {
                url = Dane.URL + "/gra/dlagracza/" + Dane.osobaZalogowana.getId();
            }
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            Gra[] events = null;
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try {
                ResponseEntity<Gra[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Gra[].class);
                events = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("RRR", e.getMessage());
                return events;
            }

            List<Gra> graList = new ArrayList<>(Arrays.asList(events));
            wypelnijListe(graList);
            return events;
        }

        @Override
        protected void onPostExecute(Gra[] list) {
            dialog.dismiss();
            lvGry.setAdapter(adapter);
        }

        private void wypelnijListe(List<Gra> lista) {
            List<ItemGra> temp = new ArrayList<>();
            ItemGra item = new ItemGra();
            for (int i = 0; i < lista.size(); i++) {
                if (!lista.get(i).isZakonczona()) {
                    item = new ItemGra();
                    item.setId(lista.get(i).getId());
                    item.setLoginGospodarza(lista.get(i).getGospodarz().getLogin());
                    int procent = (int) (((float) lista.get(i).getGospodarz().getLiczbaSkonczonychGier() / (float) lista.get(i).getGospodarz().getLiczbaRozegranychGier()) * 100);
                    item.setProcentZakonczonychGier(procent + "% ukończonych gier");
                    item.setGra(lista.get(i));
                    temp.add(item);
                }
            }
            ItemGra[] items = new ItemGra[temp.size()];
            items = temp.toArray(items);
            adapter = new ListViewAdapter(context, R.layout.item_gra, items);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_glowna_zalogowany, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.szukaj) {
            if (!isOnline()) {
                Snackbar.make(view, "Brak połączenia z internetem", Snackbar.LENGTH_SHORT).show();
                return true;
            }
            dialogSzukaj();
            return true;
        }

        if (id == R.id.odswiez) {
            Fragment fragment = null;
            fragment = new GlownaFragment(context);

            if (fragment != null) {
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogSzukaj() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Szukaj");
        dialog.setContentView(R.layout.dialog_szukaj);
        dialog.setCancelable(true);
        final EditText etLogin = (EditText) dialog.findViewById(R.id.etLogin);
        Button btnSzukaj = (Button) dialog.findViewById(R.id.btnSzukaj);
        btnSzukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = etLogin.getText().toString();
                if (!login.isEmpty()) {
                    Get_GRA_List lista = new Get_GRA_List();
                    lista.execute(etLogin.getText().toString());
                    dialog.dismiss();

                } else {
                    Snackbar.make(view, "Wypełnij pole", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
