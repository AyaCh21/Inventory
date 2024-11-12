package be.kuleuven.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Restock extends AppCompatActivity {

    private RequestQueue requestQueue1;
    private RequestQueue requestQueue2;
    private static final String URL = "https://studev.groept.be/api/a21pt304/updateOrder/";
    private static final String URL2 = "https://studev.groept.be/api/a21pt304/getSupplier/";
    private JSONObject dataObject;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private TextView lblProduct2;
    private TextView lblStock2;
    private TextView lblRestockVal;
    private TextView lblID2;
    private TextView lblOrdered;
    private Spinner spinner;

    private int quantity;
    private int totalQty;
    private String code;
    private String name;
    private String supplyCo;
    private float cost;
    private String finalcost;
    private ArrayList<String> suppliers;
    private ArrayList<String> suppliers2;
    private ArrayList<String> bulkPrice;
    boolean check;
    private int pos;
    private Orders newOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restock);

        lblProduct2 = (TextView) findViewById(R.id.lblProduct2);
        lblStock2 = (TextView) findViewById(R.id.lblStock2);
        lblID2 = (TextView) findViewById(R.id.lblID2);
        lblRestockVal = (TextView) findViewById(R.id.lblRestockVal);
        lblOrdered = (TextView) findViewById(R.id.lblOrdered);
        spinner = (Spinner) findViewById(R.id.spSupplier);

        Bundle extras = getIntent().getExtras();
        code = extras.get("idValue").toString();
        name = extras.get("productName").toString();
        lblID2.setText("ID: " + code);
        lblProduct2.setText(name);
        lblStock2.setText("Stock: " + extras.get("productStock").toString());
        lblOrdered.setText("Amount ordered: " + extras.get("amountOrdered").toString());
        suppliers = new ArrayList<String>();
        bulkPrice = new ArrayList<>();
        finalcost = "0";
        check = false;

        totalQty = Integer.parseInt(extras.get("amountOrdered").toString());




        suppliers.add("Choose Supplier");
        requestQueue1 = Volley.newRequestQueue(this);


        JsonArrayRequest getData = new JsonArrayRequest(Request.Method.GET, URL2 + code, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                dataObject = response.getJSONObject(i);
                                suppliers.add(dataObject.getString("companyName"));
                                bulkPrice.add(dataObject.getString("sellPrice"));
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

        requestQueue1.add(getData);



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, suppliers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                check = true;
                supplyCo = item;
                pos = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void onBtnPlus_Clicked(View caller) {
        quantity = Integer.parseInt(lblRestockVal.getText().toString()) + 10;
        lblRestockVal.setText(Integer.toString(quantity));
    }

    public void onBtnMinus_Clicked(View caller) {
        quantity = Integer.parseInt(lblRestockVal.getText().toString());
        if (quantity == 0) {
            quantity = 0;
        } else {
            quantity = quantity - 10;
        }
        lblRestockVal.setText(Integer.toString(quantity));
    }

    public void onSubmit_Clicked(View caller) {

        totalQty = totalQty + quantity;
        String dataURL= URL + totalQty + "/" + code;


        Intent intent = new Intent(this, Restock.class);

        if (check && !supplyCo.equals("Choose Supplier")) {

            requestQueue2 = Volley.newRequestQueue(this);
            StringRequest sendData = new StringRequest(Request.Method.GET, dataURL,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            lblOrdered.setText("Amount ordered: " + (totalQty));
                            cost = Float.parseFloat(bulkPrice.get(pos-1));
                            finalcost = df.format(quantity*cost);
                            //newOrder = new Orders(name, Integer.toString(quantity), supplyCo, finalcost);

                            StringRequest sendOrder = new StringRequest(Request.Method.GET, "https://studev.groept.be/api/a21pt304/sendOrder/" + name + "/" + quantity +"/" + supplyCo + "/" + finalcost,

                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(Restock.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                    },

                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });

                            requestQueue2.add(sendOrder);

                        }

                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });


            requestQueue2.add(sendData);


        }
        else{
            Toast.makeText(this, "No Supplier Selected", Toast.LENGTH_SHORT).show();

        }


    }

}