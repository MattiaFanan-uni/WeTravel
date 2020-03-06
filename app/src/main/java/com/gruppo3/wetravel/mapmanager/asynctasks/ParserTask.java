package com.gruppo3.wetravel.mapmanager.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;

import com.gruppo3.wetravel.mapmanager.DirectionsJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * This {@link AsyncTask} asynchronously parses a string containing json data
 * using the imported {@link DirectionsJSONParser} class.
 * Then sends a list containing parsed data to the delegate method.<br>
 * If an error occurred while parsing, then the list is emptied and returned.
 *
 * @author Giovanni Barca
 */
public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private AsyncResponse delegate; // Method to be invoked when doInBackground is terminated

    /**
     * Instantiate a new ParserTask passing a delegate method to be invoked when {@link #doInBackground(String...)} is terminated.
     *
     * @param delegate Delegated method to be invoked when {@link #doInBackground(String...)} is terminated. Never null.
     */
    public ParserTask(@NonNull AsyncResponse delegate) {
        this.delegate = delegate; // Sends result to the delegate method
    }

    /**
     * Asynchronously parses passed jsonData string using {@link DirectionsJSONParser DirectionsJSONParser}.<br>
     * Data must be in the format given by Google Maps Directions web service.
     *
     * @param jsonData String containing json data (in Google Map Directions web service format) to be parsed. Never null.
     * @return Parsed data. If something went wrong, a null object is returned.
     * @throws IllegalArgumentException If passed an empty url string.
     */
    @Nullable
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(@NonNull String... jsonData) throws IllegalArgumentException {
        Preconditions.checkArgument(jsonData.length > 0, "Parameter string can't be empty");

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]); // Gets downloaded data from method parameters
            DirectionsJSONParser parser = new DirectionsJSONParser(); // Instantiate a new DirectionsJSONParser object

            routes = parser.parse(jObject); // Parses the JSON
        } catch (JSONException e) {
            Log.e("ParserTask", Log.getStackTraceString(e));
            return null;
        }

        return routes;
    }

    /**
     * Passes parsed data to the delegated method.
     *
     * @param result Parsed json data. Will be null if something went wrong.
     */
    @Override
    protected void onPostExecute(@Nullable List<List<HashMap<String, String>>> result) {
        delegate.onFinishResult(result);
    }

    /**
     * Nested interface to be implemented by the calling class to accomplish post-parsing operations in the same calling class.
     */
    public interface AsyncResponse {
        /**
         * Method to be invoked when this AsyncTask is terminated and return the result to.
         *
         * @param result The result from the AsyncTask containing the parsed list. Argument will be null if an error occurred.
         */
        void onFinishResult(@Nullable List<List<HashMap<String, String>>> result);
    }
}
