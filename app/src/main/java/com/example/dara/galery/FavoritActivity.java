package com.example.dara.galery;

import android.app.ActionBar;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.dara.galery.db.AppDatabase;
import com.example.dara.galery.db.galery;

import java.util.ArrayList;
import java.util.List;

public class FavoritActivity extends AppCompatActivity implements FotoAdapter.OnItemClicked {

    RecyclerView rvListFoto;
     FotoAdapter fotoAdapter;
     ProgressBar waiting;
     TextView nodataFav;
    private AppDatabase basisData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorit);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getSupportActionBar().setTitle("Foto Favorit");
        fotoAdapter = new FotoAdapter();
//        fotoAdapter.setDataFoto(getDataFoto(handler,dataFoto));
        fotoAdapter.setClickHandler(this);

        rvListFoto = findViewById(R.id.list_favorit);
        nodataFav = findViewById(R.id.nodataFav);
        rvListFoto.setAdapter(fotoAdapter);
        RecyclerView.LayoutManager layoutManager;

        int orientasi = getResources().getConfiguration().orientation;
        if(orientasi == Configuration.ORIENTATION_PORTRAIT){
            layoutManager = new LinearLayoutManager(this);
        }else{
            layoutManager = new GridLayoutManager(this,2);

        }
        rvListFoto.setLayoutManager(layoutManager);
        rvListFoto.setHasFixedSize(true);
        basisData = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class, "galery_online.db")
                .allowMainThreadQueries()
                .build();
        getDataFavorit();

    }

    private void getDataFavorit() {
        Toast.makeText(this, "Data favorit dari database", Toast.LENGTH_SHORT).show();
        List<galery> listDataFoto = basisData.dataFotoDao().getDataFavorit();
        List<Foto> fotoItemList = new ArrayList<>();
        for (galery galery : listDataFoto) {
            Foto m = new Foto(
                    galery.id,
                    galery.nama,
                    galery.path_foto,
                    galery.deskripsi,
                    galery.lat,
                    galery.lng,
                    galery.kota,
                    galery.prov
            );
            fotoItemList.add(m);
        }
        if (fotoItemList.isEmpty()){
            nodataFav.setVisibility(View.VISIBLE);
        }else{
            nodataFav.setVisibility(View.INVISIBLE);
            fotoAdapter.setDataFoto(new ArrayList<Foto>(fotoItemList));
        }

    }

    public void ItemClicked(Foto foto) {
        Toast.makeText(this, "Item yang diklik adalah : " + foto.getNama(), Toast.LENGTH_SHORT).show();
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("key_foto_parcelable", foto);
        startActivity(detailIntent);
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
