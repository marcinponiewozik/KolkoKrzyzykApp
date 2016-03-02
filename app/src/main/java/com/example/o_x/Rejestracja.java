package com.example.o_x;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class Rejestracja extends AppCompatActivity {

    private EditText etLogin, etHaslo, etEmail;
    private Context context = this;

    View coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initControls();
    }

    private void initControls() {
        etLogin = (EditText) findViewById(R.id.etLogin);
        etHaslo = (EditText) findViewById(R.id.etHaslo);
        etEmail = (EditText) findViewById(R.id.etEmail);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
    }

    public void zarejestruj(View view) {
        if (!isOnline()) {
            Snackbar.make(coordinatorLayout, "Brak połączenia z internetem", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        Osoba osoba = new Osoba();
        String login = etLogin.getText().toString();
        String haslo = etHaslo.getText().toString();
        String email = etEmail.getText().toString();
        if (login.length() > 4 && haslo.length() > 5 && poprawnyEmail(email)) {
            osoba.setLogin(login);
            osoba.setHaslo(Dane.md5(haslo));
            osoba.setEmail(email);
            osoba.setPkt(0);
            byte[] logo = Dane.bitmapToByteArray(BitmapFactory.decodeResource(context.getResources(), R.drawable.zdjecie));
            osoba.setLogo(logo);
            PostOSOBA postOSOBA = new PostOSOBA();
            postOSOBA.execute(osoba);
        } else {
            if (!poprawnyEmail(email))
                Snackbar.make(coordinatorLayout, "Niepoprawny adres e-mail", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            else
                Snackbar.make(coordinatorLayout, "Login min 4 znaki\nHaslo min 5 znaków ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }
    }


    public final static boolean poprawnyEmail(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private class PostOSOBA extends AsyncTask<Osoba, Void, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (dialog != null) {
                dialog = null;
            }
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Osoba... osoba) {

            String url = Dane.URL + "/osoba/rejestracja";

            Osoba user = new Osoba();
            user = osoba[0];
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Osoba> requestEntity = new HttpEntity<Osoba>(user, requestHeaders);

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
            dialog.dismiss();
            if (s == 201) {
                finish();
            } else {
                Snackbar.make(coordinatorLayout, "Wystąpił błąd, możliwe że użytkownik o podanej nazwie już istnieje", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
