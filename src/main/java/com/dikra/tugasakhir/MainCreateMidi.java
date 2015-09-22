package com.dikra.tugasakhir;

import com.dikra.tugasakhir.music.MusicPlayer;
import com.dikra.tugasakhir.music.MusicProcessor;

import java.io.File;

/**
 * Created by DIKRA on 9/2/2015.
 */
public class MainCreateMidi {
    public static void main(String[] args){
        for (String fname : args){
            MusicProcessor mp = new MusicProcessor(fname);
            MusicPlayer player = new MusicPlayer(mp);
            player.saveMidi(fname + ".midi");
        }
    }
}
