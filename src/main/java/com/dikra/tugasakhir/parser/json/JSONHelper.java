package com.dikra.tugasakhir.parser.json;

import com.google.gson.Gson;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class JSONHelper {

    public static <T> T load(String path, Class<T> generic) throws Exception {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(path));

        T obj = gson.fromJson(br, generic);

        br.close();

        return obj;
    }
}
