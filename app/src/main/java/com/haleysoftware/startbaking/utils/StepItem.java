package com.haleysoftware.startbaking.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A step item to hold the data for each step in a recipe.
 * This item is parcelable and can be passed by intent.
 *
 * Created by haleysoft on 11/12/18.
 */
public class StepItem implements Parcelable {

    public static final String RECIPE_NAME_EXTRA = "recipeName";
    public static final String RECIPE_ING_EXTRA = "tecipeIng";
    public static final String STEP_ID_EXTRA = "stepId";
    public static final String STEP_ITEM_EXTRA = "stepList";

    private int id;
    private String title;
    private String description;
    private String video;
    private String image;

    /**
     * Creates the step item
     *
     * @param id The ID number of the step. Not used but here if needed.
     * @param title The title of the step. Used for the step list.
     * @param description The body or instructions of the step.
     * @param video URL address for the step video.
     * @param image URL address for the step image.
     */
    public StepItem(int id, String title, String description, String video, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.video = video;
        this.image = image;
    }

    /**
     * 
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return
     */
    public String getVideo() {
        return video;
    }

    /**
     *
     * @return
     */
    public String getImage() {
        return image;
    }

    // ************************* Parcelable contents *************************

    /**
     *
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public StepItem createFromParcel(Parcel in) {
            return new StepItem(in);
        }

        public StepItem[] newArray(int size) {
            return new StepItem[size];
        }
    };

    /**
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *
     * @param in
     */
    private StepItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.video = in.readString();
        this.image = in.readString();
    }

    /**
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.video);
        dest.writeString(this.image);
    }
}
