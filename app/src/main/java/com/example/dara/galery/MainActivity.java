package com.example.dara.galery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dara.galery.fragment.addFragment;
import com.example.dara.galery.fragment.galeryFragment;
import com.example.dara.galery.fragment.homeFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;
    public int mode;
    String FRAGMENT_TAG="addFragment";
    private Fragment mContent;
    private android.app.Fragment mContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//            // kita set default nya Home Fragment
        loadFragment(new galeryFragment());
//        // inisialisasi BottomNavigaionView
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
//        // beri listener pada saat item/menu bottomnavigation terpilih
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            if(FRAGMENT_TAG == "addFragmnet"){
                loadFragment(new addFragment());
            }else{
                loadFragment(new galeryFragment());
            }

        }

        Log.e("fragment", FRAGMENT_TAG);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //Jika tombol refresh diklik
        switch (item.getItemId()){
            case   R.id.menu_refresh :
                Toast.makeText(this, "Refreshing data . . .", Toast.LENGTH_SHORT).show();
                galeryFragment.getFoto();
                break;

            case R.id.favorit_menu :
                Toast.makeText(this, "Refreshing data favorite . . .", Toast.LENGTH_SHORT).show();
                Intent favIntent = new Intent(this, FavoritActivity.class);
                startActivity(favIntent);
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().findFragmentByTag(FRAGMENT_TAG);

    }



    // method untuk load fragment yang sesuai
    private boolean loadFragment(android.support.v4.app.Fragment fragment){
        if (fragment != null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment, "galeryFragment")
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.navigation_galery:
                FRAGMENT_TAG="galeryFragment";
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, new galeryFragment(), "galeryFragment")
                    .commit();
                break;
            case R.id.navigation_add:
                FRAGMENT_TAG="addFragment";
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container,  new addFragment(), "addFragment")
                    .commit();
                break;

        }
        return true;
    }

    public Boolean konekkah(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission") NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean konek = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return konek;
    }

}

