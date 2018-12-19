package com.example.dara.galery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {


    // static variable
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "galery_online";

    // table name
    private static final String TABLE_GALERIES = "galeries";

    // column tables
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PATH = "path_foto";
    private static final String KEY_DESC = "deskripsi";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_KOTA = "kota";
    private static final String KEY_PROV = "provinsi";


    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_GALERIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PATH + " TEXT,"
                + KEY_DESC + " TEXT,"
                + KEY_LAT + " TEXT,"
                + KEY_LNG + " TEXT,"
                + KEY_KOTA + " TEXT,"
                + KEY_PROV + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GALERIES);
        onCreate(db);
    }

    //tambah data
    public void tambah_foto(Foto fotos){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, fotos.getNama());
        values.put(KEY_PATH, fotos.getPath_foto());
        values.put(KEY_DESC, fotos.getDeskripsi());
        values.put(KEY_LAT, fotos.getLat());
        values.put(KEY_LNG, fotos.getLng());
        values.put(KEY_KOTA, fotos.getKota());
        values.put(KEY_PROV, fotos.getProv());

        db.insert(TABLE_GALERIES, null, values);
        db.close();
    }

    public Foto getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

       Cursor cursor = db.query(TABLE_GALERIES, new String[] { KEY_ID,
                        KEY_NAME, KEY_PATH, KEY_DESC, KEY_LAT, KEY_LNG , KEY_KOTA, KEY_PROV}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Foto data = new Foto(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getDouble(4),
                cursor.getDouble(5),
                cursor.getString(6),
                cursor.getString(7));

        return data;
    }


    // get All Record
    public List<Foto> getAllDatas() {
        List<Foto> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GALERIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Foto foto =  new Foto(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getString(6),
                        cursor.getString(7));

                foto.setId(Integer.parseInt(cursor.getString(0)));
                foto.setNama(cursor.getString(1));
                foto.setPath_foto(cursor.getString(2));
                foto.setDeskripsi(cursor.getString(3));
                foto.setLat(cursor.getDouble(4));
                foto.setLng(cursor.getDouble(5));
                foto.setKota(cursor.getString(6));
                foto.setProv(cursor.getString(7));

                dataList.add(foto);
            } while (cursor.moveToNext());
        }

        return dataList;
    }


    //htung jumlah  record
    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_GALERIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    //update data
    public int update(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, foto.getNama());
        values.put(KEY_PATH, foto.getPath_foto());
        values.put(KEY_DESC, foto.getDeskripsi());
        values.put(KEY_LAT, foto.getLat());
        values.put(KEY_LNG, foto.getLng());
        values.put(KEY_KOTA, foto.getKota());
        values.put(KEY_PROV, foto.getProv());


        // updating row
        return db.update(TABLE_GALERIES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(foto.getId()) });
    }

    //Hapus
    public void delete(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GALERIES, KEY_ID + " = ?",
                new String[] { String.valueOf(foto.getId()) });
        db.close();
    }
}
