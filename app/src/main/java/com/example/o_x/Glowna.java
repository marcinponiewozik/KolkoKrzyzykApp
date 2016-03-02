package com.example.o_x;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.o_x.Fragments.FragmentKonto;
import com.example.o_x.Fragments.GlownaFragment;
import com.example.o_x.Fragments.NiezalogowanyFragment;
import com.example.o_x.Fragments.RankingFragment;
import com.example.o_x.ListViewGra.Wybor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;

public class Glowna extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = this;

    ProgressDialog dialog;

    private boolean zalogowany = false;
    FloatingActionButton fabADD;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sprawdzCzyZalogoawny();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glowna);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        if (zalogowany)
            manager.beginTransaction().replace(R.id.frame_container, new GlownaFragment(context)).commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabADD = (FloatingActionButton) findViewById(R.id.fab);
        fabADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline())
                    nowaGra();
                else
                    Snackbar.make(view, "Brak połączenia z internetem", Snackbar.LENGTH_SHORT).show();
            }
        });


        initProgressDialog();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sprawdzCzyZalogoawny();
        fabADD.setVisibility(View.INVISIBLE);

        if (zalogowany) {
            fabADD.setVisibility(View.VISIBLE);
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.frame_container, new GlownaFragment(context)).commit();
        } else {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.frame_container, new NiezalogowanyFragment()).commit();
        }
        drawer.closeDrawer(Gravity.LEFT);
    }

    private void nowaGra() {
        Gra gra = new Gra();
        gra.setRuchGospodarza(false);
        gra.setZakonczona(false);
        gra.setWybory(new ArrayList<Wybor>());
        gra.setGospodarz(Dane.osobaZalogowana);
        PostGRA postGRA = new PostGRA();
        postGRA.execute(gra);
    }

    private void sprawdzCzyZalogoawny() {
        SharedPreferences preferences = getSharedPreferences("uzytkownik", MODE_PRIVATE);
        zalogowany = false;
        if (preferences.getBoolean("zalogowany", false)) {
            zalogowany = true;
            Long id = preferences.getLong("id", 0L);
            String login = preferences.getString("login", null);
            String haslo = preferences.getString("haslo", null);
            String email = preferences.getString("email", null);
            String logo = preferences.getString("logo", null);

            int pkt = preferences.getInt("pkt", 0);
            int liczbaRozegranychGier = preferences.getInt("liczbaRozegranychGier", 0);
            int liczbaWygranych = preferences.getInt("liczbaWygranych", 0);
            int liczbaRemisow = preferences.getInt("liczbaRemisow", 0);
            int liczbaPorazek = preferences.getInt("liczbaPorazek", 0);
            int liczbaSkonczonychGier = preferences.getInt("liczbaSkonczonychGier", 0);
            Osoba zalogowana = new Osoba();
            zalogowana.setId(id);
            zalogowana.setLogin(login);
            zalogowana.setHaslo(haslo);
            zalogowana.setEmail(email);
            zalogowana.setPkt(pkt);
            byte[] logoByte = Base64.decode(logo, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(logoByte, 0, logoByte.length);
            zalogowana.setLogo(Dane.bitmapToByteArray(bitmap));
            zalogowana.setLiczbaRozegranychGier(liczbaRozegranychGier);
            zalogowana.setLiczbaPorazek(liczbaPorazek);
            zalogowana.setLiczbaRemisow(liczbaRemisow);
            zalogowana.setLiczbaSkonczonychGier(liczbaSkonczonychGier);
            zalogowana.setLiczbaWygranych(liczbaWygranych);
            Dane.osobaZalogowana = zalogowana;
        }
    }


    private void initProgressDialog() {
        if (dialog != null)
            dialog = null;
        dialog = new ProgressDialog(context);
        dialog.setMessage("Czekaj...");
        dialog.setCancelable(false);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Fragment fragment = null;
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_Glowna:
                fragment = new GlownaFragment(context);
                fabADD.show();
                break;
            case R.id.nav_Lista:
                if (zalogowany) {
                    Intent intent = new Intent(context, MojeGry.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_Ranking:
                fragment = new RankingFragment(context, Dane.URL);
                fabADD.hide();
                break;
            case R.id.nav_Konto:
                if (zalogowany) {
                    fragment = new FragmentKonto(Dane.osobaZalogowana.getId());
                    fabADD.hide();
                }
                break;
            case R.id.nav_Ustawienia:
                if (zalogowany) {
                    Intent intent2 = new Intent(context, UstawieniaActivity.class);
                    startActivity(intent2);
                }
                break;
            case R.id.nav_Wyjdz:
                finish();
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            drawer.closeDrawer(Gravity.LEFT);
        }
        return false;
    }


    private class PostGRA extends AsyncTask<Gra, Void, Integer> {

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Gra... g) {

            String url = Dane.URL + "/gra";

            Gra gra = new Gra();
            gra = g[0];
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Gra> requestEntity = new HttpEntity<>(gra, requestHeaders);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            } catch (HttpClientErrorException e) {
                return e.getStatusCode().value();
            }

            return responseEntity.getStatusCode().value();
        }

        @Override
        protected void onPostExecute(Integer s) {
            if (s == 204) {
                GetGRAPoIdOsoba getGRA = new GetGRAPoIdOsoba();
                getGRA.execute(Dane.osobaZalogowana.getId());
            } else {
                dialog.dismiss();
                Snackbar.make(getCurrentFocus(), "Bład po stronie serwera", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private class GetGRAPoIdOsoba extends AsyncTask<Long, Void, Long> {

        @Override
        protected Long doInBackground(Long... params) {
            String url = Dane.URL + "/gra/ostatniaGra/" + params[0];
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("text", "plain")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Long idGra = null;
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                idGra = Long.parseLong(responseEntity.getBody().toString());
            } catch (Exception e) {
                return 0L;
            }

            return idGra;
        }

        @Override
        protected void onPostExecute(Long idGra) {
            dialog.dismiss();
            if (idGra == 0L)
                Snackbar.make(getCurrentFocus(), "Bład podczas pobierania gry", Snackbar.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(context, GraActivity.class);
                intent.putExtra("idGra", idGra);
                startActivity(intent);
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
