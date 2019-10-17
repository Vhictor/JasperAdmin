package ng.com.grazac.jasperadmin;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button button;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getPendingData();
                //getPendingData();
            }
        });



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
                             // Toast.makeText(MainActivity.this, "No pending data from website", Toast.LENGTH_SHORT).show();
                                getPendingData();
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
                            Toast.makeText(MainActivity.this, "Phone no is " +phone, Toast.LENGTH_SHORT).show();
                            sendTheMessage(id,phone,dataSize);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error " +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(data);
    }

    private void sendSms(int id, String phone, String dataSize) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        1);
            }
        }else {
            sendTheMessage(id, phone, dataSize);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        sendTheMessage(1,  "09069167788", "1GB");
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

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
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();

        if(message!=null){
            checkStatus(id,1);
        }


    }


    private void forOneGB(String userNo){

        String message = "SMEC " +userNo+ " 1000 4172";
        String destinationAddress = "131";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();

    }

    private void forTwoGB(String userNo){

        String message = "SMED " +userNo+ " 2000 4172";
        String destinationAddress = "131";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();

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
                                Toast.makeText(MainActivity.this, "Successful " +err, Toast.LENGTH_SHORT).show();
                                getPendingData();
                            }else {

                                Toast.makeText(MainActivity.this, "Unsuccessul " + err, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error " +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(dataStatus);
    }

//    private void sendSMS(final int id, String userNo, String dataSize) {
//        String message = "";
//        String destinationAddress = "131";
//        if (dataSize.equalsIgnoreCase("1GB")){
//            message = "SMEC " +userNo+ " 1000 4172";
//        }else if (dataSize.equalsIgnoreCase("2GB")){
//            message = "SMED " +userNo+ " 2000 4172";
//        }else if (dataSize.equalsIgnoreCase("5GB")){
//            message = "SMEE " +userNo+ " 5000 4172";
//        }
//
//
//        String SENT = "SMS_SENT";
//        String DELIVERED = "SMS_DELIVERED";
//
//        final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
//                new Intent(SENT), 0);
//
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
//                new Intent(DELIVERED), 0);
//
//        //---when the SMS has been sent---
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS sent",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(getBaseContext(), "Generic failure",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(getBaseContext(), "No service",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(getBaseContext(), "Null PDU",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(getBaseContext(), "Radio off",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        //---when the SMS has been delivered---
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), "SMS not delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(destinationAddress, null, message, sentPI, deliveredPI);
//
//    }


}
