package be.kuleuven.scanner.menu.ui.scan;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import be.kuleuven.scanner.MenuActivity;
import be.kuleuven.scanner.R;
import be.kuleuven.scanner.databinding.FragmentScanBinding;

public class ScanFragment extends Fragment {
    private SurfaceView svCam;
    private TextView lblBarcode;
    private String idProduct;
    private Button btnScan;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private FragmentScanBinding binding;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel scanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        svCam = binding.svCam;
        lblBarcode = binding.lblBarcode;
        btnScan = binding.btnScan;
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanTapped();
            }
        });
        startScan();
        return root;
    }
    private void startScan() {

        barcodeDetector = new BarcodeDetector.Builder(getActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(getActivity().getApplicationContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        svCam.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(svCam.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new
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
                                idProduct = barcodes.valueAt(0).email.address;
                                lblBarcode.setText(idProduct);
                            } else {
                                idProduct = barcodes.valueAt(0).displayValue;
                                lblBarcode.setText(idProduct);
                            }
                            lblBarcode.setText(idProduct);
                        }
                    });

                }
            }
        });
    }

    public void scanTapped() {
        //idProduct ="3068320124537";
        //idProduct ="5410013101703";
        //idProduct ="5449000111678";
        if (idProduct != null) {
            if(cameraSource!=null){
                cameraSource.release();
                //cameraSource.stop();
            }
            ((MenuActivity)getActivity()).scan(idProduct);
            //getActivity().onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //getSupportActionBar().hide();
       // cameraSource.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        //getSupportActionBar().hide();
        startScan();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //if(cameraSource!=null){
            //cameraSource.release();
            //cameraSource.stop();
       // }
    }
}