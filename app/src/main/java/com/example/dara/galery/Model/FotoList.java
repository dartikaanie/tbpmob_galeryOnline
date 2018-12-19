package com.example.dara.galery.Model;

import com.example.dara.galery.Foto;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FotoList {
    @SerializedName("results")
    public List<Foto> results;
}
