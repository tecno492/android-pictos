package com.example.pictopocketiv.addCat;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class JsonManipulation {
    public JSONArray pictoCats;

    public void JsonFromFile(String filePath) throws IOException, JSONException {
        // Crea un nuevo objeto JSONArray
        JSONArray jsonArray = null;

        // Crea una nueva instancia de File para el archivo JSON
        File jsonFile = new File(filePath);

        // Crea un nuevo BufferedReader para leer el archivo JSON
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));

        // Crea una cadena vacía para almacenar el contenido del archivo JSON
        StringBuilder stringBuilder = new StringBuilder();

        // Lee cada línea del archivo JSON y la agrega a la cadena vacía
        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = reader.readLine();
        }

        // Cierra el BufferedReader
        reader.close();

        // Convierte la cadena vacía en un objeto JSONArray
        jsonArray = new JSONArray(stringBuilder.toString());

        //devuelve el jsonArray
        this.pictoCats = jsonArray;
    }



    public void crearCategoria(String nombre) throws JSONException {
        JSONObject nuevaCategoria = new JSONObject();
        int ultimoCat = 0;
        if (pictoCats.length() > 0) {
            ultimoCat = pictoCats.getJSONObject(pictoCats.length() - 1).getInt("cat") + 1;
        }
        try {
            nuevaCategoria.put("cat", ultimoCat);
            nuevaCategoria.put("label", nombre);
            nuevaCategoria.put("pictoId", 1234);
            nuevaCategoria.put("drawable", "");
            nuevaCategoria.put("ids", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pictoCats.put(nuevaCategoria);
    }
}
