package be.kuleuven.scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.view.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SurfaceView svCam;
    private TextView lblBarcode;
    private String code;
    private Button btnScan;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private static final int REQUEST_CAMERA_PERMISSION = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        svCam = (SurfaceView) findViewById(R.id.svCam);
        lblBarcode = (TextView) findViewById(R.id.lblBarcode);
        btnScan = (Button) findViewById(R.id.btnScan);

        startScan();
    }

    private void startScan(){

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        svCam.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(svCam.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    lblBarcode.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                lblBarcode.removeCallbacks(null);
                                code = barcodes.valueAt(0).email.address;
                                //lblBarcode.setText(code);
                            } else {
                                code = barcodes.valueAt(0).displayValue;
                                //lblBarcode.setText(code);
                            }
                            lblBarcode.setText(code);
                        }
                    });

                }
            }
        });
    }

    public void scanTapped (View caller){
        if (code != null ) {
            Intent intent = new Intent(this, ItemDetails.class);
            intent.putExtra("idValue", code);
            startActivity(intent);
        }
    }

    public void orderTapped (View caller){
        Intent intent = new Intent(this, OrderList.class);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        startScan();
    }



}



