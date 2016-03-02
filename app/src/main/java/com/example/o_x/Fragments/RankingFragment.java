package com.example.o_x.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.o_x.ListViewRanking.ItemOsoba;
import com.example.o_x.ListViewRanking.ListViewAdapterRanking;
import com.example.o_x.Osoba;
import com.example.o_x.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RankingFragment extends Fragment {

    Context context;
    ListView lvRanking;
    private String URL;

    public RankingFragment(Context context, String URL) {
        this.context = context;
        this.URL = URL;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Ranking");
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        lvRanking = (ListView) view.findViewById(R.id.lvRanking);
        initListView(0);
        return view;
    }

    private void initListView(int przedzial) {
        Get_RANKING ranking = new Get_RANKING();
        ranking.execute(przedzial);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_moje_gry, menu);
        View spinner = menu.findItem(R.id.menu_spinner).getActionView();
        List<String> list = new ArrayList<>();
        list.add("1   -   10");
        list.add("10  -   50");
        list.add("50  -  100");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list);
        Spinner spnPrzedzial;

        if (spinner instanceof Spinner) {
            spnPrzedzial = (Spinner) spinner;
            spnPrzedzial.setAdapter(adapter);

            spnPrzedzial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    initListView(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
//        super.onCreateOptionsMenu(menu, inflater);
    }

    private class Get_RANKING extends AsyncTask<Integer, Void, Osoba[]> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (dialog != null)
                dialog = null;
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Osoba[] doInBackground(Integer... przedzial) {
            String url = URL + "/osoba/ranking/przedzial/" + przedzial[0];

// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Add the Gson message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            Osoba[] events = null;
            try {
                ResponseEntity<Osoba[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Osoba[].class);
                events = responseEntity.getBody();

            } catch (Exception e) {
                return events;
            }

            return events;
        }

        @Override
        protected void onPostExecute(Osoba[] list) {
            dialog.dismiss();
            List<Osoba> osoby = new ArrayList<>(Arrays.asList(list));
            wypelnijListe(osoby);
        }

        private void wypelnijListe(List<Osoba> lista) {
            List<ItemOsoba> temp = new ArrayList<>();
            ItemOsoba item;
            for (int i = 0; i < lista.size(); i++) {
                item = new ItemOsoba(lista.get(i));
                temp.add(item);
            }
            ItemOsoba[] items = new ItemOsoba[temp.size()];
            items = temp.toArray(items);
            ListViewAdapterRanking adapter = new ListViewAdapterRanking(context, R.layout.item_ranking, items);

            lvRanking.setAdapter(adapter);
        }
    }


}
