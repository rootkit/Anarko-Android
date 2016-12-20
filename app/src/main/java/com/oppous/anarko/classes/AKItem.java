package com.oppous.anarko.classes;

import org.json.JSONArray;

public class AKItem {
    public String idx;
    public String name;
    public String description;
    public String type;
    public double price;
    public int isPrivate=0;
    public String creator;
    public int minVersionReq=1;
    public int __v=0;
    public int active=0;
    public JSONArray thumbs;
    public JSONArray packages;
    public int isFree=0;
}