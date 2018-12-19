package com.example.dara.galery;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

public class Foto implements Parcelable{

    int id;
    int fav;
    String nama;
    String path_foto;
    String deskripsi;
    double lat;
    double lng;
    String kota, prov;


    public Foto(int id, String nama, String path_foto, String deskripsi, double lat, double lng, String kota, String prov) {
        this.id = id;
        this.nama = nama;
        this.path_foto = path_foto;
        this.deskripsi = deskripsi;
        this.lat = lat;
        this.lng = lng;
        this.kota = kota;
        this.prov = prov;
        this.fav=0;
    }

    public Foto() {

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPath_foto() {
        return path_foto;
    }

    public void setPath_foto(String path_foto) {
        this.path_foto = path_foto;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public int getFav() {
            return this.fav;

    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    @Override
    public String toString() {
        return  ""+ nama + '\n' +
                "kota :" + kota + '\n' +
                "Provinsi :" + prov + '\n';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.nama);
        dest.writeString(this.deskripsi);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.path_foto);
        dest.writeString(this.kota);
        dest.writeString(this.prov);
    }

    public static final Parcelable.Creator<Foto> CREATOR = new Parcelable.Creator<Foto>(){
        @Override
        public Foto createFromParcel(Parcel source){
            return new Foto(source);
        }

        @Override
        public  Foto[] newArray(int size){
            return new Foto[size];
        }
    };

    protected Foto(Parcel in){
        this.id = in.readInt();
        this.nama = in.readString();
        this.deskripsi = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.path_foto = in.readString();
        this.kota = in.readString();
        this.prov = in.readString();
    }
}
