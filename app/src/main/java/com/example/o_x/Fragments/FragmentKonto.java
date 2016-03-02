package com.example.o_x.Fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.o_x.Dane;
import com.example.o_x.Osoba;
import com.example.o_x.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentKonto extends Fragment {

    ArrayList<String> labels;
    PieChart pieChart;
    TextView tvRozegrane, tvUkonczone, tvPunkty;
    ImageView ivLogo;

    Long idOsoba = 0L;
    View v;

    public FragmentKonto() {

    }

    public FragmentKonto(Long idOsoba) {
        this.idOsoba = idOsoba;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        labels = new ArrayList<String>();
        labels.add("Wygrane");
        labels.add("Remisy");
        labels.add("Porażki");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_konto, container, false);

        pieChart = (PieChart) v.findViewById(R.id.chart);
        tvRozegrane = (TextView) v.findViewById(R.id.tvRozegraneGry);
        tvUkonczone = (TextView) v.findViewById(R.id.tvUkonczoneGry);
        tvPunkty = (TextView) v.findViewById(R.id.tvPunkty);
        ivLogo = (ImageView) v.findViewById(R.id.ivLogoStare);
        if (idOsoba.equals(Dane.osobaZalogowana.getId()))
            initLayout(Dane.osobaZalogowana);
        else {
            GetOSOBA get = new GetOSOBA();
            get.execute(idOsoba);
        }
        return v;
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
            GetOSOBA get = new GetOSOBA();
            get.execute(idOsoba);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLayout(Osoba osoba) {
        getActivity().setTitle(osoba.getLogin());
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(osoba.getLiczbaWygranych(), 0));
        entries.add(new Entry(osoba.getLiczbaRemisow(), 1));
        entries.add(new Entry(osoba.getLiczbaPorazek(), 2));


        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) value);
            }
        });

        PieData data = new PieData(labels, dataset); // initialize Piedata

        pieChart.setData(data);// set data into chart
        pieChart.setDescription("Statystyki");  // set the description
        pieChart.animateX(3000);
        pieChart.animateY(2000);
        pieChart.setHoleColorTransparent(true);


        int procent = (int) (((float) osoba.getLiczbaSkonczonychGier() / (float) osoba.getLiczbaRozegranychGier()) * 100);
        tvRozegrane.setText("Rozegrane gry: " + osoba.getLiczbaRozegranychGier());
        tvUkonczone.setText("Ukonczone: " + osoba.getLiczbaSkonczonychGier() + " (" + procent + "%)");
        tvPunkty.setText("Liczba punktów: " + osoba.getPkt());
        byte[] logo = osoba.getLogo();
        Bitmap bitmap = BitmapFactory.decodeByteArray(logo, 0, logo.length);
        ivLogo.setImageBitmap(bitmap);
    }

    private class GetOSOBA extends AsyncTask<Long, Void, Osoba> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (dialog != null)
                dialog = null;

            dialog = new ProgressDialog(getContext());
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Osoba doInBackground(Long... params) {
            String url = Dane.URL + "/osoba/" + params[0];
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Osoba osoba = null;
            try {
                ResponseEntity<Osoba> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Osoba.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    osoba = responseEntity.getBody();
                }
            } catch (Exception e) {
                return osoba;
            }

            return osoba;
        }

        @Override
        protected void onPostExecute(Osoba osoba) {
            dialog.dismiss();
            if (osoba == null)
                Snackbar.make(v, "Błąd pobierania", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            else {
                if (osoba.getId().equals(Dane.osobaZalogowana.getId()))
                    zalogujUzytkownika(osoba);
                initLayout(osoba);
            }
        }

        private void zalogujUzytkownika(Osoba osoba) {
            SharedPreferences.Editor preferences = getActivity().getSharedPreferences("uzytkownik", getActivity().MODE_PRIVATE).edit();

            preferences.putBoolean("zalogowany", true);
            preferences.putLong("id", osoba.getId());
            preferences.putString("login", osoba.getLogin());
            preferences.putString("haslo", osoba.getHaslo());
            preferences.putString("email", osoba.getEmail());
            preferences.putString("logo", Base64.encodeToString(osoba.getLogo(), Base64.DEFAULT));

            preferences.putInt("pkt", osoba.getPkt());
            preferences.putInt("liczbaRozegranychGier", osoba.getLiczbaRozegranychGier());
            preferences.putInt("liczbaSkonczonychGier", osoba.getLiczbaSkonczonychGier());
            preferences.putInt("liczbaWygranych", osoba.getLiczbaWygranych());
            preferences.putInt("liczbaRemisow", osoba.getLiczbaRemisow());
            preferences.putInt("liczbaPorazek", osoba.getLiczbaPorazek());
            preferences.apply();

            Dane.osobaZalogowana = osoba;

        }
    }
}
