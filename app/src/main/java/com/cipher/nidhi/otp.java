package com.cipher.nidhi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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

public class otp extends AppCompatActivity
{
    private PinEntryEditText pinEntry;
    private RequestQueue mQueue;
    private SharedPreferences AppPref;
    private SharedPreferences.Editor AppEdit;

    private String otp="",mno="",scode="",server_response;
    private Button btn_submit;

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

    private void validate_pin()
    {

        if (pinEntry != null)
        {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str)
                {
                    if (str.toString().equals("1234"))
                    {
                        otp=str.toString();
                        Log.d("X",mno+" "+scode+" "+otp);
                        signup_api();
                    }
                    else
                    {
                        Toast toast=Toast.makeText(getApplicationContext(),"Check your OTP code again",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }

    }

    private void save_prefernce(String key,String val)
    {
        AppEdit.putString(key,val);
        AppEdit.commit();
    }

    private void getdata()
    {
        Intent iotp=getIntent();
        mno=iotp.getStringExtra("member_key");
        scode=iotp.getStringExtra("scode");
    }

    private void check()
    {
        if(server_response.equals("1"))
        {
            Toast msg=Toast.makeText(getApplicationContext(),"Signup Complete",Toast.LENGTH_LONG);
            msg.show();
            Intent login = new Intent(otp.this, login.class);
            startActivity(login);
            save_prefernce("companycode",scode);
            save_prefernce("memberno", mno);
        }
        else
        {
            Toast msg=Toast.makeText(getApplicationContext(),"Signup Failed",Toast.LENGTH_LONG);
            msg.show();
        }
    }

    private void signup_api()
    {
        final String url;

        //url = "https://api.myjson.com/bins/kp9wz";
        //url = "https://192.168.15.46/apihandler/Apihandler/validateOTP";
        url = "https://ignosi.in/apihandler/Apihandler/validateOTP";


        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    Log.d("response call",response);
                    JSONObject obj = new JSONObject(response);

                    server_response=obj.getString("status");
                    Log.d("server response",server_response);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                check();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("companycode", scode);
                params.put("otp", otp);
                params.put("memberno", mno);
                return params;
            }
        };

        mQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        AppPref= PreferenceManager.getDefaultSharedPreferences(this);
        AppEdit=AppPref.edit();

        mQueue = Volley.newRequestQueue(this);
        pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
        btn_submit=findViewById(R.id.submit);

        handleSSLHandshake();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getdata();
        validate_pin();
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                signup_api();
            }
        });
    }
}
