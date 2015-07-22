package com.dikra.tugasakhir.music.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class Music {
    protected Part[] parts;

    public Music(){
    }

    public Part[] getParts(){
        return parts;
    }

    public String getMusicJSON(){
        return getMusicJSON(false);
    }

    public String getMusicJSON(boolean prettify){
        Gson gson;
        if (prettify) gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        else gson = new GsonBuilder().serializeNulls().create();

        return gson.toJson(this);
    }

    public void setParts(Part[] _parts){
        parts = _parts;
    }
}
