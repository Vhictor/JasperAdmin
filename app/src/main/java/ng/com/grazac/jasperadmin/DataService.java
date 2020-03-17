package ng.com.grazac.jasperadmin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class DataService extends Service{

    public static final String CHANNEL_ID = "DataService";
    RequestQueue mQueue;
    private Timer timer;
    private TimerTask timerTask;
    public int counter=0;
    boolean done;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Data Service")
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startTimer();
        startForeground(1, notification);

        return START_STICKY;
    }



    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 minute and the period is 10 seconds
        timer.schedule(timerTask, 1000, 30000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
                    getPendingData();
                }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Data Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

    }

    @Override
    public void onCreate() {
    mQueue = Volley.newRequestQueue(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        Toast.makeText(this, "Data service has been stopped", Toast.LENGTH_SHORT).show();


    }




    private void getPendingData() {

        String url = "https://jassperdata.com/embedded/getPending?secret_key=980fee286d26f20b03f88a0bbb26fb1aa30b945b4f2c02451010c8349fc11a1d";
        final JsonObjectRequest data = new JsonObjectRequest(Request.Method.GET, url, null, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int error = response.getInt("err");
                            String msg =response.getString("msg");
                            if (error == 0 && !msg.isEmpty()){
//                                Toast.makeText(getApplicationContext(), "Message from server : " +msg, Toast.LENGTH_SHORT).show();
                                //getPendingData();

                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            int id = response.getInt("id");
                            String phone = response.getString("phone");
                            String dataSize = response.getString("data");
                            Log.d("Phone", "No" +phone);
  //                          Toast.makeText(getApplicationContext(), "Phone no is " +phone, Toast.LENGTH_SHORT).show();
                            sendTheMessage(id,phone,dataSize);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error " +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(data);
    }



    public void sendTheMessage(int id, String userNo, String dataSize){
        String message = "";
        String destinationAddress = "131";
        if (dataSize.equalsIgnoreCase("1GB")){
            message = "SMEC " +userNo+ " 1000 4172";
        }else if (dataSize.equalsIgnoreCase("2GB")){
            message = "SMED " +userNo+ " 2000 4172";
        }else if (dataSize.equalsIgnoreCase("5GB")){
            message = "SMEE " +userNo+ " 5000 4172";
        } else if (dataSize.equalsIgnoreCase("3GB")){
            forOneGB(userNo);
            forTwoGB(userNo);
            checkStatus(id,1);
            return;
        } else if (dataSize.equalsIgnoreCase("4GB")){
            forTwoGB(userNo);
            forTwoGB(userNo);
            checkStatus(id,1);
            return;
        } else if (dataSize.equalsIgnoreCase("10GB")){
            forFiveGB(userNo);
            forFiveGB(userNo);
            checkStatus(id,1);
            return;

        }else {
            return;
        }


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress, null, message, null, null);
//        Toast.makeText(getApplicationContext(), "SMS sent.",
//                Toast.LENGTH_LONG).show();

        if(message!=null){
            checkStatus(id,1);
        }


    }


    private void forOneGB(String userNo){

        String message = "SMEC " +userNo+ " 1000 4172";
        String destinationAddress = "131";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress, null, message, null, null);
//        Toast.makeText(getApplicationContext(), "SMS sent.",
//                Toast.LENGTH_LONG).show();

    }

    private void forTwoGB(String userNo){

        String message = "SMED " +userNo+ " 2000 4172";
        String destinationAddress = "131";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress, null, message, null, null);
//        Toast.makeText(getApplicationContext(), "SMS sent.",
//                Toast.LENGTH_LONG).show();

    }

    private void forFiveGB(String userNo){

        String message = "SMEE " +userNo+ " 5000 4172";
        String destinationAddress = "131";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();

    }


    private void checkStatus(int id, int status) {
        String url = "https://jassperdata.com/embedded/updateStatus?secret_key=980fee286d26f20b03f88a0bbb26fb1aa30b945b4f2c02451010c8349fc11a1d&id=" +id + "&status=" +status;
        final JsonObjectRequest dataStatus = new JsonObjectRequest(Request.Method.GET, url, null, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int err = response.getInt("err");
                            if (err==0){
                       //         Toast.makeText(getApplicationContext(), "Successful " +err, Toast.LENGTH_SHORT).show();

                              //  getPendingData();
                            }else {
                                Toast.makeText(getApplicationContext(), "Unsuccessul " + err, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error " +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(dataStatus);
    }



}
