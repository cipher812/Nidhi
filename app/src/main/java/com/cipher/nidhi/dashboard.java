package com.cipher.nidhi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
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

public class dashboard extends AppCompatActivity {
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private ImageView profpic;
    private TextView cname, dep, loan, uid;
    private RequestQueue mQueue;

    private SharedPreferences AppPref;
    private SharedPreferences.Editor AppEdit;

    private String server_response, server_message, scode, memberno, member_name, member_url, deposits, loans,username,server_msg;

    class var
    {
    private String account_no[] = new String[50];
    private String amount[] = new String[50];
    private String interestrate[] = new String[50];
    private String startdate[] = new String[50];
    private String maturitydate[] = new String[50];
    private String maturityamount[] = new String[50];
    private String var7[]=new String[50];
    }

    var have=new var();
    var owe=new var();
    var due=new var();
    var cloans=new var();
    var cdep=new var();

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    public static void handleSSLHandshake()
    {
        try {
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
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public String get_pref(String key)
    {
        String val;
        val = AppPref.getString(key, "default");
        return val;
    }

    private void get_prefernces()
    {
        Intent iotp = getIntent();
        memberno = iotp.getStringExtra("memberno");
        scode = iotp.getStringExtra("companycode");
        username=iotp.getStringExtra("uname");
        Log.v("xpref", scode + " " + memberno);
    }

    private void dashBoard_api()
    {
        final String url;

        //url = "https://api.myjson.com/bins/kp9wz";
        //url = "https://192.168.15.46/apihandler/Apihandler/getprofiledata";
        url = "https://ignosi.in/apihandler/Apihandler/getprofiledata";


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {
                    Log.d("response call", response);
                    JSONObject obj = new JSONObject(response);

                    server_response = obj.getString("status");
                    server_message = obj.getString("msg");

                    Log.i("raw", response);

                    JSONObject data = new JSONObject(obj.getString("data"));
                    Log.i("raw", data.toString());

                    if (data.getString("memberstatus").equals("1"))
                    {
                        member_name = data.getString("membername");
                        member_url = data.getString("memberphoto");
                        deposits = data.getString("deposits");
                        loans = data.getString("loans");
                        memberno = data.getString("memberid");
                        Log.i("dash response", server_response + " " + server_message + " " + member_name + " " + member_url);


                        JSONArray arr_have = new JSONArray(data.getString("have"));
                        Log.i("raw", arr_have.toString());

                        for (int i = 0; i < arr_have.length(); i++)
                        {
                            JSONObject axx = arr_have.getJSONObject(i);

                            have.account_no[i] = axx.getString("accountno");
                            have.amount[i] = axx.getString("amount");
                            have.interestrate[i] = axx.getString("interestrate");
                            have.startdate[i] = axx.getString("startdate");
                            have.maturitydate[i] = axx.getString("maturitydate");
                            have.maturityamount[i] = axx.getString("maturityamount");
                        }

                        JSONArray arr_owe = new JSONArray(data.getString("owe"));
                        Log.i("raw", arr_owe.toString());

                        for (int i = 0; i < arr_owe.length(); i++)
                        {
                            JSONObject axx = arr_owe.getJSONObject(i);

                            owe.account_no[i] = axx.getString("loanno");
                            owe.amount[i] = axx.getString("loanamount");
                            owe.interestrate[i] = axx.getString("interestrate");
                            owe.startdate[i] = axx.getString("outstanding");
                            owe.maturitydate[i] = axx.getString("dueperiod");
                            owe.maturityamount[i] = axx.getString("tenure");
                            owe.var7[i]=axx.getString("startdate");
                        }

                        JSONArray arr_due = new JSONArray(data.getString("due"));
                        Log.i("raw", arr_due.toString());

                        for (int i = 0; i < arr_due.length(); i++)
                        {
                            JSONObject axx = arr_owe.getJSONObject(i);

                            due.account_no[i] = axx.getString("loanno");
                            due.amount[i] = axx.getString("loanamount");
                            due.interestrate[i] = axx.getString("interestrate");
                            due.startdate[i] = axx.getString("outstanding");
                            due.maturitydate[i] = axx.getString("dueperiod");
                            due.maturityamount[i] = axx.getString("tenure");
                        }

                        JSONArray arr_dep = new JSONArray(data.getString("closeddeposits"));
                        Log.i("dep", arr_dep.toString());

                        for (int i = 0; i < arr_dep.length(); i++)
                        {
                            JSONObject axx = arr_dep.getJSONObject(i);

                            cdep.account_no[i] = axx.getString("accountno");
                            cdep.interestrate[i] = axx.getString("startdate");
                            cdep.startdate[i] = axx.getString("closeddate");
                            cdep.maturitydate[i] = axx.getString("closedamount");
                            cdep.var7[i]=axx.getString("depositamount");

                        }

                        JSONArray arr_loans = new JSONArray(data.getString("closedloans"));
                        Log.i("raw", arr_loans.toString());

                        for (int i = 0; i < arr_loans.length(); i++)
                        {
                            JSONObject axx = arr_loans.getJSONObject(i);

                            cloans.account_no[i] = axx.getString("loanno");
                            cloans.amount[i] = axx.getString("loanamount");
                            cloans.interestrate[i] = axx.getString("totalpaid");
                            cloans.startdate[i] = axx.getString("closeddate");
                            cloans.maturitydate[i] = axx.getString("startdate");
                        }

                        set_data();
                        expand_data();

                    }
                    else
                    {
                        Toast msg = Toast.makeText(getApplicationContext(), "Account Not Active", Toast.LENGTH_LONG);
                        msg.show();
                    }


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    set_data();
                    expand_data();
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
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("companycode", scode);
                params.put("memberno", memberno);
                return params;
            }
        };

        mQueue.add(request);
    }

    private Boolean logoff()
    {
        final String url;

        //url = "https://api.myjson.com/bins/kp9wz";
        //url = "https://192.168.15.46/apihandler/Apihandler/signinuser";
        url = "https://ignosi.in/apihandler/Apihandler/logoffuser";


        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response call", response);
                    JSONObject obj = new JSONObject(response);

                    server_response = obj.getString("status");
                    server_msg=obj.getString("msg");

                    Log.d("server response", server_response+" "+server_msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                params.put("username", username);
                return params;
            }
        };

        mQueue.add(request);

        if(server_response.equals("1"))
        {
            return true;
        }
        else
        {
            return false;
        }

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

        List<String> Closed_Loans = new ArrayList<>();
        for (int i = 0; i < cloans.account_no.length; i++)
        {
            if (due.account_no[i] == null)
            {
                continue;
            }

            Closed_Loans.add("Loan No             :  "+cloans.account_no[i]+"\nLoan Amount  :  "+cloans.amount[i]+"\nLoan Paid          :  "+cloans.interestrate[i]+"\nTotal Paid          :  "+cloans.startdate[i]+"\nClosed Date     :  "+cloans.startdate[i]+"\nStart Date         :  "+cloans.maturitydate[i]);
        }

        List<String> Closed_Deposits = new ArrayList<>();
        for (int i = 0; i < cdep.account_no.length; i++)
        {
            if (cdep.account_no[i] == null)
            {
                continue;
            }

            Closed_Deposits.add("Account no                :   " + cdep.account_no[i] + "\nAmount                       :  " + cdep.var7[i] + "\nStart Date                  :   " + cdep.interestrate[i] + "\nClosed Amount       :   " + cdep.startdate[i]+"\nClosed Date              :   "+cdep.maturitydate[i]);
        }


        List<String> Dues = new ArrayList<>();
        for (int i = 0; i < due.account_no.length; i++)
        {
            if (due.account_no[i] == null)
            {
                continue;
            }

            Dues.add("Loan No            :  "+due.account_no[i]+"\nLoan Amount :  "+due.amount[i]+"\nInterstate        :  "+due.interestrate[i]+"\nOutstanding   :  "+due.startdate[i]+"\nDue Period      :  "+due.maturitydate[i]+"\nTenture             :  "+due.maturityamount[i]);
        }


        List<String> Owe = new ArrayList<>();
        for (int i = 0; i < owe.account_no.length; i++)
        {
            if (owe.account_no[i] == null)
            {
                continue;
            }

            Owe.add("Loan No            :  "+owe.account_no[i]+"\nLoan Amount :  "+owe.amount[i]+"\nInterstate        :  "+owe.interestrate[i]+"\nOutstanding   :  "+owe.startdate[i]+"\nDue Period      :  "+owe.maturitydate[i]+"\nTenture             :  "+owe.maturityamount[i]+"\nStart Date        :  "+owe.var7[i]);
        }

        List<String> Have = new ArrayList<>();
        for (int i = 0; i < have.account_no.length; i++)
        {
            if (have.account_no[i] == null)
            {
                continue;
            }
            Have.add("Account no               :   " + have.account_no[i] + "\nAmount                      :  " + have.amount[i] + "\nIntrest Rate              :   " + have.interestrate[i] + "\nStart Date                 :   " + have.startdate[i] + "\nMaturity Date         :    " + have.maturitydate[i] + "\nMaturity Amount  :  " + have.maturityamount[i]);
        }


        listHash.put(listDataHeader.get(0), Have);
        listHash.put(listDataHeader.get(1), Owe);
        listHash.put(listDataHeader.get(2), Dues);
        listHash.put(listDataHeader.get(4), Closed_Deposits);
        listHash.put(listDataHeader.get(3), Closed_Loans);
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
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                Toast.makeText(getApplicationContext(),listHash.get(listDataHeader.get(groupPosition)).get(childPosition),Toast.LENGTH_LONG);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        if(logoff())
                        {
                            dashboard.super.onBackPressed();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),server_msg,Toast.LENGTH_LONG).show();
                        }
                    }
                }).create().show();
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
        uid = findViewById(R.id.txt_userid);

        AppPref = PreferenceManager.getDefaultSharedPreferences(this);
        AppEdit = AppPref.edit();


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
