package com.haleysoftware.startbaking.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by haleysoft on 11/12/18.
 */
public class NetworkHelper {

    private static final String URI_PATH =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    /**
     * A check if the device has internet.
     *
     * @param context The activity context.
     * @return A Boolean if the device has internet or not.
     */
    public static boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork;
        try {
            activeNetwork = cm.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     *
     * @return
     */
    static URL urlBuilder() {
        Uri builtUri = Uri.parse(URI_PATH).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    static String internetResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }
}
