package com.cipher.nidhi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class signup_qr extends AppCompatActivity
{
    SurfaceView sv;
    CameraSource cs;
    TextView msg;
    BarcodeDetector barcodeDetector;

    private RequestQueue mQueue;
    private  AlertDialog alertDialog;
    private Button btn_continue;
    private EditText txt_uname,txt_pass,txt_memno;

    String scode,uname,pass,mno;

    public static void handleSSLHandshake()
    {
        try
        {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        }
        catch (Exception ignored)
        {
            ignored.printStackTrace();
        }
    }
    //===========================================================================================//

    private void getqr_code()
    {
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cs = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).build();

        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cs.start(holder);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cs.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> code = detections.getDetectedItems();
                if (code.size() != 0) {
                    msg.post(new Runnable() {
                        @Override
                        public void run() {
                            scode = code.valueAt(0).displayValue;
                            msg.setText(scode);
                            //signup_api();
                        }
                    });
                }
            }
        });
    }

    //===========================================================================================//

    private void store_to_file(String filename,String data)
    {
        FileOutputStream outputStream;

        try
        {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //===========================================================================================//

    private void signup_api()
    {
        final String url;

        //url = "https://api.myjson.com/bins/kp9wz";
        url = "https://192.168.15.202/apihandler/Apihandler/fetchcodes";


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    String client="";
                    int mno=0;
                    Log.d("response call",response);
                    JSONObject obj1 = new JSONObject(response);
                    JSONArray obj=obj1.getJSONArray("data");

                    for (int i = 0; i < obj.length(); i++)
                    {
                        JSONObject data = obj.getJSONObject(i);

                        client = data.getString("_client");
                        mno = data.getInt("_member_no");
                        Log.d("response",client+" "+mno);
                    }
                    store_to_file("data",client);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("companycode", scode);
                //params.put("companycode", scode);
                //params.put("companycode", scode);
                return params;
            }
        };

                mQueue.add(request);
    }
    //===========================================================================================//

    private void get_Data()
    {
        uname=txt_uname.getText().toString();
        pass=txt_pass.getText().toString();
        mno=txt_memno.getText().toString();
    }

    //===========================================================================================//

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);              //will hide the title
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_qr);

        mQueue = Volley.newRequestQueue(this);
        msg = findViewById(R.id.txt_msg);
        sv = findViewById(R.id.camera_prev);
        btn_continue=findViewById(R.id.btn_continue);
        txt_uname=findViewById(R.id.txt_uname);
        txt_pass=findViewById(R.id.txt_pass);
        txt_memno=findViewById(R.id.txt_mno);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        alertDialog = alertDialogBuilder.create();


        handleSSLHandshake();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getqr_code();
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                get_Data();
                signup_api();
            }
        });
    }

}

