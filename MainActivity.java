package mindvalley.com.navigation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import mindvalley.com.navigation.permission.IPermissionHandler;
import mindvalley.com.navigation.permission.IView;
import mindvalley.com.navigation.permission.PermissionProducer;
import mindvalley.com.navigation.permission.RequestPermission;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ResponseListener, IView, PermissionProducer {
    AutoCompleteTextView txtSource;
    AutoCompleteTextView txtDestination;
    AutoCompleteTextView txtVia;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private IPermissionHandler iPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iPermissionHandler = RequestPermission.newInstance(this);

        txtSource = (AutoCompleteTextView) findViewById(R.id.source);
        txtDestination = (AutoCompleteTextView) findViewById(R.id.destination);
        txtVia = (AutoCompleteTextView) findViewById(R.id.via);


        txtSource.setDropDownBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white)));
        txtDestination.setDropDownBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white)));
        txtVia.setDropDownBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white)));
        PlacesAutoCompleteAdapter mPlaceAdapter = new PlacesAutoCompleteAdapter(this, R.layout.inflate_autocomplete_list_item);

        txtSource.setAdapter(mPlaceAdapter);
        txtDestination.setAdapter(mPlaceAdapter);
        txtVia.setAdapter(mPlaceAdapter);


        findViewById(R.id.next).setOnClickListener(this);
        if (CommonUtils.getInstance().isAboveMarshmallow()) {
            iPermissionHandler.callLocationPermissionHandler();
        } else {
            setLocation();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                if (validate()) {
                    getRouteAPI();
                }
                break;
        }
    }

    private void getRouteAPI() {
        if (checkNetWork()) {
            try {
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

                String url = makeURL(getLatLongStringFromAddress(txtSource.getText().toString()).replace(" ", "%20"), getLatLongStringFromAddress(txtDestination.getText().toString()).replace(" ", "%20"), txtVia.getText().toString().replace(" ", "%20"));
                Call<ResponseBody> call = apiService.getRouteData(url);
                RetroFitUtils.getInstance().retrofitEnqueue(call, this, 0);
                // new connectAsyncTask(url).execute();

            } catch (Exception e) {
                e.printStackTrace();
                dismissProgressbar();
            }
        }
    }

    private String makeURL(String source, String dest, String via) {

        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(source);
        urlString.append("&destination=");// to
        urlString.append(dest);
        urlString.append("&waypoints=");// to
        urlString.append(via);
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        // urlString.append("&key=AIzaSyB7ukSPRAS4ItWvrYBDruaEjbbKxRfDp7Y");
        return urlString.toString();
    }

    private boolean checkNetWork() {

        if (CommonUtils.getInstance().hasNetworkConnection(this)) {
            showProgressBar();
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            dismissProgressbar();
            return false;
        }


    }

    private void dismissProgressbar() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.imageView_logo).setVisibility(View.GONE);
    }

    private void showProgressBar() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.imageView_logo).setVisibility(View.VISIBLE);
    }

    private boolean validate() {
        int count = 0;
        if (txtSource.getText().toString().isEmpty()) {
            txtSource.setError("Enter source");
        } else {
            txtSource.setError(null);
            count++;
        }
        if (txtDestination.getText().toString().isEmpty()) {
            txtDestination.setError("Enter destination");
        } else {
            txtDestination.setError(null);

            count++;
        }
        if (txtVia.getText().toString().isEmpty()) {
            txtVia.setError("Enter source");
        } else {
            txtVia.setError(null);

            count++;
        }
        if (count == 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://mindvalley.com.navigation/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://mindvalley.com.navigation/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public String getLatLongStringFromAddress(String strAddress) {
    /*    OfficeAddressDetails mOfficeAddressDetails = new OfficeAddressDetails();
        List<Double> mLatLong = new ArrayList<>();*/
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }
            try {

                Address location = address.get(0);
                return location.getLatitude() + "," + location.getLongitude();


            } catch (Exception e) {
                e.printStackTrace();
                return strAddress;
            }


        } catch (IOException e) {

            e.printStackTrace();
            //return null;
        }
        return strAddress;
    }

    public String getAddressFromLatLng(Location latLng) {
    /*    OfficeAddressDetails mOfficeAddressDetails = new OfficeAddressDetails();
        List<Double> mLatLong = new ArrayList<>();*/
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 1);
            Address returnedAddress = address.get(0);
            StringBuilder strReturnedAddress = new StringBuilder("");
            for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
            }

            return strReturnedAddress.toString();


        } catch (IOException e) {

            e.printStackTrace();
            //return null;
        }
        return latLng.getLatitude() + "," + latLng.getLongitude();

    }

    @Override
    public void onSuccess(String mResponse, int flag) {
        dismissProgressbar();
        switch (flag) {
            case 0:
                try {
                    if (mResponse != null) {
                        final JSONObject json = new JSONObject(mResponse);

                        JSONArray routeArray = json.getJSONArray("routes");
                        if (routeArray.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Enter valid address", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent iIntent = new Intent(MainActivity.this, MapActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("route", mResponse);
                            iIntent.putExtras(mBundle);
                            startActivity(iIntent);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFailure(Throwable mThrowable, int flag) {

    }

    @Override
    public void showDialog(String mResponse, int flag) {

    }

    @Override
    public void showErrorDialog(String mResponse, int flag) {

    }

    @Override
    public void showInternalServerErrorDialog(String mResponse, int flag) {

    }

    @Override
    public void logOut(int flag) {

    }

    @Override
    public FragmentActivity getActivity() {
        return this;
    }

    @Override
    public void onReceivedPermissionStatus(int code, boolean isGrated) {
        switch (code) {
            case IPermissionHandler.PERMISSIONS_REQUEST_LOCATION:
                if (isGrated) {
                    setLocation();
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case IPermissionHandler.PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //     showMessage("Accepted");
                    onReceivedPermissionStatus(IPermissionHandler.PERMISSIONS_REQUEST_LOCATION, true);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    onReceivedPermissionStatus(IPermissionHandler.PERMISSIONS_REQUEST_LOCATION, false);
                }

            }
            break;
        }
    }

    private void setLocation() {
        if (CommonUtils.getInstance().getLocation(getApplicationContext()) != null) {
            txtSource.setText(getAddressFromLatLng(CommonUtils.getInstance().getLocation(getApplicationContext())));
        }


    }
}
