package com.haleysoftware.startbaking.widget;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An object class that is used to hold ingredient items for the widget.
 * This object is parcelable.
 *
 * Created by haleysoft on 11/27/18.
 */
public class IngredientItem implements Parcelable {

    private int amount;
    private String measure;
    private String item;

    /**
     * Creates a single or list of ingredients from a parcel.
     */
    public static final Creator<IngredientItem> CREATOR = new Creator<IngredientItem>() {
        @Override
        public IngredientItem createFromParcel(Parcel in) {
            return new IngredientItem(in);
        }

        @Override
        public IngredientItem[] newArray(int size) {
            return new IngredientItem[size];
        }
    };

    /**
     * Creates a new ingredient item.
     *
     * @param amount  The amount needed for the ingredient.
     * @param measure The unit of measure for the ingredient.
     * @param item    The ingredient name.
     */
    IngredientItem(int amount, String measure, String item) {
        this.amount = amount;
        this.measure = measure;
        this.item = item;
    }

    /**
     * Takes data from a parcel object and creates an ingredient object.
     *
     * @param in The passed parcel object.
     */
    private IngredientItem(Parcel in) {
        this.amount = in.readInt();
        this.measure = in.readString();
        this.item = in.readString();
    }

    /**
     * Gets the ingredient amount.
     *
     * @return The ingredient amount.
     */
    int getAmount() {
        return amount;
    }

    //************************* Parcelable code *************************

    /**
     * Gets the unit of measurement.
     *
     * @return The unit of measurement.
     */
    String getMeasure() {
        return measure;
    }

    /**
     * Gets the name of the ingredient.
     *
     * @return The ingredient's name.
     */
    public String getItem() {
        return item;
    }

    /**
     * This is not used.
     *
     * @return Returns 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Turns this object into a parcel object.
     *
     * @param dest The parcel to load with data.
     * @param flags Flags used by the system. Not used.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.amount);
        dest.writeString(this.measure);
        dest.writeString(this.item);
    }
}