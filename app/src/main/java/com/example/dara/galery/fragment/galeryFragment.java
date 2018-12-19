package com.example.dara.galery.fragment;


import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dara.galery.DetailActivity;
import com.example.dara.galery.Foto;
import com.example.dara.galery.FotoAdapter;
import com.example.dara.galery.MainActivity;
import com.example.dara.galery.R;
import com.example.dara.galery.TmdbClient;
import com.example.dara.galery.db.AppDatabase;
import com.example.dara.galery.db.galery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class galeryFragment extends Fragment implements FotoAdapter.OnItemClicked {

    static RecyclerView rvListFoto;
    static FotoAdapter fotoAdapter;
    static ProgressBar waiting;
    static TextView load;
    static ImageView noData;


    static Foto dataFoto;
    ArrayList<Foto> fotoList;
    static Activity activity;
    static AppDatabase basisData;
    static int status_data;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
         dataFoto = new Foto();
    }

    @Nullable
    @Override


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_galery, container, false);
        waiting=view.findViewById(R.id.progressBar);
        noData = view.findViewById(R.id.noData);
        load = view.findViewById(R.id.load);
        fotoAdapter = new FotoAdapter();
        fotoAdapter.setClickHandler(this);

        rvListFoto = view.findViewById(R.id.list_galery);
        rvListFoto.setAdapter(fotoAdapter);
        RecyclerView.LayoutManager layoutManager;

        rvListFoto.setVisibility(view.INVISIBLE);
        waiting.setVisibility(view.VISIBLE);
        load.setVisibility(View.VISIBLE);
        status_data=0;
        int orientasi = getResources().getConfiguration().orientation;
        if(orientasi == Configuration.ORIENTATION_PORTRAIT){
            layoutManager = new LinearLayoutManager(getContext());
        }else{
            layoutManager = new GridLayoutManager(getContext(),2);

        }
        rvListFoto.setLayoutManager(layoutManager);
        rvListFoto.setHasFixedSize(true);

        //database
        basisData = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "galery_online.db")
                .allowMainThreadQueries()
                .build();
        getFoto();
        return view;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //Jika tombol refresh diklik
        switch (item.getItemId()){
            case   R.id.menu_refresh :
                Toast.makeText(activity, "Refreshing data . . .", Toast.LENGTH_SHORT).show();
                getFoto();
                return  true;
            default:
                break;
        }
        return false;
    }

    public static void getFoto()  {

        load.setText("Mengambil Data");
        waiting.setVisibility(View.VISIBLE);
        load.setVisibility(View.VISIBLE);
        rvListFoto.setVisibility(View.INVISIBLE);
        noData.setVisibility(View.INVISIBLE);

        if(((MainActivity)activity).konekkah()){
            TmdbClient client = (new Retrofit.Builder()
                    .baseUrl("http://parit.store/galery/public/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build())
                    .create(TmdbClient.class);

            Call<List<Foto>> call = client.getAllGaleries();
            call.enqueue(new Callback<List<Foto>>() {
                @Override
                public void onResponse(Call<List<Foto>> call,  Response<List<Foto>> response) {
                    waiting.setVisibility(View.INVISIBLE);
                    load.setVisibility(View.INVISIBLE);
                    rvListFoto.setVisibility(View.VISIBLE);

                    Toast.makeText(activity, "Berhasil", Toast.LENGTH_SHORT).show();

                    List<Foto> listFotoItem = response.body();
                   if(listFotoItem == null){
                        Toast.makeText(activity, "Maaf, Tidak ada data", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        saveToDb(listFotoItem);
                       Toast.makeText(activity, "Foto ditambahkan ke database", Toast.LENGTH_SHORT).show();
                        fotoAdapter.setDataFoto(new ArrayList<Foto>(listFotoItem));

                    }

                }

                @Override
                public void onFailure(Call<List<Foto>> call, Throwable t) {
                    Toast.makeText(activity, "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
                }
             });
        }else{
            waiting.setVisibility(View.INVISIBLE);
            load.setVisibility(View.INVISIBLE);
            rvListFoto.setVisibility(View.VISIBLE);
            Toast.makeText(activity, "Anda Offline, Data dari database", Toast.LENGTH_SHORT).show();
            List<galery> listDataFoto = basisData.dataFotoDao().getAllDataFoto();
            List<Foto> fotoItemList = new ArrayList<>();
            for(galery galery : listDataFoto){
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
            fotoAdapter.setDataFoto(new ArrayList<Foto>(fotoItemList));
            if(listDataFoto.isEmpty()){
                load.setVisibility(View.VISIBLE);
                load.setText("Data tidak ada, Silakan hidupkan koneksi internet");
                noData.setVisibility(View.VISIBLE);
                waiting.setVisibility(View.INVISIBLE);
            }

        }
    }

    public static void saveToDb(List<Foto> fotoList){
        for (Foto  m : fotoList){
            galery record = new galery();
            record.id = m.getId();
            record.nama= m.getNama();
            record.deskripsi=m.getDeskripsi();
            record.path_foto=m.getPath_foto();
            record.lat=m.getLat();
            record.lng=m.getLng();

            //lokasi
            StringBuilder result = new StringBuilder();
            try {
                Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(m.getLat(),m.getLng(), 1);
                if (addresses.size() > 0) {
                    android.location.Address address = addresses.get(0);
                    record.kota=result.append(address.getAdminArea()).toString();
                    record.prov=result.append(address.getCountryName()).toString();
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }

//            handler.tambah_foto(dataFoto);
            basisData.dataFotoDao().insertDataFoto(record);
            status_data =1;
            Log.e("add database", String.valueOf(record.kota));
        }
    }

    //klik foto

    @Override
    public void ItemClicked(Foto foto) {
        Toast.makeText(activity, "Item yang diklik adalah : " + foto.getNama(), Toast.LENGTH_SHORT).show();
        Intent detailIntent = new Intent(activity, DetailActivity.class);
        detailIntent.putExtra("key_foto_parcelable", foto);
        startActivity(detailIntent);
    }


}
