package com.dikra.tugasakhir.parser.json;

import com.dikra.tugasakhir.music.model.Music;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class MusicJSON extends Music{


    public MusicJSON(String path){
        boolean success = false;
        try {
            Music music = JSONHelper.load(path, Music.class);
            parts = music.getParts();

            success = true;
        } catch (Exception e){
            e.printStackTrace();

            // reset attributes to null
            parts = null;
        } finally {
            //System.out.println("Parsing MusicJSON success? " + success);
        }

    }
}
