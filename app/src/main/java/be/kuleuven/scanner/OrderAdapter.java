package be.kuleuven.scanner;

import android.content.Context;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView productName;
            public TextView orderQuantity;
            public TextView supplierName;
            public TextView totalCost;
            public Button recieveOrder;

            public ViewHolder(View itemView) {
                super(itemView);

                productName = (TextView) itemView.findViewById(R.id.lblProd);
                orderQuantity = (TextView) itemView.findViewById(R.id.lblQty);
                supplierName = (TextView) itemView.findViewById(R.id.lblSup);
                totalCost = (TextView) itemView.findViewById(R.id.lblCost);
                recieveOrder = (Button) itemView.findViewById(R.id.btnRecieved);
            }
        }

    private RequestQueue requestQueue;
    private JSONObject dataObject;

    private ArrayList<Orders> mOrder;


    public OrderAdapter(ArrayList<Orders> orders) {
        mOrder = orders;
    }


    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.order_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        Orders order = mOrder.get(position);

        TextView tvName = holder.productName;
        tvName.setText(order.getProduct());
        TextView tvQty = holder.orderQuantity;
        tvQty.setText("Qty: " + order.getQuantity());
        TextView tvSupplier = holder.supplierName;
        tvSupplier.setText("Supplier: " + order.getSupplier());
        TextView tvCost = holder.totalCost;
        tvCost.setText("Total Cost: €" + order.getCost());
        Button buttRecieved = holder.recieveOrder;


        buttRecieved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQueue = Volley.newRequestQueue(buttRecieved.getContext());
                JsonArrayRequest sendData = new JsonArrayRequest(Request.Method.GET, "https://studev.groept.be/api/a21pt304/updateStatus/" + order.getId(), null,

                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                JsonArrayRequest sendData2 = new JsonArrayRequest(Request.Method.GET, "https://studev.groept.be/api/a21pt304/RecieveStock/" + order.getQuantity() +"/"+order.getQuantity()+"/"+ order.getProduct(), null,

                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });



                requestQueue.add(sendData);
                requestQueue.add(sendData2);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mOrder.size();
    }

}



