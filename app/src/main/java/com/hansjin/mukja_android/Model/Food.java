package com.hansjin.mukja_android.Model;

import java.util.List;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public class Food {
    public String _id;
    public String name;
    public List<String> taste;
    public List<String> country;
    public List<String> cooking;

    public List<String> getIngredient() {
        return ingredient;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTaste() {
        return taste;
    }

    public List<String> getCountry() {
        return country;
    }

    public List<String> getCooking() {
        return cooking;
    }

    public List<String> ingredient;

}
