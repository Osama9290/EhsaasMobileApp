package com.example.ehsaas.UI.notification;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAuPkmt3M:APA91bEEGNcYSHC9yJv3MpB62L-j25_NKTXoEeu6_fvkWb14DlFp4OMUyc6TFyNialmrc7HqaOK3bEhCSGMAXgrsWsOwWROISY63j40wHiSD6ht1L9e9sZH5qIY5v46dHY1l4PiGZOy-"
    })

    @POST("fcm/send")
    public Call<Response> sendNotification(@Body Sender body);
}
