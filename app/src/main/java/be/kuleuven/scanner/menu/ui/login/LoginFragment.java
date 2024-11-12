package be.kuleuven.scanner.menu.ui.login;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import be.kuleuven.scanner.MainActivity;
import be.kuleuven.scanner.MenuActivity;
import be.kuleuven.scanner.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private Button login;
    private EditText username, password;
    private TextView signin;
    private RequestQueue requestQueue;
    private String cartId, userId;
    private int role,status;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //((MenuActivity)getActivity()).printt();
        LoginViewModel loginViewModel =
                new ViewModelProvider(this).get(LoginViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        return true;
                    }
                }
                return false;
            }
        });



        login = binding.newAccount;
        username = binding.username;
        password = binding.password;
        signin = binding.signin;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass, user;
                pass = password.getText().toString();
                user = username.getText().toString();
                if (pass.equals("") || user.equals("")) {
                    message("Please set the user name and the password first");
                } else {
                    message("Connection to server, Please wait...");
                    requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    String requestURL = "https://studev.groept.be/api/a21pt304/userNamePass/" + user + "/" + pass;
                    JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    if (response.length() == 1) {
                                        userId = null;
                                        role = 0;
                                        status = 0;
                                        try {
                                            JSONObject curObject = response.getJSONObject(0);
                                            //String name = curObject.getString("Name");
                                            userId = curObject.getString("Id");
                                            role = curObject.getInt("Role");
                                            status = curObject.getInt("Status");
                                        } catch (JSONException e) {
                                            Log.e("Database", e.getMessage(), e);
                                        }
                                        if (userId != null) {

                                                final String requestCartURL = "https://studev.groept.be/api/a21pt304/customerCart/" + userId;
                                                final String cartInsertURL = "https://studev.groept.be/api/a21pt304/newCustomerCart/" + userId;
                                                JsonArrayRequest submitCartRequest = new JsonArrayRequest(Request.Method.GET, requestCartURL
                                                        , null, new Response.Listener<JSONArray>() {
                                                    @Override
                                                    public void onResponse(JSONArray response) {
                                                        if (response.length() == 0) {
                                                            JsonArrayRequest cartInsertRequest = new JsonArrayRequest(Request.Method.GET, cartInsertURL, null, new Response.Listener<JSONArray>() {
                                                                @Override
                                                                public void onResponse(JSONArray response) {
                                                                    message("********* reception insertnewCustomerCart");
                                                                    Log.e("cart", "********* reception insertnewCustomerCart");
                                                                    JsonArrayRequest cartLastRequest = new JsonArrayRequest(Request.Method.GET, requestCartURL, null,
                                                                            new Response.Listener<JSONArray>() {
                                                                                @Override
                                                                                public void onResponse(JSONArray response) {
                                                                                    try {
                                                                                        JSONObject curObject = response.getJSONObject(0);
                                                                                        cartId = curObject.getString("idcart");
                                                                                        if(role==1) {
                                                                                            ((MenuActivity) getActivity()).putData(userId, cartId);
                                                                                            ((MenuActivity) getActivity()).setRole(role);
                                                                                        }else{
                                                                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                                            startActivity(intent);
                                                                                        }
                                                                                    } catch (JSONException e) {
                                                                                        message(e.getMessage());
                                                                                        Log.e("login form", "********* error cartLastRequest\n"+e.getMessage());
                                                                                    }

                                                                                }
                                                                            },
                                                                            new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {
                                                                                    message("Connection to server failed, Please try later...");
                                                                                }
                                                                            }
                                                                    );
                                                                    requestQueue.add(cartLastRequest);
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
                                                        }
                                                        else {
                                                            try {
                                                                JSONObject curObject = response.getJSONObject(0);
                                                                cartId = curObject.getString("idcart");
                                                                if(role==1) {
                                                                    ((MenuActivity) getActivity()).putData(userId, cartId);
                                                                    ((MenuActivity) getActivity()).setRole(role);
                                                                }else{
                                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            } catch (JSONException e) {
                                                                message(e.getMessage());
                                                            }
                                                        }

                                                    }
                                                },

                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                message("Connection to server failed, Please try later...");
                                                            }
                                                        }
                                                );
                                                requestQueue.add(submitCartRequest);
                                                //endActivity();

                                        }
                                    } else {
                                        message("Incorrect username or password");
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    message("Connection to server failed, Please try later...");
                                }
                            }
                    );
                    requestQueue.add(submitRequest);
                }
            }

        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MenuActivity) getActivity()).sign();
                //Intent intent = new Intent(view.getContext(), Sign.class);
                //view.getContext().startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void message(String msg) {
        Toast.makeText(((MenuActivity) getActivity()), msg, Toast.LENGTH_SHORT).show();
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }


}