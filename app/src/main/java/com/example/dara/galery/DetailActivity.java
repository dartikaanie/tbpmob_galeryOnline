package com.example.dara.galery;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.dara.galery.db.AppDatabase;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    TextView nama;
    TextView deskripsi;
    TextView lat,lng, alamat, alamatLengkap;
    ImageView fotoView, favoritImg;
    Foto foto;
    Button unduhBtn, shareBtn;
    AppDatabase basisData;

    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        basisData = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class, "galery_online.db")
                .allowMainThreadQueries()
                .build();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        nama = findViewById(R.id.namaDetail);
        deskripsi = findViewById(R.id.deskripsiDetail);
        lat = findViewById(R.id.latDetail);
        lng = findViewById(R.id.lngDetail);
        alamat = findViewById(R.id.alamatDetail);
        alamatLengkap =  findViewById(R.id.alamatlenkapDetail);
        fotoView  = findViewById(R.id.fotoDetail);
        favoritImg = findViewById(R.id.favorit_img);

        Intent detailIntent = getIntent();
        if(null != detailIntent) {
            foto = detailIntent.getParcelableExtra("key_foto_parcelable");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(foto != null) {
            getSupportActionBar().setTitle(foto.getNama());
            nama.setText(String.valueOf(foto.getNama()));
            deskripsi.setText(foto.getDeskripsi());
            lat.setText(String.valueOf(foto.getLat()));
            lng.setText(String.valueOf(foto.getLng()));
            imageUrl = "http://parit.store/galery/public/foto/" + foto.getPath_foto();
            Glide.with(this).load(imageUrl).into(fotoView);
//            unduhBtn = findViewById(R.id.btn_download);
            shareBtn = findViewById(R.id.btn_share);
            Foto data = basisData.dataFotoDao().selectFoto(foto.getId());
            if(konekkah()){
                alamat.setText( getAddress(foto.getLat(), foto.getLng(),1));
                alamatLengkap.setText( getAddress(foto.getLat(), foto.getLng(),2));

            }else{
                Log.e("data", String.valueOf(data));
                alamat.setText(data.kota);
                alamatLengkap.setText(data.kota+","+data.prov);
            }



            if(data.fav == 1){
                favoritImg.setBackgroundResource(R.drawable.start_fill);
            }else{
                favoritImg.setBackgroundResource(R.drawable.star_default);
            }

            favoritImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickFav(v);
                }
            });

//            unduhBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(DetailActivity.this, "download", Toast.LENGTH_SHORT).show();
//                }
//            });

            shareBtn = findViewById(R.id.btn_share);
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  share(v);
                }
            });
        }
        else{
            Toast.makeText(this, "no Data", Toast.LENGTH_SHORT).show();

        }
    }


     public void share(View v){
//
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String text =  ""+ foto.getNama() + '\n' +
                "ALamat  :" + '\n' +
                alamatLengkap.getText().toString() + '\n';
        share.putExtra(Intent.EXTRA_TEXT, text);

        if(share.resolveActivity(getPackageManager())!= null ){
            startActivity(share);
        }
    }


    private String getAddress(double latitude, double longitude, int pilih) {

           StringBuilder result = new StringBuilder();
           try {
               Geocoder geocoder = new Geocoder(this, Locale.getDefault());
               List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
               if (addresses.size() > 0) {
                   android.location.Address address = addresses.get(0);
                   // 1 untuk kota saja
                   if (pilih == 1){
                       result.append(address.getLocality());
                   }else{
                       result.append(address.getAddressLine(0));
                   }
               }
           } catch (IOException e) {
               Log.e("tag", e.getMessage());
           }
           return result.toString();

    }

    public Boolean konekkah(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission") NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean konek = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return konek;
    }

    public void clickFav(View v){
        Foto m = basisData.dataFotoDao().selectFoto(foto.getId());
        if(m.fav==0){
           basisData.dataFotoDao().updateFav(foto.getId(),1);
            Toast.makeText(this, "Data favorit berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        }else{
           basisData.dataFotoDao().updateFav(foto.getId(),0);
            Toast.makeText(this, "Data favorit berhasil dihapuskan", Toast.LENGTH_SHORT).show();
        }

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
