package com.example.o_x;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class UstawieniaActivity extends AppCompatActivity {


    Context context = this;
    EditText etStareHaslo, etNoweHaslo;
    Osoba osobaTemp;

    private Uri mImageCaptureUri;

    Bitmap noweLogo;
    View coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustawienia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNoweHaslo = (EditText) findViewById(R.id.etNoweHaslo);
        etStareHaslo = (EditText) findViewById(R.id.etStareHaslo);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
    }

    public void zmianaHaslo(View view) {
        String nowe = etNoweHaslo.getText().toString();
        String stare = etStareHaslo.getText().toString();
        osobaTemp = Dane.osobaZalogowana;
        if (etNoweHaslo.getText().length() > 4 && etStareHaslo.getText().length() > 4) {
            if (Dane.md5(stare).equals(Dane.osobaZalogowana.getHaslo())) {
                osobaTemp.setHaslo(Dane.md5(nowe));
                PutOsoba zmianaHasla = new PutOsoba();
                zmianaHasla.execute(osobaTemp);
            } else {
                Snackbar.make(view, "Wprowadzono nieprawidłowe hasło", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else {
            Snackbar.make(view, "Haslo musza miec minimum 4 znaki", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void wyloguj(View v) {
        SharedPreferences preferences = getSharedPreferences("uzytkownik", MODE_PRIVATE);
        preferences.edit().clear().commit();
        context.deleteDatabase("test");
        finish();
    }

    public void zmianaZdjecia(View v) {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        Bitmap bitmap = null;
        String path = "";

        if (requestCode == 1) {
            mImageCaptureUri = data.getData();
            path = getRealPathFromURI(mImageCaptureUri); //from Gallery

            if (path == null)
                path = mImageCaptureUri.getPath(); //from File Manager

            if (path != null)
                bitmap = getBitmap(path);
            File file = new File(path);
            float size = file.length() / 1024;

            if (size < 201)
                dialogPotwierdzenie(bitmap);
            else
                Snackbar.make(getCurrentFocus(), "Maksymalny rozmiar zdjęcia to 200Kb", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

        } else
            Snackbar.make(getCurrentFocus(), "Error", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

    }

    public void dialogPotwierdzenie(final Bitmap bitmap) {
        noweLogo = bitmap;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_zmiana_zdjecia, null);
        dialogBuilder.setView(dialogView);

        final CircleImageView ivLogo = (CircleImageView) dialogView.findViewById(R.id.ivLogo);
        ivLogo.setImageBitmap(noweLogo);

        final CircleImageView ivLogoStare = (CircleImageView) dialogView.findViewById(R.id.ivLogoStare);
        byte[] logo = Dane.osobaZalogowana.getLogo();
        Bitmap bitmapLogo = BitmapFactory.decodeByteArray(logo, 0, logo.length);
        ivLogoStare.setImageBitmap(bitmapLogo);

        Button btnObroc = (Button) dialogView.findViewById(R.id.btnObroc);
        btnObroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noweLogo = obroc(noweLogo);
                final CircleImageView ivLogo = (CircleImageView) dialogView.findViewById(R.id.ivLogo);
                ivLogo.setImageBitmap(noweLogo);
            }
        });

        dialogBuilder.setTitle("Potwierdzenie zmiany");
        dialogBuilder.setPositiveButton("Potwierdzam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                osobaTemp = Dane.osobaZalogowana;
                byte[] logo = Dane.bitmapToByteArray(noweLogo);
                osobaTemp.setLogo(logo);
                PutOsoba putOsoba = new PutOsoba();
                putOsoba.execute(osobaTemp);
                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    private Bitmap obroc(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap getBitmap(String path) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(path);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), null, true);
        return bitmap;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private class PutOsoba extends AsyncTask<Osoba, Void, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (dialog != null)
                dialog = null;

            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Osoba... osoba) {

            String url = Dane.URL + "/osoba/" + osoba[0].getId();

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Osoba> requestEntity = new HttpEntity<Osoba>(osoba[0], requestHeaders);

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
                Snackbar.make(coordinatorLayout, "Wystąpił błąd po stronie serwera", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } else {
                zalogujUzytkownika(osobaTemp);
                etNoweHaslo.setText("");
                etStareHaslo.setText("");
                Snackbar.make(coordinatorLayout, "Zmiany zostały zapisane", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
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
