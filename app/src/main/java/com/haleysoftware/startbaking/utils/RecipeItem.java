package com.haleysoftware.startbaking.utils;

/**
 * Created by haleysoft on 11/12/18.
 */
public class RecipeItem {

    private int id;
    private String name;
    private String ingredientString;
    private String ingredientJson;
    private String stepJson;
    private int serve;
    private String image;


    public RecipeItem(int id, String name, String ingredientString, String ingredientJson,
                      String stepJson, int serve, String image){
        this.id = id;
        this.name = name;
        this.ingredientString = ingredientString;
        this.ingredientJson = ingredientJson;
        this.stepJson = stepJson;
        this.serve = serve;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIngredientString() {
        return ingredientString;
    }

    public String getIngredientJson() {
        return ingredientJson;
    }

    public String getStepJson() {
        return stepJson;
    }

    public int getServe() {
        return serve;
    }

    public String getImage() {
        return image;
    }
}
