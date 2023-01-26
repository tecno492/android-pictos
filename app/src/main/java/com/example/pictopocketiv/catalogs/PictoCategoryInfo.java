package com.example.pictopocketiv.catalogs;

public class PictoCategoryInfo {

    /** Attrs **/
    public int category;
    public int count;
    public String label;
    public int pictoId;
    public String drawable;


    /** C **/
    public PictoCategoryInfo(int category, int count, String label,
                             int pictoId, String drawable) {
        this.category = category;
        this.count = count;
        this.label = label;
        this.pictoId = pictoId;
        this.drawable = drawable;
    }
}