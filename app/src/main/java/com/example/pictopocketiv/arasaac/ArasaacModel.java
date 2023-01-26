package com.example.pictopocketiv.arasaac;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ArasaacModel {

    public class Pictogram implements Serializable {

        @SerializedName("_id")
        public int id;

        @SerializedName("keywords")
        public List<Keyword> keywords;

        @SerializedName("schematic")
        public boolean schematic;

        @SerializedName("sex")
        public boolean sex;

        @SerializedName("violence")
        public boolean violence;

        @SerializedName("categories")
        public List<String> categories;

        @SerializedName("synsets")
        public List<String> synsets;

        @SerializedName("tags")
        public List<String> tags;

        @SerializedName("desc")
        public String desc;

        @Override
        public String toString() {
            return "Pictogram{" +
                    "id=" + id +
                    ", keywords=" + keywords +
                    ", schematic=" + schematic +
                    ", sex=" + sex +
                    ", violence=" + violence +
                    ", categories=" + categories +
                    ", synsets=" + synsets +
                    ", tags=" + tags +
                    ", desc=" + desc +
                    '}';
        }
    }

    public class Keyword implements Serializable {
        @SerializedName("idKeyword")
        public String id;

        @SerializedName("keyword")
        public String keyword;

        @SerializedName("idLocution")
        public String idLocution;

        @SerializedName("meaning")
        public String meaning;

        @SerializedName("type")
        public int type;

        @SerializedName("lse")
        public int lse;

        @Override
        public String toString() {
            return "Keyword{" +
                    "id='" + id + '\'' +
                    ", keyword='" + keyword + '\'' +
                    ", idLocution='" + idLocution + '\'' +
                    ", meaning='" + meaning + '\'' +
                    ", type=" + type +
                    ", lse=" + lse +
                    '}';
        }
    }
}
