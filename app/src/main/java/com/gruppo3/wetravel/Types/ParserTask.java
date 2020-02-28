package com.gruppo3.wetravel.Types;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Giovanni Barca
 */
public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    public interface AsyncResponse {
        void onFinishResult(List<List<HashMap<String, String>>> result);
    }

    private AsyncResponse delegate = null;

    public ParserTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            routes = parser.parse(jObject);
        } catch (JSONException e) {
            Log.e("ParserTask", Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        Log.e("Attenzione", "onPostExecute()");
        delegate.onFinishResult(result);
    }
}
