package be.kuleuven.scanner.menu.ui.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.scanner.CustomListAdapter;
import be.kuleuven.scanner.MenuActivity;
import be.kuleuven.scanner.Product;
import be.kuleuven.scanner.Restock;
import be.kuleuven.scanner.databinding.FragmentCartBinding;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private String customerId, cartId;
    private ArrayList<Integer> quantity;
    private ArrayList<String> prodName;
    private ArrayList<String> uniqueProduct;
    private RequestQueue requestQueue;
    private JSONObject dataObject;
    private ListView listView;
    private Button btnPay;
    private List<Product> cartProductsList = new ArrayList<Product>();
    private String idcart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CartViewModel cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        quantity = new ArrayList<>();
        prodName = new ArrayList<>();
        uniqueProduct = new ArrayList<>();

        btnPay = binding.pay;
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPay();
            }
        });

        listView = binding.listView;
        listView.setAdapter(new CustomListAdapter(getActivity().getApplicationContext(), cartProductsList));
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null && extras.containsKey("idCustomer")) {
            customerId = extras.get("idCustomer").toString();
            cartId = extras.get("idCart").toString();
            fillCart();
        } else {
            customerId = null;
            cartId = null;
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Product country = (Product) o;
                Toast.makeText(((MenuActivity) getActivity()), "Selected :" + " " + country, Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }

    private void fillCart() {
        if (requestQueue != null) {
            String cartProductsURL = "https://studev.groept.be/api/a21pt304/customerCartProducts/"
                    + cartId;
            JsonArrayRequest cartInsertRequest = new JsonArrayRequest(Request.Method.GET,
                    cartProductsURL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    cartProductsList.clear();
                    Log.e("fillCart","*********  OK taille = "+response.length());
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            dataObject = response.getJSONObject(i);
                            int status = dataObject.getInt("status");
                            idcart = dataObject.getString("idcart");

                            if(dataObject.getInt("status") == 0) {
                            String name = dataObject.getString("name");
                            String desc = dataObject.getString("description");
                            Double price = dataObject.getDouble("price");
                            Log.i("cart:", "price = " +price);
                            Double qty = dataObject.getDouble("quantity");
                            String idimg = dataObject.getString("idimage");

                            Product p = new Product(idcart, name, desc, price, qty, idimg);


                                cartProductsList.add(p);
                                message(p.toString());


                                quantity.add(Integer.parseInt(dataObject.getString("quantity")));
                                prodName.add(name);
                                if (!uniqueProduct.contains(name)) {
                                    uniqueProduct.add(name);
                                }
                            }

                        } catch (JSONException e) {
                            Log.e("fillCart", "*********  JSONException");
                        }
                    }
                    listView.invalidateViews();
                }
            },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            message("Connection to server failed, Please try later...");
                        }
                    }
            );
            requestQueue.add(cartInsertRequest);
        } else {
            Log.e("fillCart", "*********  idCustomer=" + customerId + "idCart=" + cartId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void message(String msg) {
        Toast.makeText(((MenuActivity) getActivity()), msg, Toast.LENGTH_SHORT).show();
    }

    public void onClickPay() {
        cartProductsList.clear();
        int total = 0;
        for (String e : uniqueProduct) {
            total = 0;
            for (int i = 0; i < prodName.size(); i++) {
                if (prodName.get(i).equals(e)){
                    total += quantity.get(i);
                }
            }
            //decrease stock volley

            StringRequest updateStock = new StringRequest(Request.Method.GET, "https://studev.groept.be/api/a21pt304/decreaseStock/" + total + "/" + e,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            StringRequest updateStatus = new StringRequest(Request.Method.GET, "https://studev.groept.be/api/a21pt304/updateStatusCart/" + idcart,

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

                            requestQueue.add(updateStatus);

                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            requestQueue.add(updateStock);

        }
        Toast.makeText(((MenuActivity) getActivity()),"Transaction Complete",Toast.LENGTH_SHORT).show();
        //clear cart here
        cartProductsList.clear();

    }

}


