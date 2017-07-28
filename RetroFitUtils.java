package mindvalley.com.navigation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anand_android on 9/28/2016.
 */
public class RetroFitUtils {
    //Single ton object...
    private static RetroFitUtils RetroFitUtility = null;
    private final String TAG = "RetroFitUtils";

    //Single ton method...
    public static RetroFitUtils getInstance() {
        if (RetroFitUtility != null) {
            return RetroFitUtility;
        } else {
            RetroFitUtility = new RetroFitUtils();
            return RetroFitUtility;
        }
    }


    public void retrofitEnqueue(Call<ResponseBody> call, final ResponseListener resListener, final int flag) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "response.code(): " + response.code());
                Log.e(TAG, "response.code(): " + response.raw().code());
                Log.e(TAG, "=" + response.raw());
                String resultRes = null;
                if (response.body() != null) {
                    resultRes = getStringFromByte(response.body().byteStream());
                    Log.e(TAG, "=" + resultRes);
                }
                switch (response.code()) {
                    case 200://success response
                        resListener.onSuccess(resultRes, flag);
                        break;
                    case 201://Message
                        resListener.showDialog(resultRes, flag);

                        break;
                    case 400://Request error
                        if (response.errorBody() != null) {
                            resListener.showErrorDialog(getStringFromByte(response.errorBody().byteStream()), flag);

                        }
                        break;
                    case 401://Unauthorized
                        resListener.logOut(flag);

                        break;
                    case 500://internal server error
                        if (response.errorBody() != null) {
                            resListener.showInternalServerErrorDialog(getStringFromByte(response.errorBody().byteStream()), flag);

                        }


                        break;
                    case 204://only for device token update
                        Log.e(TAG, "Device token updated");
                        break;
                    default:
                        resListener.logOut(flag);
                        break;
                }

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resListener.onFailure(t, flag);
            }
        });
    }

    public boolean isConnectingToInternet(Context _context) {

        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }


    public String getStringFromByte(InputStream inputStream) {

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String result = sb.toString();
        return result;
    }
}
