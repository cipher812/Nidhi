package com.cipher.nidhi;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundWorker extends AsyncTask <String,Void,Void>
{
    Context context;
    AlertDialog alertDialog;
    String result="";

    BackgroundWorker(Context ctx)
    {
        context=ctx;
    }

    private void login_api(String login_url,String userName,String password)
    {
        try
        {
            URL url = new URL(login_url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);
            OutputStream os = http.getOutputStream();
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            br.write(post_data);
            br.flush();
            br.close();
            os.close();
            InputStream inputStream = http.getInputStream();
            BufferedReader bw = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

            String line = "";

            while ((line = bw.readLine()) != null)
            {
                result += line;
            }
            bw.close();
            inputStream.close();
            http.disconnect();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //=======================================================================================//

    @Override
    protected Void doInBackground(String... strings)
    {
        String type= strings[0];
        String userName=strings[1];
        String password=strings[2];
        String login_url="";
        if(type.equals("login"))
        {
            login_api(login_url,userName,password);
        }
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        alertDialog.setMessage(result);
        alertDialog.show();
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }
}
