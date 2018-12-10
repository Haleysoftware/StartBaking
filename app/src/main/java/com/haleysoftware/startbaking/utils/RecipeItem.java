package com.haleysoftware.startbaking.utils;

/**
 * The object to hold Recipe data.
 * <p>
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

    /**
     * Creates the Recipe Item Object.
     *
     * @param id               The Recipe ID.
     * @param name             The name of the Recipe.
     * @param ingredientString The string of ingredients used for the first recipe step.
     * @param ingredientJson   The JSON String of ingredients used in the Widget.
     * @param stepJson         The JSON String used for the Step list activity.
     * @param serve            The number of servings this recipe makes.
     * @param image            The image URL for the main recipe list.
     */
    RecipeItem(int id, String name, String ingredientString, String ingredientJson,
               String stepJson, int serve, String image) {
        this.id = id;
        this.name = name;
        this.ingredientString = ingredientString;
        this.ingredientJson = ingredientJson;
        this.stepJson = stepJson;
        this.serve = serve;
        this.image = image;
    }

    /**
     * Returns the ID of the recipe.
     *
     * @return The recipe's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the recipe.
     *
     * @return The recipe's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the string of Ingredients of the recipe.
     * Used for the first step of the recipe.
     *
     * @return The recipe's ingredients in string form.
     */
    public String getIngredientString() {
        return ingredientString;
    }

    /**
     * Returns the JSON string of Ingredients of the recipe.
     * Used for the widget.
     *
     * @return The recipe's ingredients in JSON string form.
     */
    public String getIngredientJson() {
        return ingredientJson;
    }

    /**
     * Returns the JSON string of Steps of the recipe.
     *
     * @return The recipe's steps in JSON string form.
     */
    public String getStepJson() {
        return stepJson;
    }

    /**
     * Returns the serving number of the recipe.
     *
     * @return The recipe's number of servings.
     */
    public int getServe() {
        return serve;
    }

    /**
     * Returns the URL for an image used by the recipe.
     *
     * @return The recipe's image URL string.
     */
    public String getImage() {
        return image;
    }
}