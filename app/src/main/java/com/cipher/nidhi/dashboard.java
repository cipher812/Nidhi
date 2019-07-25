package com.cipher.nidhi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class dashboard extends AppCompatActivity
{
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private ImageView profpic;
    private TextView cname, dep, loan,uid;
    private RequestQueue mQueue;

    private SharedPreferences AppPref;
    private SharedPreferences.Editor AppEdit;

    private String server_response,server_message,scode,memberno,member_name,member_url,deposits,loans;
    private String account_no[]=new String[50];
    private String amount[]=new String[50];
    private String interestrate[]=new String[50];
    private String startdate[]=new String[50];
    private String maturitydate[]=new String[50];
    private String maturityamount[]=new String[50];

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

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

    public String get_pref(String key)
    {
        String val;
        val=AppPref.getString(key,"default");
        return val;
    }

    private void get_prefernces()
    {
        Intent iotp=getIntent();
        memberno=iotp.getStringExtra("memberno");
        scode=iotp.getStringExtra("companycode");
        Log.v("xpref",scode+" "+memberno);
    }

    private void dashBoard_api()
    {
        final String url;

        //url = "https://api.myjson.com/bins/kp9wz";
        url = "https://192.168.15.46/apihandler/Apihandler/getprofiledata";
        //url = "https://192.168.15.202/apihandler/Apihandler/fetchcodes";


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    Log.d("response call", response);
                    JSONObject obj = new JSONObject(response);

                    server_response = obj.getString("status");
                    server_message = obj.getString("msg");

                    Log.i("raw",response.toString());

                    JSONObject data = new JSONObject(obj.getString("data"));
                    Log.i("raw",data.toString());

                    if(data.getString("memberstatus").equals("1"))
                    {
                        member_name = data.getString("membername");
                        member_url = data.getString("memberphoto");
                        deposits=data.getString("deposits");
                        loans=data.getString("loans");
                        memberno=data.getString("memberid");
                        Log.i("dash response", server_response+" "+server_message+" "+member_name+" "+member_url);

                        set_data();

                        JSONArray arr=new JSONArray(data.getString("have"));
                        Log.i("raw",arr.toString());

                        for(int i=0;i<arr.length();i++)
                        {
                            JSONObject havex=arr.getJSONObject(i);

                            account_no[i]=havex.getString("accountno");
                            amount[i]=havex.getString("amount");
                            interestrate[i]=havex.getString("interestrate");
                            startdate[i]=havex.getString("startdate");
                            maturitydate[i]=havex.getString("maturitydate");
                            maturityamount[i]=havex.getString("maturityamount");

                            Log.i("raw",account_no[i]);
                        }

                        expand_data();

                    }
                    else
                    {
                        Toast msg=Toast.makeText(getApplicationContext(),"Account Not Active",Toast.LENGTH_LONG);
                        msg.show();
                    }



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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("companycode", scode);
                params.put("memberno",memberno);
                return params;
            }
        };

        mQueue.add(request);
    }

    private void initData()
    {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Have");//active Loans
        listDataHeader.add("Owe");//active Deposits
        listDataHeader.add("Dues");
        listDataHeader.add("Closed Loans");
        listDataHeader.add("Closed Deposits");

        List<String> Closed_Deposits = new ArrayList<>();
        Closed_Deposits.add("This is Expandable ListView");

        List<String> Closed_Loans = new ArrayList<>();
        Closed_Loans.add("1");
        Closed_Loans.add("2");
        Closed_Loans.add("3");

        List<String> Dues = new ArrayList<>();
        Dues.add("1");
        Dues.add("2");
        Dues.add("3");


        List<String> Owe = new ArrayList<>();
        Owe.add("1");
        Owe.add("2 ");
        Owe.add("3");
        Owe.add("4");

        List<String> Have = new ArrayList<>();
        for (int i=0;i<account_no.length;i++)
        {
            if (account_no[i]==null)
            {
                continue;
            }
            Have.add(" Account no               :   "+account_no[i]+"\n"+" 32175trrr+Amount                      :  "+amount[i]+"\n Intrest Rate              :   "+interestrate[i]+"\n Start Date                 :   "+startdate[i]+"\n Maturity Date         :    "+maturitydate[i]+"\n Maturity Amount  :  "+maturityamount[i]);
        }


        listHash.put(listDataHeader.get(0), Have);
        listHash.put(listDataHeader.get(1), Owe);
        listHash.put(listDataHeader.get(2), Dues);
        listHash.put(listDataHeader.get(3), Closed_Deposits);
        listHash.put(listDataHeader.get(4), Closed_Loans);
    }

    public void set_data()
    {
        cname.setText(member_name);
        loan.setText(loans);
        dep.setText(deposits);
        uid.setText(memberno);
        new DownloadImageTask(profpic).execute(member_url);
    }

    private void expand_data()
    {
        initData();
        listAdapter = new ExpandableListAdapter(dashboard.this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //will hide the title
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mQueue = Volley.newRequestQueue(this);

        listView = findViewById(R.id.expv);
        profpic = findViewById(R.id.profile_pic);
        cname = findViewById(R.id.txt_cname);
        dep = findViewById(R.id.txt_dep);
        loan = findViewById(R.id.txt_loan);
        uid=findViewById(R.id.txt_userid);

        AppPref= PreferenceManager.getDefaultSharedPreferences(this);
        AppEdit=AppPref.edit();


        handleSSLHandshake();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        get_prefernces();
        dashBoard_api();

    }
}
