package com.haleysoftware.startbaking.widget;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haleysoft on 11/27/18.
 */
public class IngredientItem implements Parcelable {

    private int amount;
    private String measure;
    private String item;

    public IngredientItem(int amount, String measure, String item) {
        this.amount = amount;
        this.measure = measure;
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public String getMeasure() {
        return measure;
    }

    public String getItem() {
        return item;
    }

    //************************* Parcelable code *************************

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

    @Override
    public int describeContents() {
        return 0;
    }

    private IngredientItem(Parcel in) {
        this.amount = in.readInt();
        this.measure = in.readString();
        this.item = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.amount);
        dest.writeString(this.measure);
        dest.writeString(this.item);
    }
}
