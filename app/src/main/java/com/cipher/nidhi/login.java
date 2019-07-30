package com.cipher.nidhi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

public class login extends AppCompatActivity
{
    private Button btn_login;
    private TextView new_user;
    private EditText user_name,password;
    private SharedPreferences AppPref;
    private SharedPreferences.Editor AppEdit;

    private RequestQueue mQueue;
    private String username="", pass="", server_response="",server_msg="",scode="";

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

    private void getdata()
    {
        username=user_name.getText().toString();
        pass=password.getText().toString();
    }

    private void save_prefernce(String key,String val)
    {
        AppEdit.putString(key,val);
        AppEdit.commit();
    }

    public String get_pref(String key)
    {
        String val;
        val=AppPref.getString(key,"default");
        return val;
    }

    private void check()
    {
        if(server_response.equals("1"))
        {
            Toast toast=Toast.makeText(getApplicationContext(),"Sign-In Success "+server_msg,Toast.LENGTH_SHORT);
            toast.show();
            save_prefernce("uname",username);
            save_prefernce("memberno",server_msg);
            save_prefernce("companycode",scode);
            Intent dash = new Intent(login.this, dashboard.class);
            dash.putExtra("uname",username);
            dash.putExtra("memberno",server_msg);
            dash.putExtra("companycode",scode);
            startActivity(dash);

        }
        else
        {
            Toast toast=Toast.makeText(getApplicationContext(),"Sign-In Fail"+" "+server_msg,Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private String HashPassword(String pass)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(pass.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            while (hashtext.length() < 32)
            {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    private boolean isOnline()
    {
        Context context=getApplicationContext();
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo=cm.getActiveNetworkInfo();

        if(ninfo!=null && ninfo.isConnectedOrConnecting())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void signin_api()
    {
        final String url;

        //url = "https://api.myjson.com/bins/kp9wz";
        //url = "https://192.168.15.46/apihandler/Apihandler/signinuser";
        url = "https://ignosi.in/apihandler/Apihandler/signinuser";


        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response call", response);
                    JSONObject obj = new JSONObject(response);

                    server_response = obj.getString("status");
                    server_msg=obj.getString("msg");
                    scode=obj.getString("data");

                    Log.d("server response", server_response+" "+server_msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                check();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("companycode", "finsoft");
                params.put("username", username);
                params.put("password", HashPassword(pass));
                return params;
            }
        };

        mQueue.add(request);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //will hide the title
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mQueue = Volley.newRequestQueue(this);
        AppPref= PreferenceManager.getDefaultSharedPreferences(this);
        AppEdit=AppPref.edit();

        btn_login=findViewById(R.id.btn_login);
        new_user=findViewById(R.id.signup);
        user_name=findViewById(R.id.txt_login);
        password=findViewById(R.id.txt_pass);

        handleSSLHandshake();
    }


    protected void onStart()
    {
        super.onStart();

        if(!isOnline())
        {
            Toast toast=Toast.makeText(getApplicationContext(),"Check Connection",Toast.LENGTH_LONG);
            toast.show();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getdata();
                signin_api();
            }
        });

        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent signup = new Intent(login.this, signup_qr.class);
                startActivity(signup);
            }
        });
    }
}
