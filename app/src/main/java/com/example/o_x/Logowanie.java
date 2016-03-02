package com.example.o_x;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class Logowanie extends AppCompatActivity {

    private EditText etLogin, etHaslo;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initControls();
    }

    private void initControls() {
        etLogin = (EditText) findViewById(R.id.etLogin);
        etHaslo = (EditText) findViewById(R.id.etHaslo);
    }

    public void zalogujSie(View v) {
        GetOSOBA getOSOBA = new GetOSOBA();
        getOSOBA.execute();
    }

    public void rejestracja(View v) {
        Intent intent = new Intent(context, Rejestracja.class);
        startActivity(intent);
    }

    private class GetOSOBA extends AsyncTask<Void, Void, Osoba> {

        private ProgressDialog dialog;

        String login, haslo;

        @Override
        protected void onPreExecute() {
            login = etLogin.getText().toString();
            haslo = Dane.md5(etHaslo.getText().toString());
            if (dialog != null)
                dialog = null;
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Osoba doInBackground(Void... params) {
            String url = Dane.URL + "/osoba/" + login + "/" + haslo;

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
                Snackbar.make(getCurrentFocus(), "Błąd bobierania", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            else {
                zalogujUzytkownika(osoba);
                finish();
            }
        }
    }

    private void zalogujUzytkownika(Osoba osoba) {
        SharedPreferences.Editor preferences = getSharedPreferences("uzytkownik", MODE_PRIVATE).edit();

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
