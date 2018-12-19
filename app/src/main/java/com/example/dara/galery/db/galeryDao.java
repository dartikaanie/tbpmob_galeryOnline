package com.example.dara.galery.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.dara.galery.Foto;

import java.util.List;

@Dao
public interface galeryDao {

    //query
    @Query("Select * From galery_online")
    //method
    List<galery> getAllDataFoto();

    @Insert
    void insertDataFoto(galery...galeries);
    //artinya bisa banyak data sekaligus. kata awal merupakan objek dan kata kedua adalah data

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDataFoto(galery galeries);

    @Query("SELECT * FROM galery_online WHERE id =:id")
    Foto selectFoto(int id);


    @Query("UPDATE galery_online SET fav= :fav WHERE id = :id")
    int updateFav(int id, int fav);

    @Query("SELECT * FROM galery_online where fav=1")
    List<galery> getDataFavorit();
}
