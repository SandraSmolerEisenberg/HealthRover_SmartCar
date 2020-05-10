package se.healthrover.conectivity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import se.healthrover.entities.HealthRoverCar;
import se.healthrover.ui_activity_controller.CarSelect;
import se.healthrover.ui_activity_controller.ManualControl;

public class OkHttpWebService implements HealthRoverWebService {


    private OkHttpClient client;
    private String responseData;
    private static final String HTTP_STATUS_RESPONSE = "status";


    public OkHttpWebService(){
        client = new OkHttpClient();
    }


    @Override
    public void createHttpRequest(final String url, final Activity activity) {
        //Builds a GET request to a given url
        final Request request = new Request.Builder()
                .url(url)
                .build();

        //enqueue the request and run it on a thread, Logging the failures into the log and on success handling the response depending of the response body
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Error","Failed to connect: "+e.getMessage());
                        System.out.println("error" + e.getMessage());
                        call.cancel();
                    }
                });

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            responseData = response.body().string();
                            if (responseData.equals(HTTP_STATUS_RESPONSE)){
                                Intent intent = new Intent(activity, ManualControl.class);
                                intent.putExtra("carName", HealthRoverCar.getCarNameByUrl(url.substring(0,20)));
                                activity.startActivity(intent);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("Success","Success: "+response.code());
                        }


                    }
                });



            }});

    }
}