package be.kuleuven.scanner.menu.ui.item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import be.kuleuven.scanner.MenuActivity;
import be.kuleuven.scanner.databinding.FragmentItemDetailBinding;

public class ItemDetailFragment extends Fragment {

    private RequestQueue requestQueue;
    private static final String getItemURL = "https://studev.groept.be/api/a21pt304/itemID/";
    private static final String addProductURL = "https://studev.groept.be/api/a21pt304/newCartOrder/";
    private static final String GET_IMAGE_URL = "https://studev.groept.be/api/a21pt325/getImageById/";
    private JSONObject dataObject;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private TextView lblProduct;
    private EditText editText;
    private TextView lblPrice;
    private TextView lblID;
    private Button btnback;
    private Button btnadd;
    private ImageView productImage;

    private String idProduct;
    private String idCustomer;
    private String idCart;
    private int stockcount;


    private FragmentItemDetailBinding binding;

    public static ItemDetailFragment newInstance() {
        return new ItemDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ItemDetailViewModel previousViewModel =
                new ViewModelProvider(this).get(ItemDetailViewModel.class);

        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lblProduct = binding.ItemDetaillblProduct;
        editText = binding.quantity;
        lblPrice = binding.lblPrice;
        lblID = binding.lblID;
        btnback = binding.btnback;
        btnadd = binding.btnadd;
        productImage=binding.imgProduct;

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null && extras.containsKey("idCustomer")
                && extras.containsKey("idProduct")
                && extras.containsKey("idCart")) {
            idProduct = extras.get("idProduct").toString();
            idCustomer = extras.get("idCustomer").toString();
            idCart = extras.get("idCart").toString();
        } else {
            idProduct = null;
            idCustomer = null;
            idCart = null;
        }
        Log.e("In FragmentItemDetail", "eeeeeeeeeeeeeee  idProduct=" + idProduct
                + " idCustomer=" + idCustomer + " idCart=" + idCart);
        String dataURL = getItemURL + idProduct;
        lblID.setText(idProduct);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message("Item added to cart");
                try {
                    int q = Integer.parseInt(editText.getText().toString());
                    if (q <= stockcount) {
                        addOrder(idCart, idProduct, "" + q);
                    } else {
                        message("Not enough in stock");
                        editText.requestFocus();
                    }
                } catch (NumberFormatException e) {
                    message("incorrect quantity value");
                    editText.requestFocus();
                }
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //message("btnback");
                ((MenuActivity) getActivity()).backToScan();
            }
        });

        JsonArrayRequest getData = new JsonArrayRequest(Request.Method.GET, dataURL,
                null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            dataObject = response.getJSONObject(0);
                            String name = dataObject.getString("name");
                            Double price = Double.parseDouble(dataObject.getString("price"));
                            int discount = (dataObject.getInt("discount"));
                            String imageId = dataObject.getString("idimage");
                            stockcount = dataObject.getInt("stock");
                            lblProduct.setText("ID: " + name);
                            lblPrice.setText("Price: €" + df.format(price*((100 - discount)/100)));
                            editText.setText("1");
                            retrieveImage(imageId);
                        } catch (JSONException e) {
                            message(e.getMessage());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(getData);
        return root;
    }

    private void addOrder(String id_cart, String id_product, String quant) {
        String url2 = addProductURL + id_cart + "/" + id_product + "/" + quant;
        Log.e("addOrder Item Detail", url2);
        JsonArrayRequest addProductRequest = new JsonArrayRequest(Request.Method.GET,
                url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("addOrder Item Detail", "OKKKK");
                //((MenuActivity) getActivity()).backToScan();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("addOrder Item Detail", "NOOOO");
                        message("Connection to server failed, Please try later...");
                    }
                }
        );
        requestQueue.add(addProductRequest);
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
                                productImage.setImageBitmap(bitmap2);

                                //Just a double-check to tell us the request has completed
                                //message("Image retrieved from DB");
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void message(String msg) {
        Toast.makeText(((MenuActivity) getActivity()), msg, Toast.LENGTH_SHORT).show();
    }
}