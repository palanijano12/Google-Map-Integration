package mindvalley.com.navigation.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by anand_android on 10/1/2016.
 */
public class RequestPermission implements IPermissionHandler {

    private String TAG = "RequestPermission";
    private PermissionProducer mPermissionProducer;

    public static IPermissionHandler newInstance(PermissionProducer permissionProducer) {
        RequestPermission requestPermission = new RequestPermission();
        requestPermission.mPermissionProducer = permissionProducer;
        return requestPermission;
    }



    @Override
    public void callLocationPermissionHandler( ) {
        if (ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mPermissionProducer.getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat .requestPermissions(mPermissionProducer.getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mPermissionProducer.onReceivedPermissionStatus(PERMISSIONS_REQUEST_LOCATION, true);
        }
    }

}
