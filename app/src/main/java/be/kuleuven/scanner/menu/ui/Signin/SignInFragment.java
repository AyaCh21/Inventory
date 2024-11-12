package be.kuleuven.scanner.menu.ui.Signin;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import be.kuleuven.scanner.MenuActivity;
import be.kuleuven.scanner.databinding.FragmentSignInBinding;

public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;

    Button newAccount;
    EditText name, email, password1, password2;
    private RequestQueue requestQueue;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SignInViewModel scanViewModel =
                new ViewModelProvider(this).get(SignInViewModel.class);

        binding = FragmentSignInBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        newAccount = binding.newAccount;
        name = binding.nameTxt;
        email = binding.emailTxt;
        password1 = binding.password1Txt;
        password2 = binding.password2Txt;
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1, pass2, emailStr, nameStr;
                nameStr = name.getText().toString();
                emailStr = email.getText().toString();
                pass1 = password1.getText().toString();
                pass2 = password2.getText().toString();
                if (nameStr.equals("")) {
                    name.requestFocus();
                    message("Please set the user name!");
                } else if (emailStr.equals("")) {
                    email.requestFocus();
                    message("Please set the login email!");
                } else if (pass1.equals("")) {
                    password1.requestFocus();
                    message("Please set your password!");
                } else if (pass2.equals("")) {
                    password2.requestFocus();
                    message("Please set your confirmation password!");
                } else if (!pass1.equals(pass2)) {
                    password2.requestFocus();
                    message("Please passwords must match!");
                } else {
                    message("Connection to server, Please wait...");

                    requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext() );
                    String requestInsertURL = "https://studev.groept.be/api/a21pt304/newUser/" + nameStr + "/" + emailStr + "/" + pass1;

                    JsonArrayRequest submitInsertRequest = new JsonArrayRequest(Request.Method.GET, requestInsertURL, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if (response.length() == 0) {
                                message("Your account is created succesfully");
                                ((MenuActivity)getActivity()).signOk();
                                //Intent intent = new Intent(v.getContext(), MainActivity.class);
                                //v.getContext().startActivity(intent);
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



                    String requestURL = "https://studev.groept.be/api/a21pt304/userName/" + emailStr;

                    JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if (response.length() == 0) {
                                requestQueue.add(submitInsertRequest);
                            } else {
                                message("the username already exists, please change it");
                                email.requestFocus();
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
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void message(String msg){
        Toast.makeText(((MenuActivity)getActivity()), msg, Toast.LENGTH_SHORT).show();
    }


}