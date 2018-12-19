package com.example.dara.galery.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

@Entity(tableName = "galery_online")
public class galery {

    @PrimaryKey
    public  int id;

    @ColumnInfo(name="nama")
    public String nama;

    @ColumnInfo(name="deskripsi")
    public String deskripsi;

    @ColumnInfo(name="lat")
    public double lat;

    @ColumnInfo(name="lng")
    public  double lng;

    @ColumnInfo(name = "path_foto")
    public String path_foto;

    @ColumnInfo(name = "prov")
    public String prov;

    @ColumnInfo(name = "kota")
    public String kota;


    @ColumnInfo(name="fav")
    public int fav;


}
