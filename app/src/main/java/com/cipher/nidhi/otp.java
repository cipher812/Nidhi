package com.cipher.nidhi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;

public class otp extends AppCompatActivity
{
    private PinEntryEditText pinEntry;

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

                    }
                    else
                    {

                    }
                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
