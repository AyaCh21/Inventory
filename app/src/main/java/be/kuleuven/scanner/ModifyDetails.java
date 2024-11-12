package be.kuleuven.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModifyDetails extends AppCompatActivity {

    private RequestQueue requestPrice;
    private RequestQueue requestDisc;
    private RequestQueue requestQueue;
    private static final String URLprice = "https://studev.groept.be/api/a21pt304/priceUpdate/";
    private static final String URLdisc = "https://studev.groept.be/api/a21pt304/discountUpdate/";
    private static final String GET_IMAGE_URL = "https://studev.groept.be/api/a21pt325/getImageById/";
    private JSONObject dataObject;

    private TextView lblProduct;
    private TextView lblStock;
    private TextView lblPrice;
    private TextView lblID;
    private TextView lblOrder;
    private EditText priceVal;
    private EditText discount;
    private CheckBox cbDiscount;
    private ImageView imgProduct;

    private String code;
    private Editable price;
    private Editable disc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_details);

        lblProduct = (TextView) findViewById(R.id.lblProduct);
        lblStock = (TextView) findViewById(R.id.lblStock);
        lblID = (TextView) findViewById(R.id.lblID);
        lblPrice = (TextView) findViewById(R.id.lblPrice);
        lblOrder = (TextView) findViewById(R.id.lblOrder);
        priceVal = (EditText) findViewById(R.id.priceVal);
        discount = (EditText) findViewById(R.id.discount);
        cbDiscount = (CheckBox) findViewById(R.id.cbDiscount);
        imgProduct=(ImageView) findViewById(R.id.imgProduct);

        Bundle extras = getIntent().getExtras();
        code = extras.get("idValue").toString();
        lblID.setText("ID: " + code);
        lblProduct.setText(extras.get("productName").toString());
        lblStock.setText("Stock: " + extras.get("productStock").toString());
        lblOrder.setText("Amount ordered: " + extras.get("amountOrdered").toString());
        lblPrice.setText("Full Price: €");
        priceVal.setText(extras.get("productPrice").toString());
    }

    public void onSave_Clicked(View caller) {

        price = priceVal.getText();
        String dataURL= URLprice + price + "/" + code;
        requestPrice = Volley.newRequestQueue(this);


        StringRequest sendData = new StringRequest(Request.Method.GET, dataURL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });



        String dataURL2= URLdisc;

        if (cbDiscount.isChecked()){
            disc = discount.getText();
            dataURL2= dataURL2 + disc + "/" + code;
        }
        else{
            dataURL2= dataURL2 + 0 + "/" + code;
        }



        requestDisc = Volley.newRequestQueue(this);

        StringRequest sendData2 = new StringRequest(Request.Method.GET, dataURL2,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ModifyDetails.this, "Details Updated", Toast.LENGTH_SHORT).show();
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestPrice.add(sendData);
        requestDisc.add(sendData2);


        JsonArrayRequest getData = new JsonArrayRequest(Request.Method.GET, dataURL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                String imageId = dataObject.getString("idimage");
                                retrieveImage(imageId);
                            }
                        } catch (JSONException e) {
                            Log.e("Database", e.getMessage(), e);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(getData);
        finish();

    }


    public void retrieveImage(String imageId) {
        Log.i("retrieveImage Item Detail", " ***********   requested =>"+imageId);
        String url = GET_IMAGE_URL+imageId;
        JsonArrayRequest retrieveImageRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //Check if the DB actually contains an image
                            if (response.length() > 0) {
                                JSONObject o = response.getJSONObject(0);

                                //converting base64 string to image
                                String b64String = o.getString("image");
                                byte[] imageBytes = Base64.decode(b64String, Base64.DEFAULT);
                                Bitmap bitmap2 = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                //Link the bitmap to the ImageView, so it's visible on screen
                                imgProduct.setImageBitmap(bitmap2);

                                //Just a double-check to tell us the request has completed
                                message("Image retrieved from DB");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message("Unable to communicate with server");
                    }
                }
        );

        requestQueue.add(retrieveImageRequest);
    }
    private void message(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}