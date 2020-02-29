package com.gruppo3.wetravel.MapManager.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.gruppo3.wetravel.MapManager.DirectionsJSONParser;

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

    /**
     * Nested interface to accomplish post-download operations from calling class
     */
    public interface AsyncResponse {
        void onFinishResult(List<List<HashMap<String, String>>> result);
    }

    private AsyncResponse delegate = null; // Method to be invoked when doInBackground is terminated

    public ParserTask(AsyncResponse delegate) {
        this.delegate = delegate; // Sends result to the delegate method
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
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

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        Log.e("Attenzione", "onPostExecute()");
        delegate.onFinishResult(result);
    }
}
