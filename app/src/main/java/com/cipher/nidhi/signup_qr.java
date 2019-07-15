package com.cipher.nidhi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


public class signup_qr extends AppCompatActivity
{
    SurfaceView sv;
    CameraSource cs;
    TextView msg;
    BarcodeDetector barcodeDetector;

    private void signup_api(String code)
    {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute("signup",code);
    }

    //============================================================================================//

    private void  getqr_code()
    {
        barcodeDetector=new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cs=new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(640,480).build();

        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                try
                {
                    cs.start(holder);
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                cs.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release()
            {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
                final SparseArray<Barcode> code=detections.getDetectedItems();
                if(code.size()!=0)
                {
                    msg.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            String scode=code.valueAt(0).displayValue;
                            msg.setText(scode);
                            signup_api(scode);
                        }
                    });
                }
            }
        });
    }

    //===========================================================================================//

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);              //will hide the title
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_qr);

        msg=findViewById(R.id.txt_msg);
        sv=findViewById(R.id.camera_prev);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getqr_code();
    }
}
