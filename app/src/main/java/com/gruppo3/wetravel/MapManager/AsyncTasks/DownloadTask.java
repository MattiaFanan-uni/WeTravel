package com.gruppo3.wetravel.MapManager.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This AsyncTask asynchronously downloads data from a given url and sends that to the delegate method.
 *
 * @author Giovanni Barca
 */
public class DownloadTask extends AsyncTask<String, String, String> {

    public static final String errorMessage = "error";

    /**
     * Nested interface to accomplish post-download operations from calling class
     */
    public interface AsyncResponse {
        void onFinishResult(String result);
    }

    private AsyncResponse delegate = null; // Method to be invoked when doInBackground is terminated

    public DownloadTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... url) {
        String data;

        try {
            data = downloadUrl(url[0]); // Download data from the given url
        } catch (IOException e) {
            Log.e("DownloadTask", Log.getStackTraceString(e));
            data = errorMessage; // Returns an error message if something went wrong
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.onFinishResult(result); // Sends result to the delegate method
    }

    /**
     * Opens a connection to strUrl and returns data downloaded from it.
     * @param strUrl Url to open a connection and download something from
     * @return Downloaded data from the given url
     * @throws IOException If connection or data download can't be done
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection)url.openConnection(); // Opening connection to the url
            urlConnection.connect(); // Connecting to the url
            iStream = urlConnection.getInputStream(); // Opening input stream from the url

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream)); // Opening BufferedReader
            StringBuilder sb = new StringBuilder();

            // Retrieving (one line at a time) data from the url
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.e("downloadUrl", Log.getStackTraceString(e));
            data = errorMessage;
        } finally {
            if (iStream != null)
                iStream.close();

            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return data;
    }
}
