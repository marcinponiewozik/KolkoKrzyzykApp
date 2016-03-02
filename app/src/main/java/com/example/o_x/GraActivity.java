package com.example.o_x;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.o_x.ListViewGra.Wybor;
import com.example.o_x.db.DBManager;
import com.example.o_x.db.GraDB;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GraActivity extends AppCompatActivity {

    private Gra gra = null, temp;

    CircleImageView ivGospodarz, ivPrzeciwnik;
    ImageView ivFiguraUzytkownika;

    ImageButton btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8;

    private Context context = this;
    int tempPole = 10;
    ImageButton tempBtn = null;

    Long idGra;

    Button btnZatwierdz;

    private boolean mojRuch = false;
    private boolean pobierzZbazyDanych = false;
    DBManager manager;

    View coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        idGra = getIntent().getLongExtra("idGra", 0L);
        pobierzZbazyDanych = getIntent().getBooleanExtra("pobierzZBazyDanych", false);
        initControls();
        if (pobierzZbazyDanych)
            pobierzZBazy();
        else
            pobierzZSerwera();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void pobierzZBazy() {
        DBManager manager = new DBManager(context);
        wypelnij(new Gra(manager.getGra(idGra)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gra, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.odswiez) {
            pobierzZSerwera();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void initPlansza() {
        List<Wybor> wyboryGraczy = gra.getWybory();
        if (wyboryGraczy != null) {
            for (int i = 0; i < wyboryGraczy.size(); i++) {
                Wybor wybor = new Wybor();
                wybor = wyboryGraczy.get(i);
                ustawPole(wybor.getPole(), wybor.getIdOsoba().equals(gra.getGospodarz().getId()));
            }
        }
        btnZatwierdz.setEnabled(mojRuch);
    }

    private void ustawPole(int pole, boolean gospodarz) {
        if (pole == 1) {
            btn0.setEnabled(false);
            if (gospodarz)
                btn0.setImageResource(R.drawable.kolko);
            else
                btn0.setImageResource(R.drawable.krzyzyk);
        } else if (pole == 2) {
            btn1.setEnabled(false);
            if (gospodarz)
                btn1.setImageResource(R.drawable.kolko);
            else
                btn1.setImageResource(R.drawable.krzyzyk);
        } else if (pole == 3) {
            btn2.setEnabled(false);
            if (gospodarz)
                btn2.setImageResource(R.drawable.kolko);
            else
                btn2.setImageResource(R.drawable.krzyzyk);
        } else if (pole == 4) {
            btn3.setEnabled(false);
            if (gospodarz)
                btn3.setImageResource(R.drawable.kolko);
            else
                btn3.setImageResource(R.drawable.krzyzyk);
        } else if (pole == 5) {
            btn4.setEnabled(false);
            if (gospodarz)
                btn4.setImageResource(R.drawable.kolko);
            else
                btn4.setImageResource(R.drawable.krzyzyk);
        } else if (pole == 6) {
            btn5.setEnabled(false);
            if (gospodarz)
                btn5.setImageResource(R.drawable.kolko);
            else
                btn5.setImageResource(R.drawable.krzyzyk);
        } else if (pole == 7) {
            btn6.setEnabled(false);
            if (gospodarz)
                btn6.setImageResource(R.drawable.kolko);
            else
                btn6.setImageResource(R.drawable.krzyzyk);
        } else if (pole == 8) {
            btn7.setEnabled(false);
            if (gospodarz)
                btn7.setImageResource(R.drawable.kolko);
            else
                btn7.setImageResource(R.drawable.krzyzyk);
        } else if (pole == 9) {
            btn8.setEnabled(false);
            if (gospodarz)
                btn8.setImageResource(R.drawable.kolko);
            else
                btn8.setImageResource(R.drawable.krzyzyk);
        }
    }

    private void initControls() {
        btn0 = (ImageButton) findViewById(R.id.btn0);
        btn1 = (ImageButton) findViewById(R.id.btn1);
        btn2 = (ImageButton) findViewById(R.id.btn2);
        btn3 = (ImageButton) findViewById(R.id.btn3);
        btn4 = (ImageButton) findViewById(R.id.btn4);
        btn5 = (ImageButton) findViewById(R.id.btn5);
        btn6 = (ImageButton) findViewById(R.id.btn6);
        btn7 = (ImageButton) findViewById(R.id.btn7);
        btn8 = (ImageButton) findViewById(R.id.btn8);
        ivFiguraUzytkownika = (ImageView) findViewById(R.id.ivFiguraUzytkownika);
        btnZatwierdz = (Button) findViewById(R.id.btnGotowe);

        ivGospodarz = (CircleImageView) findViewById(R.id.ivGospodarz);
        ivPrzeciwnik = (CircleImageView) findViewById(R.id.ivPrzeciwnik);
    }

    private void pobierzZSerwera() {
        GetGRA getGRA = new GetGRA();
        getGRA.execute(idGra);
    }

    public void wykonajRuch(View v) {
        if (mojRuch) {
            if (tempBtn != null)
                tempBtn.setImageResource(R.drawable.line);
            initPlansza();
            ImageButton btn = (ImageButton) v;
            int id = v.getId();
            int pole = 9;
            if (id == R.id.btn0)
                pole = 1;
            else if (id == R.id.btn1)
                pole = 2;
            else if (id == R.id.btn2)
                pole = 3;
            else if (id == R.id.btn3)
                pole = 4;
            else if (id == R.id.btn4)
                pole = 5;
            else if (id == R.id.btn5)
                pole = 6;
            else if (id == R.id.btn6)
                pole = 7;
            else if (id == R.id.btn7)
                pole = 8;
            else if (id == R.id.btn8)
                pole = 9;


            if (v.isEnabled())
                zapamietajRuch(pole, Dane.osobaZalogowana.getId().equals(gra.getGospodarz().getId()), btn);
        } else {
            Snackbar.make(coordinatorLayout, "Ruch przeciwnika", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }

    }

    private void zapamietajRuch(int pole, boolean gospodarz, ImageButton btn) {
        if (pole == 10) {
            Snackbar.make(coordinatorLayout, "Niedozwolony ruch", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }
        if (gospodarz)
            btn.setImageResource(R.drawable.kolko_pomarancz);
        else
            btn.setImageResource(R.drawable.krzyzyk_pomarancz);

        tempBtn = btn;
        tempPole = pole;
    }

    public void gotowe(View v) {
        Wybor w = new Wybor();
        w.setIdOsoba(Dane.osobaZalogowana.getId());
        w.setPole(tempPole);
        List<Wybor> list;
        list = gra.getWybory();
        list.add(w);

        temp = new Gra();
        temp = gra;
        temp.setWybory(list);
        temp.setRuchGospodarza(!gra.isRuchGospodarza());

        PutGRA putGRA = new PutGRA();
        putGRA.execute(w);
    }

    private class GetGRA extends AsyncTask<Long, Void, Gra> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (dialog != null)
                dialog = null;
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Gra doInBackground(Long... params) {
            String url = Dane.URL + "/gra/" + params[0];
// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));

            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);


// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            Gra gra = null;
            try {
                ResponseEntity<Gra> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Gra.class);
                gra = responseEntity.getBody();
            } catch (Exception e) {
                return gra;
            }

            return gra;
        }

        @Override
        protected void onPostExecute(Gra graNEW) {
            dialog.dismiss();
            if (graNEW == null)
                Snackbar.make(coordinatorLayout, "Błąd bopierania", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            else {
                manager = new DBManager(getApplicationContext());
                if (gra == null) {
                    manager.addGra(new GraDB(graNEW));
                } else {
                    manager.updateGra(new GraDB(graNEW));
                }
                wypelnij(graNEW);
            }
        }
    }

    private void wypelnij(Gra graNEW) {
        gra = graNEW;
        initPlansza();

        byte[] logoGospodarz = gra.getGospodarz().getLogo();
        ivGospodarz.setImageBitmap(BitmapFactory.decodeByteArray(logoGospodarz, 0, logoGospodarz.length));
        if (gra.getPrzeciwnik() != null) {
            byte[] logoPrzeciwnik = gra.getPrzeciwnik().getLogo();
            ivPrzeciwnik.setImageBitmap(BitmapFactory.decodeByteArray(logoPrzeciwnik, 0, logoPrzeciwnik.length));
        }

        if ((gra.isRuchGospodarza() && gra.getGospodarz().getId().equals(Dane.osobaZalogowana.getId()))
                || (!gra.isRuchGospodarza() && !gra.getGospodarz().getId().equals(Dane.osobaZalogowana.getId()))) {
            mojRuch = true;
        } else {
            mojRuch = false;
        }
        if (gra.isZakonczona())
            btnZatwierdz.setEnabled(false);
        else
            btnZatwierdz.setEnabled(mojRuch);

        komunikat();
    }

    private void komunikat() {
        if (gra.isZakonczona()) {
            if (gra.getIdZwyciescy() == null) {
                Snackbar.make(coordinatorLayout, "Remis", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } else if (gra.getIdZwyciescy().equals(Dane.osobaZalogowana.getId())) {
                Snackbar.make(coordinatorLayout, "Wygrana", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } else {
                Snackbar.make(coordinatorLayout, "Porażka", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }
    }

    private class PutGRA extends AsyncTask<Wybor, Void, Integer> {
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
        protected Integer doInBackground(Wybor... wbor) {

            String url = Dane.URL + "/gra/" + idGra;

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Wybor> requestEntity = new HttpEntity<Wybor>(wbor[0], requestHeaders);

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
            if (s != 204) {
                Snackbar.make(coordinatorLayout, "Błąd po stronie serwera. Spróbuj ponownie", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } else {
                gra = temp;
                manager = new DBManager(getApplicationContext());
                manager.updateGra(new GraDB(gra));
                tempBtn = null;
                tempPole = 9;
                mojRuch = !mojRuch;
                initPlansza();
            }
        }
    }

    public void pokazProfil(View v) {
        CircleImageView item = (CircleImageView) v;
        if (item.getId() == R.id.ivGospodarz) {
            Intent intent = new Intent(context, OsobaActivity.class);
            intent.putExtra("idOsoba", gra.getGospodarz().getId());
            startActivity(intent);
        } else if (item.getId() == R.id.ivPrzeciwnik && (gra.getPrzeciwnik() != null)) {
            Intent intent = new Intent(context, OsobaActivity.class);
            intent.putExtra("idOsoba", gra.getPrzeciwnik().getId());
            startActivity(intent);
        }
    }
}
