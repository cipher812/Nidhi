package com.cipher.nidhi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity
{
    Button btn_login;
    TextView new_user;
    EditText user_name,password;

    private void login()
    {
        String username,pass;
        username=user_name.getText().toString();
        pass=password.getText().toString();
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //will hide the title
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login=findViewById(R.id.btn_login);
        new_user=findViewById(R.id.signup);
        user_name=findViewById(R.id.txt_login);
        password=findViewById(R.id.txt_pass);
    }

    public  void onStart()
    {
        super.onStart();

        Toast toast=Toast.makeText(getApplicationContext(),"Check Connection",Toast.LENGTH_LONG);
        if(!isOnline())
        {
            toast.show();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                login();
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
