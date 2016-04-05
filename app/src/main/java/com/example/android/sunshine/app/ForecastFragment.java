package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 31-03-2016.
 */

public  class ForecastFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment,menu);
    }


    ArrayAdapter<String> mArrayAdapter;
    private ListView listView;
    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] fakedata = {"Monday-Sunny-82/54", "Today-Rainy-34/65", "Wednesday-Windy-50/60", "Tuesday-Awesome-67/70"};
        List<String> adapter = new ArrayList<String>(Arrays.asList(fakedata));
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, adapter);
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mArrayAdapter);
        return rootView;
    }

class FetchWeatherTask extends AsyncTask<String,Void,Void>{
    private  final String LOG_TAG=FetchWeatherTask.class.getSimpleName();
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

        // Will contain the raw JSON response as a string.

    @Override
    protected Void doInBackground(String... params) {
        String forecastJsonStr = null;

                    String format = "json";
                    String units = "metric";
                    int numDays = 7;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
             URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                            final String QUERY_PARAM = "q";
                            final String FORMAT_PARAM = "mode";
                            final String UNITS_PARAM = "units";
                            final String DAYS_PARAM = "cnt";
                            final String APPID_PARAM = "APPID";

                                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                                            .appendQueryParameter(QUERY_PARAM, params[0])
                                            .appendQueryParameter(FORMAT_PARAM, format)
                                            .appendQueryParameter(UNITS_PARAM, units)
                                            .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                                            .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                                           .build();

                                   url = new URL(builtUri.toString());

                                   Log.v(LOG_TAG, "Built URI " + builtUri.toString());


            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return null;
    }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_refresh) {
           FetchWeatherTask fetchWeatherTask= new FetchWeatherTask();
            fetchWeatherTask.execute("333029");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}