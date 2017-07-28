package mindvalley.com.navigation;


import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by anand_android on 9/28/2016.
 */
public interface ResponseListener {
    void onSuccess(String mResponse, int flag);
    void onFailure(Throwable mThrowable, int flag);
    void showDialog(String mResponse, int flag);
    void showErrorDialog(String mResponse, int flag);
    void showInternalServerErrorDialog(String mResponse, int flag);
    void logOut(int flag);

}
