package com.dikra.tugasakhir;

import com.dikra.tugasakhir.ann.DataSet;
import com.dikra.tugasakhir.music.MusicProcessor;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by DIKRA on 8/6/2015.
 */
public class MainCreateDataSet {

    public static void main(String[] args){
        File file = new File("res/data.in");
        Scanner sc = null;
        List<DataSet> dataSets = new ArrayList<DataSet>();

        try {
            sc = new Scanner(file);

            while (sc.hasNext()) {
                String inpName = sc.next();

                File f = new File("res/songs/" + inpName + ".json");
                if (f.exists()) continue;

                System.out.println("Creating " + inpName + ".json...");

                boolean success = true;
                try {
                    MusicProcessor mp = new MusicProcessor("res/songs/" + inpName + ".xml");

                    PrintWriter pw = new PrintWriter("res/songs/" + inpName + ".json");
                    pw.println(mp.getMusicJSON(true));
                    pw.close();
                } catch (Exception ex){
                    ex.printStackTrace();
                    success = false;
                } finally {
                    if (success) System.out.println("Success!");
                    else System.out.println("Fail.");
                }


            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}
