package mindvalley.com.navigation;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by anand_android on 10/5/2016.
 */
public class PlacesAutoCompleteAdapter
        extends ArrayAdapter<String> implements Filterable

{

    private static final String LOG_TAG = "PlacesAutoCompleteAdapter";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    //------------ make your specific key ------------
    private static final String API_KEY = "AIzaSyAdo2xMm3GJiKw5b4JLpj6HCZcif5kUrso";
  /* private static final String API_KEY = "AIzaSyDgKmP453PoZJEUstfn8wpAtw-e-mS0OYA";*/

    static String location = "13.036485,80.156816";//chennai location
    private ArrayList<String> resultList;

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId, String latLong) {
        super(context, textViewResourceId);
        location = latLong;
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
          /*  sb.append("&types=(cities)");*/
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&location=" + URLEncoder.encode(location, "utf8"));
            sb.append("&radius=" + URLEncoder.encode("50000", "utf8"));

            URL url = new URL(sb.toString());
            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                String city = predsJsonArray.getJSONObject(i).getString("description");
                //String curtArray[] = city.split(",");
                resultList.add(city);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public boolean checkValidLocation(String loc) {
        if (resultList != null) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).equals(loc)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

}
