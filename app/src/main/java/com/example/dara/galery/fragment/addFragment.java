package com.example.dara.galery.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dara.galery.MainActivity;
import com.example.dara.galery.R;
import com.example.dara.galery.TmdbClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class addFragment extends Fragment {

    Intent intent;
    Button btn_choose_image;
    ImageView imageView;
    Bitmap bitmap, decoded;
     public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;

    int bitmap_size = 40; // image quality 1 - 100;
    int max_resolution_image = 800;

    //untuk location
    private static  final  int REQUEST_LOCATION =1;
    private EditText ETnama, ETdeskripsi, ETlokasi;
    TextView TextAlert,simpanText;
    private Button tambahBtn, button_location;
    LocationManager locationManager;
    String latitude, longtitude;
    Menu refresh;
    int status_image;
    ProgressBar simpanProgres;


    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // jangan hapus fragment ini saat activity dibuat ulang.
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.menu_refresh);
        item.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        btn_choose_image = view.findViewById(R.id.btn_choose_image);
        imageView = view.findViewById(R.id.imageView);
        refresh = view.findViewById(R.id.menu_refresh);
        TextAlert = view.findViewById(R.id.alert_foto);
        TextAlert.setVisibility(view.INVISIBLE);
        status_image =0;
        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



        return view;
    }

    private void selectImage() {
        imageView.setImageResource(0);
        final CharSequence[] items = {"Ambil Foto", "Ambil dari Galery",
                "Batal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Pilih Foto!");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Ambil Foto")) {
                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[item].equals("Ambil dari Galery")) {
                    intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Pilih Foto"), SELECT_FILE);
                } else if (items[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "requestCode " + requestCode + ", resultCode " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    super.onActivityResult(requestCode,resultCode,data);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {
                    // mengambil gambar dari Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            status_image =1;
        }
    }

    // Untuk menampilkan bitmap pada ImageView
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageView.setImageBitmap(decoded);
    }

    // Untuk resize bitmap
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


        ETnama = view.findViewById(R.id.edit_text_name);
        ETdeskripsi = view.findViewById(R.id.edit_text_deskripsi);
        ETlokasi = view.findViewById(R.id.edit_text_lat);
        tambahBtn = view.findViewById(R.id.tambah_foto);
        button_location = view.findViewById(R.id.button_location);
        simpanProgres = view.findViewById(R.id.simpanProgress);
        simpanText = view.findViewById(R.id.simpanData);

        simpanProgres.setVisibility(View.INVISIBLE);
        simpanText.setVisibility(View.INVISIBLE);
        tambahBtn.setVisibility(View.VISIBLE);

//        TextView = view.findViewById(R.id.text_location);

        //klik button_location
        button_location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    buildAlertMessageNoGPS();
                }
                else
                    {
                        getLocation();
                    }

            }
        });

        //insert

        // lakukan validasi terlebih dahulu, jika sudah benar, maka lakukan proses insert data
        tambahBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(TextUtils.isEmpty(ETnama.getText().toString())){
                    ETnama.setError(getResources().getString(R.string.msg_cannot_allow_empty_field));
                }else if(TextUtils.isEmpty(ETdeskripsi.getText().toString())) {
                    ETdeskripsi.setError(getResources().getString(R.string.msg_cannot_allow_empty_field));
                }
                else if(TextUtils.isEmpty(ETlokasi.getText().toString())) {
                    ETlokasi.setError(getResources().getString(R.string.msg_cannot_allow_empty_field));
                } else if( status_image == 0) {
                    TextAlert.setVisibility(view.VISIBLE);
                }else{

                    if(((MainActivity)activity).konekkah()){
                        simpanProgres.setVisibility(View.VISIBLE);
                        simpanText.setVisibility(View.VISIBLE);
                        tambahBtn.setVisibility(View.INVISIBLE);
                        String nama = ETnama.getText().toString();
                        String deskripsi = ETdeskripsi.getText().toString();
                        Double lat = Double.valueOf(latitude);
                        Double lng = Double.valueOf(longtitude);
                        String foto = getBase64String(imageView);
                        TmdbClient client = (new Retrofit.Builder()
                                .baseUrl("http://parit.store/galery/public/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build())
                                .create(TmdbClient.class);
                        Call<ResponseBody> call = client.newFoto(nama, deskripsi,lat, lng, foto);
                        Log.e("foto", foto);

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                ResponseBody s = response.body();
                                Toast.makeText(activity, "berhasil disimpan", Toast.LENGTH_LONG).show();
                                ((MainActivity) activity).getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fl_container, new galeryFragment(), "galeryFragment")
                                        .commit();

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        //tidak konek internet
                        Toast.makeText(activity, "Tambah data Gagal, anda tidak terkoneksi internet", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }


    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        String lokasi = ETlokasi.getText().toString();
        String nama = ETnama.getText().toString();
        String deskripsi = ETdeskripsi.getText().toString();
        outState.putString("data_lokasi", lokasi);
        outState.putString("data_nama", nama);
        outState.putString("data_deskripsi",deskripsi);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( savedInstanceState!=null){
            String lokasi = savedInstanceState.getString("data_lokasi","");
            String nama = savedInstanceState.getString("data_nama","");
            String deskripsi = savedInstanceState.getString("data_deskripsi","");
            ETlokasi.setText(lokasi);
            ETnama.setText(nama);
            ETdeskripsi.setText(deskripsi);

        }


    }

   public void getLocation(){
        if((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
                &&
                (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(getActivity(), new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
       }
       else{
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null){
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                latitude = String.valueOf(lat);
                longtitude = String.valueOf(lng);
                ETlokasi.setText("Lat  :" + latitude +'\n'+", long  :"+ longtitude);
            }
            else{
                Toast.makeText(getActivity(),"Lokasi Gagal diambil", Toast.LENGTH_SHORT).show();
            }
        }
   }

   protected void buildAlertMessageNoGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Hidupkan GPS mu")
                .setCancelable(false)
                .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
   }

    //konvert gambar ke string
    private String getBase64String(ImageView imageFoto) {

        BitmapDrawable drawable = (BitmapDrawable) imageFoto.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
