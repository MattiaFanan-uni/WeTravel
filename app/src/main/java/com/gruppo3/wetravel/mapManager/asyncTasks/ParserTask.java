package com.gruppo3.wetravel.mapManager.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gruppo3.wetravel.mapManager.DirectionsJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * This AsyncTask asynchronously parses the downloaded data (should be in JSON format)
 * using the imported DirectionsJSONParser class and sends that to the delegate method.
 *
 * @author Giovanni Barca
 */
public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private AsyncResponse delegate; // Method to be invoked when doInBackground is terminated

    /**
     * Nested interface to be implemented by the calling class to accomplish post-parsing operations in the same calling class.
     */
    public interface AsyncResponse {
        /**
         * Method to be invoked when this AsyncTask is terminated and return the result to.
         * @param result The result from the AsyncTask containing the parsed list.
         */
        void onFinishResult(List<List<HashMap<String, String>>> result);
    }

    /**
     * Instantiate a new ParserTask passing a delegate method to be invoked when doInBackground() is terminated.
     * @param delegate Delegated method to be invoked when doInBackground is terminated
     */
    public ParserTask(AsyncResponse delegate) {
        this.delegate = delegate; // Sends result to the delegate method
    }

    /**
     * Asynchronously parses passed jsonData using {@link DirectionsJSONParser DirectionsJSONParser}.<br>
     * Data must be in the format given by Google Maps Directions web service.
     * @param jsonData String containing json data (in Google Map Directions web service format) to be parsed. Never null.
     * @return Parsed data. If something went wrong, an empty list is returned. Never null.
     * @throws RuntimeException If passed an empty url string.
     */
    @NonNull
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(@NonNull String... jsonData) throws RuntimeException {
        if (jsonData.length == 0)
            throw new RuntimeException("Parameter string can't be empty.");
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]); // Gets downloaded data from method parameters
            DirectionsJSONParser parser = new DirectionsJSONParser(); // Instantiate a new DirectionsJSONParser object

            routes = parser.parse(jObject); // Parses the JSON
        } catch (JSONException e) {
            Log.e("ParserTask", Log.getStackTraceString(e));
            routes.clear(); // Clearing the List to avoid displaying inconsistent data
        }

        return routes;
    }

    /**
     * Passes parsed data to the delegated method.
     * @param result Parsed json data
     */
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        delegate.onFinishResult(result);
    }
}
