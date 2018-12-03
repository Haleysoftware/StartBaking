package com.haleysoftware.startbaking.utils;

import android.arch.lifecycle.LiveData;

import com.haleysoftware.startbaking.widget.IngredientItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haleysoft on 11/12/18.
 */
final class ReadJson {

    static List<RecipeItem> getRecipesFromJson(String jsonString) throws JSONException {

        final String MAIN_ID = "id"; //int
        final String MAIN_NAME = "name"; //String

        final String MAIN_MAKES = "ingredients"; //JSON Array

        final String MAKES_QUANTITY = "quantity"; //int
        final String MAKES_MEASURE = "measure"; //String
        final String MAKES_INGREDIENT = "ingredient"; //String

        final String MAIN_STEPS = "steps"; //JSON Array to String

        final String MAIN_SERVINGS = "servings"; //int
        final String MAIN_IMAGE = "image"; //String

        JSONArray results = new JSONArray(jsonString);
        int recipeLength = results.length();
        ArrayList<RecipeItem> parsedRecipes = new ArrayList<>(recipeLength);
        for (int i = 0; i<recipeLength; i++) {
            JSONObject recipe = results.getJSONObject(i);

            int mainId = recipe.getInt(MAIN_ID);
            String mainName = recipe.getString(MAIN_NAME);

            JSONArray mainMakes = recipe.getJSONArray(MAIN_MAKES);
            int makesLength = mainMakes.length();
            StringBuilder makesBuilder = new StringBuilder();
            for (int j = 0; j<makesLength; j++) {
                JSONObject make = mainMakes.getJSONObject(j);

                int makeQuantity = make.getInt(MAKES_QUANTITY);
                String makeMeasure = make.getString(MAKES_MEASURE);
                String makeIngredient = make.getString(MAKES_INGREDIENT);

                makesBuilder.append(makeQuantity).append(makeMeasure).append(" ")
                        .append(makeIngredient);

                if (j < makesLength-1) {
                    makesBuilder.append("\n");
                }
            }
            String makesList = makesBuilder.toString();

            String makesJson = mainMakes.toString();
            String mainSteps = recipe.getJSONArray(MAIN_STEPS).toString();
            int mainServings = recipe.getInt(MAIN_SERVINGS);
            String mainImage = recipe.getString(MAIN_IMAGE);

            parsedRecipes.add(new RecipeItem(mainId, mainName, makesList, makesJson, mainSteps,
                    mainServings, mainImage));
        }
        return parsedRecipes;
    }

    static List<StepItem> getStepsFromJson(String jsonString) throws JSONException {
        final String STEPS_ID = "id"; //int
        final String STEPS_TITLE = "shortDescription"; //String
        final String STEPS_DESCRIPTION = "description"; //String
        final String STEPS_VIDEO = "videoURL"; //String
        final String STEPS_IMAGE = "thumbnailURL"; //String

        JSONArray stepArray = new JSONArray(jsonString);
        int stepsLength = stepArray.length();
        ArrayList<StepItem> parsedSteps = new ArrayList<>(stepsLength);
        for (int i = 0; i<stepsLength; i++) {
            JSONObject step = stepArray.getJSONObject(i);

            int id = step.getInt(STEPS_ID);
            String title = step.getString(STEPS_TITLE);
            String description = step.getString(STEPS_DESCRIPTION);
            String video = step.getString(STEPS_VIDEO);
            String image = step.getString(STEPS_IMAGE);

            parsedSteps.add(new StepItem(id, title, description, video, image));
        }
        return parsedSteps;
    }


}
