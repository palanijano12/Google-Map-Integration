package mindvalley.com.navigation;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    GoogleMap mGoogleMap;
    String TAG = "MapActivity";
    private HashMap<Polyline, Integer> mHashMap = new HashMap<>();
    List<LatLng> lstDecodedPolygon;
    String mVia;
    String mOverViewPolygon;
    List<Double> mFromLatLong;
    List<Double> mToLatLong;
    List<LatLng> listPoints;
    Set<String> cityList = new HashSet<>();
    int flagPolyLineSelectColor = 0;
    JSONArray routeArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;


        mGoogleMap.setOnPolylineClickListener(this);

        Bundle mBundle = getIntent().getExtras();
        String route = mBundle.getString("route");
        Log.e("MapActivity", route);
        setMapData(route);


    }

    private void setMapData(String route) {
        try {
            final JSONObject json = new JSONObject(route);

            routeArray = json.getJSONArray("routes");
            createPolyLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPolyLine() {
        mGoogleMap.clear();
        mHashMap.clear();
        listPoints = new ArrayList<>();
        for (int i = routeArray.length() - 1; i >= 0; i--) {
            JSONObject routes = null;
            try {
                routes = routeArray.getJSONObject(i);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                lstDecodedPolygon = decodePoly(encodedString);
                android.util.Log.e(TAG, "overviewPolylines size: " + lstDecodedPolygon.size());
                if (i == 0) {
                    mVia = routes.optString("summary");
                    mOverViewPolygon = encodedString;
                }
                Polyline line = mGoogleMap.addPolyline(new PolylineOptions()
                        .addAll(lstDecodedPolygon)
                        .width(12)
                        .color(getClr(i))//Google maps blue color
                        .geodesic(true)
                );
                mGoogleMap.addMarker(new MarkerOptions().position(lstDecodedPolygon.get(0)));
                mGoogleMap.addMarker(new MarkerOptions().position(lstDecodedPolygon.get(lstDecodedPolygon.size() - 1)));
                line.setClickable(true);
                mHashMap.put(line, i);
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(lstDecodedPolygon.get(0));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);

                mGoogleMap.moveCamera(center);
                mGoogleMap.animateCamera(zoom);
                if (i == 0) {
                    listPoints.add(lstDecodedPolygon.get(0));
                    JSONArray legs = routes.getJSONArray("legs");

                    for (int j = 0; j < legs.length(); j++) {

                        JSONObject legsObject = legs.getJSONObject(j);
                        mFromLatLong = new ArrayList<>();
                        mToLatLong = new ArrayList<>();

                        mFromLatLong.add(legsObject.optJSONObject("start_location").optDouble("lat"));
                        mFromLatLong.add(legsObject.optJSONObject("start_location").optDouble("lng"));
                        Log.e(TAG, "mFrom: " + mFromLatLong.toString());
                        mToLatLong.add(legsObject.optJSONObject("end_location").optDouble("lat"));
                        mToLatLong.add(legsObject.optJSONObject("end_location").optDouble("lng"));
                        Log.e(TAG, "mFrom: " + mToLatLong.toString());

                        JSONArray steps = legsObject.getJSONArray("steps");

                        for (int k = 0; k < steps.length(); k++) {
                            JSONObject stepsObject = steps.getJSONObject(k);
                            JSONObject endLocation = stepsObject.getJSONObject("end_location");
                            //  JSONObject endLocationObject=    endLocation.getJSONObject(0);
                            LatLng a = new LatLng(endLocation.optDouble("lat"), endLocation.optDouble("lng"));
                            android.util.Log.e(TAG, "lat : " + a.latitude + " long : " + a.longitude);

                            listPoints.add(a);
                        }

                    }

                }


                if (routeArray != null && routeArray.length() > 0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(lstDecodedPolygon.get(0));
                    builder.include(lstDecodedPolygon.get(lstDecodedPolygon.size() - 1));


                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                    mGoogleMap.animateCamera(cu);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    int getClr(int i) {
        if (i == 0) {
            return Color.parseColor("#009688");

        } else {
            return Color.parseColor("#FFB7B7B7");
        }


    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        cityList.clear();
        android.util.Log.e(TAG, "cityList size: " + cityList.toString());

        flagPolyLineSelectColor = mHashMap.get(polyline);
        if (routeArray.length() > 0) {
            android.util.Log.e(TAG, "flagPolyLineSelectColor: " + routeArray.length());
            try {
                Object clickPosition = routeArray.get(flagPolyLineSelectColor);
                Object zerothPosition = routeArray.get(0);
                routeArray.put(0, clickPosition);
                routeArray.put(flagPolyLineSelectColor, zerothPosition);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            createPolyLine();
        }

    }
}
