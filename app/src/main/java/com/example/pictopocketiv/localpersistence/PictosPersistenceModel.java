package com.example.pictopocketiv.localpersistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import com.example.pictopocketiv.arasaac.ArasaacModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PictosPersistenceModel {

    /** Entities **/
    @Entity(tableName = "keywords")
    public final static class Keyword {
        @PrimaryKey
        @NonNull
        public String keyword;
        public String meaning;
        public String locale;


        public Keyword(@NonNull String keyword, String meaning, String locale) {
            this.keyword = keyword;
            this.meaning = meaning;
            this.locale = locale;
        }
    }

    @Entity(tableName = "pictos")
    public final static class Picto {
        @PrimaryKey
        @NonNull
        public int id;
        public int category;

        public Picto(int id, int category) {
            this.id = id;
            this.category = category;
        }
    }

    @Entity(tableName = "pictoskw", primaryKeys = {"picto", "keyword"})
    public final static class PictoKeyword {

        @ColumnInfo(name = "picto")
        @NonNull
        public int picto;

        @ColumnInfo(name = "keyword")
        @NonNull
        public String keyword;

        public PictoKeyword(int picto, String keyword) {
            this.picto = picto;
            this.keyword = keyword;
        }
    }

    @Entity(tableName = "dbupdates")
    public final static class DBUpdate {
        @NonNull
        @PrimaryKey
        long date = new Date().getTime();

        public DBUpdate(@NonNull long date) {
            this.date = date;
        }
    }

    @Entity(tableName = "categoriesinfo")
    public final static class PictoCategoryInfo {

        // Attrs
        @NonNull
        @PrimaryKey
        public
        int id;
        @NonNull
        String label;
        int picto;
        String drawable;

        //
        public int getId() {
            return id;
        }

        @NonNull
        public String getLabel() {
            return label;
        }

        public int getPicto() {
            return picto;
        }

        public String getDrawable() {
            return drawable;
        }




        public PictoCategoryInfo(int id, @NonNull String label, int picto, String drawable) {
            this.id = id;
            this.label = label;
            this.picto = picto;
            this.drawable = drawable;
        }
    }


    /** DAO **/
    @androidx.room.Dao
    public interface Dao {

        /** Pictos **/
        @Query("SELECT * FROM pictos")
        List<Picto> getAllPictos();

        @Query("SELECT * FROM pictos WHERE category = :category")
        List<Picto> getPictosByCategory(int category);

        @Query("SELECT COUNT(*) FROM pictos WHERE category = :category")
        int getPictosByCategoryCount(int category);

        @Query("SELECT COUNT(id) FROM categoriesinfo")
        int getCategoryCount();

        @Query("SELECT * FROM pictos WHERE id = :id")
        Picto getPictoById(int id);

        @Insert
        void addPicto(Picto picto); // inserts one picto

        @Insert
        void addPictos(Picto[] pictos); // inserts some pictos


        /** keywords **/
        @Query("SELECT * FROM keywords WHERE keyword = :keyword")
        Keyword getKeyword(String keyword);

        @Insert
        void addKeyword(Keyword keyword); // inserts one keyword

        /** Pictos - Keywords **/
        @Query("SELECT * FROM pictoskw")
        List<PictoKeyword> getPictosKewords();

        @Query("SELECT * FROM pictoskw WHERE picto = :pictoId")
        List<PictoKeyword> getPictosKewords(int pictoId);

        @Insert
        void addPictoKeyword(PictoKeyword keyword); // inserts one keyword


        /** Categories **/
        @Query("SELECT * FROM categoriesinfo ORDER BY id")
        List<PictoCategoryInfo> getCategoriesInfo();

        @Insert
        void addCategoryInfo(PictoCategoryInfo categoryInfo);


        /** Update **/
        @Query("SELECT * FROM dbupdates")
        List<DBUpdate> getUpdates();

        @Insert
        void addUpdate(DBUpdate dbUpdate);  // inserts an update

    }


    /** Database **/
    @Database(version = 1, entities = {
            Keyword.class,
            Picto.class,
            PictoKeyword.class,
            DBUpdate.class,
            PictoCategoryInfo.class
    })
    public static abstract class PictosDB extends RoomDatabase {
        public abstract Dao dao();
    }


    /** Adapters **/
    public static class ArasaacAdapter {
        public static Picto adapt(ArasaacModel.Pictogram pictogram, int category) {

            if(pictogram != null ) {
                return new Picto(pictogram.id, category );
            }

            return null;
        }

        public static List<Keyword> adaptKeywords(ArasaacModel.Pictogram pictogram) {

            List<Keyword> ls = new ArrayList<>();

            for (ArasaacModel.Keyword keyword : pictogram.keywords) {

                PictosPersistenceModel.Keyword kw = new Keyword(keyword.keyword, keyword.meaning, "es");
                ls.add(kw);
            }


            return ls;
        }

        public static List<PictoKeyword> adaptPictosKws(Picto picto, List<Keyword> keywords) {
            List<PictoKeyword> ls = new ArrayList<>();

            for (Keyword keyword : keywords) {

                PictoKeyword pk = new PictoKeyword(picto.id,keyword.keyword);
                ls.add(pk);
            }

            return ls;
        }
    }
}

