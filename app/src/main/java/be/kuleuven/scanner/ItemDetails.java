package be.kuleuven.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ItemDetails extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String URL = "https://studev.groept.be/api/a21pt304/itemID/";
    private static final String GET_IMAGE_URL = "https://studev.groept.be/api/a21pt325/getImageById/";
    private JSONObject dataObject;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private TextView lblProduct;
    private TextView lblStock;
    private TextView lblPrice;
    private TextView lblID;
    private ImageView imgProduct;

    private String code;
    private String product;
    private String stock;
    private String price;
    private String ordered;
    private String discount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        lblProduct = (TextView) findViewById(R.id.lblProduct);;
        lblStock = (TextView) findViewById(R.id.lblStock);;
        lblPrice = (TextView) findViewById(R.id.lblPrice);;
        lblID = (TextView) findViewById(R.id.lblID);;
        imgProduct=(ImageView) findViewById(R.id.imgProduct);

        Bundle extras = getIntent().getExtras();
        code = extras.get("idValue").toString();
        String dataURL= URL + code;
        requestQueue = Volley.newRequestQueue(this);
        lblID.setText(extras.get("idValue").toString());







        JsonArrayRequest getData = new JsonArrayRequest(Request.Method.GET, dataURL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                dataObject = response.getJSONObject(i);

                                product = dataObject.getString("name");
                                stock = dataObject.getString("stock");
                                price = dataObject.getString("price");
                                ordered = dataObject.getString("ordered");
                                discount = dataObject.getString("discount");
                                String imageId = dataObject.getString("idimage");


                                lblProduct.setText("ID: " + product);
                                lblPrice.setText("Price: €" + df.format(Float.parseFloat(price)*((100 - Float.parseFloat(discount))/100)));
                                lblStock.setText("Stock: " + stock);
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

    }

    public void restockTapped (View caller){
        Intent intent = new Intent(this, Restock.class);
        intent.putExtra("idValue", code);
        intent.putExtra("productName", product);
        intent.putExtra("productStock", stock);
        intent.putExtra("amountOrdered", ordered);
        startActivity(intent);
        }


    public void modifyTapped (View caller){
        Intent intent = new Intent(this, ModifyDetails.class);
        intent.putExtra("idValue", code);
        intent.putExtra("productPrice", price);
        intent.putExtra("productName", product);
        intent.putExtra("productStock", stock);
        intent.putExtra("amountOrdered", ordered);
        intent.putExtra("discountValue", discount);
        startActivity(intent);
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


