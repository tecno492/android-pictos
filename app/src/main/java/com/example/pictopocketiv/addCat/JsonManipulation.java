package com.example.pictopocketiv.addCat;


import android.content.Context;
import android.content.res.AssetManager;

import com.example.pictopocketiv.arasaac.ArasaacModel;
import com.example.pictopocketiv.localpersistence.Populator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;


public class JsonManipulation {
    public Populator.CategorizedPictosList pictos;

    public void crearCat(String filePath, Context context, ArasaacModel.Pictogram p) throws IOException, JSONException {

        AssetManager assman = context.getAssets();
        InputStream istream = null;

        // Crea una nueva instancia de File para el archivo JSON
        istream = assman.open(filePath);

        BufferedReader buffReader = new BufferedReader(new InputStreamReader(istream));
        StringBuilder strBuilder = new StringBuilder();
        String line;
        while ((line = buffReader.readLine()) != null) {
            strBuilder.append(line);
        }
        buffReader.close();

        String jsonStr = strBuilder.toString();

        System.out.println(jsonStr);

        // Convierte la cadena vacía en un objeto JSONArray
        Gson gson = new Gson();
        Populator.CategorizedPictosList pictos = gson.fromJson(jsonStr, Populator.CategorizedPictosList.class);

        System.out.println(pictos.pictoCats.get(pictos.pictoCats.size() - 1).cat);

        Populator.CategorizedPictos new_cat = new Populator.CategorizedPictos(pictos.pictoCats.get(pictos.pictoCats.size() - 1).cat + 1, new int[]{(p.id)}, p.keywords.get(0).keyword, p.id, "");
        System.out.println(new_cat);

        pictos.pictoCats.add(new_cat);
        System.out.println(pictos.pictoCats.get(pictos.pictoCats.size() - 1));

        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(pictos.pictoCats);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }



    public void crearCategoria( ArasaacModel.Pictogram p) throws JSONException {

    }
}
