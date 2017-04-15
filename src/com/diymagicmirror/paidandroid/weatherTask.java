package com.diymagicmirror.paidandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public class weatherTask extends AsyncTask<Void, Void, String> {
	
	
	/*public interface AsyncResponse {
        void processFinish(String output);
  }
	
	public AsyncResponse delegate = null;

    public weatherTask(AsyncResponse delegate){
        this.delegate = delegate;
    }*/
    
    String cityName;
    TextView tvResult;
    String weatherIcon;

    String dummyAppid = "e7fc61dd49597f0cac7871393e8cd971";
    String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=";
    String queryDummyKey = "&appid=" + dummyAppid;

    weatherTask(String cityName, TextView tvResult){
        this.cityName = cityName;
        this.tvResult = tvResult;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        String queryReturn;

        String query = null;
        try {
            query = queryWeather + URLEncoder.encode(cityName, "UTF-8") + queryDummyKey;
            queryReturn = sendQuery(query);
            result += ParseJSON(queryReturn);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            queryReturn = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            queryReturn = e.getMessage();
        }


        final String finalQueryReturn = query + "\n\n" + queryReturn;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //textViewInfo.setText(finalQueryReturn);
                //textViewInfo.setText(finalQueryReturn);
            }
        });


        return result;
    }

    private void runOnUiThread(Runnable runnable) {
		// TODO Auto-generated method stub
		
	}

	@Override
    //protected void onPostExecute(String s, String weatherIcon) {
	protected void onPostExecute(String s) {
       tvResult.setText(s);
    //delegate.processFinish(s);
        
    }

    private String sendQuery(String query) throws IOException {
        String result = "";

        URL searchURL = new URL(query);

        HttpURLConnection httpURLConnection = (HttpURLConnection)searchURL.openConnection();
        if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader,
                    8192);

            String line = null;
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
        }

        return result;
    }

    private String ParseJSON(String json){
        String jsonResult = "";

        try {
            JSONObject JsonObject = new JSONObject(json);
            String cod = jsonHelperGetString(JsonObject, "cod");

            if(cod != null){
                if(cod.equals("200")){

                   /* jsonResult += jsonHelperGetString(JsonObject, "name") + "\n";
                    JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
                    if(sys != null){
                        jsonResult += jsonHelperGetString(sys, "country") + "\n";
                    }
                    jsonResult += "\n";

                    JSONObject coord = jsonHelperGetJSONObject(JsonObject, "coord");
                    if(coord != null){
                        String lon = jsonHelperGetString(coord, "lon");
                        String lat = jsonHelperGetString(coord, "lat");
                        jsonResult += "lon: " + lon + "\n";
                        jsonResult += "lat: " + lat + "\n";
                    }
                    jsonResult += "\n";*/

                    /*JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                    if(weather != null){
                        for(int i=0; i<weather.length(); i++){
                            JSONObject thisWeather = weather.getJSONObject(i);
                            jsonResult += "weather " + i + ":\n";
                            jsonResult += "id: " + jsonHelperGetString(thisWeather, "id") + "\n";
                            jsonResult += jsonHelperGetString(thisWeather, "main") + "\n";
                            jsonResult += jsonHelperGetString(thisWeather, "description") + "\n";
                            jsonResult += jsonHelperGetString(thisWeather, "icon") + "\n";
                            jsonResult += "\n";
                        }
                    }*/
                    
                	  JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                      if(weather != null){
                          for(int i=0; i<weather.length(); i++){
                              JSONObject thisWeather = weather.getJSONObject(i);
                              jsonResult += jsonHelperGetString(thisWeather, "icon");
                              weatherIcon = jsonHelperGetString(thisWeather, "icon");
                             
                          }
                      }
                	
                	/*JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                    if(weather != null){
                        for(int i=0; i<weather.length(); i++){
                            JSONObject thisWeather = weather.getJSONObject(i);
                            jsonResult += jsonHelperGetString(thisWeather, "icon") + "\n";
                            jsonResult += "\n";
                            weatherIcon = jsonHelperGetString(thisWeather, "icon");
                           
                        }
                    }*/

                  /*  JSONObject main = jsonHelperGetJSONObject(JsonObject, "main");
                    if(main != null){
                        jsonResult += "temp: " + jsonHelperGetString(main, "temp") + "\n";
                        jsonResult += "pressure: " + jsonHelperGetString(main, "pressure") + "\n";
                        jsonResult += "humidity: " + jsonHelperGetString(main, "humidity") + "\n";
                        jsonResult += "temp_min: " + jsonHelperGetString(main, "temp_min") + "\n";
                        jsonResult += "temp_max: " + jsonHelperGetString(main, "temp_max") + "\n";
                        jsonResult += "sea_level: " + jsonHelperGetString(main, "sea_level") + "\n";
                        jsonResult += "grnd_level: " + jsonHelperGetString(main, "grnd_level") + "\n";
                        jsonResult += "\n";
                    }

                    jsonResult += "visibility: " + jsonHelperGetString(JsonObject, "visibility") + "\n";
                    jsonResult += "\n";

                    JSONObject wind = jsonHelperGetJSONObject(JsonObject, "wind");
                    if(wind != null){
                        jsonResult += "wind:\n";
                        jsonResult += "speed: " + jsonHelperGetString(wind, "speed") + "\n";
                        jsonResult += "deg: " + jsonHelperGetString(wind, "deg") + "\n";
                        jsonResult += "\n";
                    }*/

                    //...incompleted

                }else if(cod.equals("404")){
                    String message = jsonHelperGetString(JsonObject, "message");
                    jsonResult += "cod 404: " + message;
                }
            }else{
                jsonResult += "cod == null\n";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            jsonResult += e.getMessage();
        }

        return jsonResult;
    }

    private String jsonHelperGetString(JSONObject obj, String k){
        String v = null;
        try {
            v = obj.getString(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k){
        JSONObject o = null;

        try {
            o = obj.getJSONObject(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k){
        JSONArray a = null;

        try {
            a = obj.getJSONArray(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return a;
    }
}