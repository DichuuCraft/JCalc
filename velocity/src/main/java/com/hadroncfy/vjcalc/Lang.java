package com.hadroncfy.vjcalc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Lang {
    private Map<String, String> selected;
    private String lang;
    private static final Gson GSON = new Gson();

    public void load(String lang) throws IOException {
        if (!lang.equals(this.lang)){
            try (Reader reader = new InputStreamReader(Lang.class.getClassLoader().getResourceAsStream(String.format("lang/%s.json", lang)), StandardCharsets.UTF_8)){
                selected = GSON.fromJson(reader, HashMap.class);
                this.lang = lang;
            }
        }
    }

    public String getString(String name){
        if (selected != null){
            String val = selected.get(name);
            return val == null ? name : val;
        }
        return name;
    }
}