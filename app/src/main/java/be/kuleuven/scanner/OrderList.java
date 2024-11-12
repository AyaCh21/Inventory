package be.kuleuven.scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderList extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String URL = "https://studev.groept.be/api/a21pt304/getOrders";
    private JSONObject dataObject;
    ArrayList<Orders> ordersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        RecyclerView rvOrder = (RecyclerView) findViewById(R.id.rvOrderlist);
        ordersArrayList = new ArrayList<>();

        // Initialize orders
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest getData = new JsonArrayRequest(Request.Method.GET, URL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                dataObject = response.getJSONObject(i);
                                if (dataObject.getString("status").equals("0")) {
                                    Orders newOrder = new Orders(dataObject.getString("idRestockOrders"),dataObject.getString("productName"), dataObject.getString("quantity"), dataObject.getString("supplier"), dataObject.getString("totalCost"));
                                    Log.i("OrderList:", "getting data: " + newOrder);
                                    ordersArrayList.add(newOrder);
                                    Log.i("OrderList:", "getting array data: " + ordersArrayList);
                                }

                            }
                        } catch (JSONException e) {
                            Log.e("Database", e.getMessage(), e);
                        }

                        OrderAdapter adapter = new OrderAdapter(ordersArrayList);
                        rvOrder.setAdapter(adapter);
                        rvOrder.setLayoutManager(new LinearLayoutManager(OrderList.this));

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(getData);



    }

}