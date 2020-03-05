package com.gruppo3.wetravel.mapManager.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

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

    /**
     * String returned when something in the download phase went wrong.
     */
    public static final String ERROR_MESSAGE = "error";

    private AsyncResponse delegate; // Method to be invoked when doInBackground is terminated

    /**
     * Nested interface to be implemented by the calling class to accomplish post-download operations in the same calling class.
     */
    public interface AsyncResponse {
        /**
         * Method to be invoked when this AsyncTask is terminated and return the result to.
         * @param result The result from the AsyncTask containing downloaded data.
         */
        void onFinishResult(String result);
    }

    /**
     * Instantiate a new DownloadTask passing a delegate method to be invoked when doInBackground() is terminated.
     * @param delegate Delegated method to be invoked when doInBackground is terminated
     */
    public DownloadTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    /**
     * Asynchronously downloads data from a given url and returns it.
     * @param url String containing url of the data to be downloaded. Never null.
     * @return Downloaded data. Never null.
     * @throws RuntimeException If passed an empty url string.
     */
    @NonNull
    @Override
    protected String doInBackground(@NonNull String... url) throws RuntimeException {
        if (url.length == 0)
            throw new RuntimeException("Parameter string can't be empty");

        String data;
        try {
            data = downloadUrl(url[0]); // Download data from the given url
        } catch (IOException e) {
            Log.e("DownloadTask", Log.getStackTraceString(e));
            data = ERROR_MESSAGE; // Returns an error message if something went wrong
        }

        return data;
    }

    /**
     * Passes downloaded data to the delegated method.
     * @param result Downloaded data
     */
    @Override
    protected void onPostExecute(String result) {
        delegate.onFinishResult(result); // Sends result to the delegate method
    }

    /**
     * Opens a connection to strUrl parameter and returns data downloaded from it.
     * @param strUrl Url to open a connection and download data from.
     * @return Downloaded data from the given url or {@link #ERROR_MESSAGE} if something went wrong.
     * @throws IOException If connection or data download can't be established.
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data;
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
            // Something went wrong... setting data to an error message.
            Log.e("downloadUrl", Log.getStackTraceString(e));
            data = ERROR_MESSAGE;
        } finally {
            // Closing all streams and connections even if something went wrong
            if (iStream != null)
                iStream.close();

            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return data;
    }
}
