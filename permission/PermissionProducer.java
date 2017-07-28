package mindvalley.com.navigation.permission;



/**
 * Created by guru on 2/27/2016.
 */
public interface PermissionProducer extends IView {
    void onReceivedPermissionStatus(int code, boolean isGrated);
}
