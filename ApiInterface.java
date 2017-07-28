package mindvalley.com.navigation;

/**
 * Created by aravindraj on 9/15/2016.
 */

import org.json.JSONObject;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiInterface {
    @GET
    Call<ResponseBody> getRouteData(@Url String url);
}