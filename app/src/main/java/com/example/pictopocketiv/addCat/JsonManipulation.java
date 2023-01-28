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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class JsonManipulation {
    public Populator.CategorizedPictosList pictoCats;

    public void JsonFromFile(String filePath, Context context) throws IOException, JSONException {

        AssetManager assman = context.getAssets();
        InputStream istream = null;

        // Crea una nueva instancia de File para el archivo JSON
        istream = assman.open("welcome_pictos_bundle.json");

        BufferedReader buffReader = new BufferedReader(new InputStreamReader(istream));
        StringBuilder strBuilder = new StringBuilder();
        String line;
        while ((line = buffReader.readLine()) != null ) {
            strBuilder.append(line);
        }

        String jsonStr = strBuilder.toString();

        System.out.println(jsonStr);

        // Convierte la cadena vacía en un objeto JSONArray
        Gson gson = new Gson();
        Populator.CategorizedPictosList pictos = gson.fromJson(jsonStr, Populator.CategorizedPictosList.class);


        this.pictoCats = pictos;
    }


/*
    public void crearCategoria( ArasaacModel.Pictogram p) throws JSONException {
        JSONObject nuevaCategoria = new JSONObject();
        int ultimoCat = 0;
        if (pictoCats.length() > 0) {
            ultimoCat = pictoCats.getJSONObject(pictoCats.length() - 1).getInt("cat") +1;
        }
        try {
            nuevaCategoria.put("cat", ultimoCat);
            nuevaCategoria.put("label", p.keywords);
            nuevaCategoria.put("pictoId", p.id);
            nuevaCategoria.put("drawable", "");
            nuevaCategoria.put("ids", new JSONArray(p.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pictoCats.put(nuevaCategoria);
    }*/
}
