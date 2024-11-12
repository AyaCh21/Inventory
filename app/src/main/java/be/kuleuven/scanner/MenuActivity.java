package be.kuleuven.scanner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import be.kuleuven.scanner.databinding.ActivityMenuBinding;
import be.kuleuven.scanner.menu.ui.cart.CartFragment;
import be.kuleuven.scanner.menu.ui.item.ItemDetailFragment;
import be.kuleuven.scanner.menu.ui.login.LoginFragment;

import be.kuleuven.scanner.menu.ui.scan.ScanFragment;
import be.kuleuven.scanner.menu.ui.Signin.SignInFragment;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;
    private NavigationView navigationView;
    private int currentRole=-1;
    private DrawerLayout mDrawer;
    private String idCustomer;
    private String idCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMenu.toolbar);
        mDrawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_login, R.id.nav_signin, R.id.nav_cart, R.id.nav_scan, R.id.nav_logout)
                .setOpenableLayout(mDrawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        setRole(-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void setRole(int role){
        currentRole=role;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();

        if(role==1){//customer user
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_signin).setVisible(false);
            nav_Menu.findItem(R.id.nav_cart).setVisible(true);
            nav_Menu.findItem(R.id.nav_scan).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            setCurrentFragment(R.id.nav_cart);
        }else if(role==0){//admin user
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_signin).setVisible(false);
            nav_Menu.findItem(R.id.nav_cart).setVisible(false);
            nav_Menu.findItem(R.id.nav_scan).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            setCurrentFragment(R.id.nav_cart);
        }else if(role==-1){//simple user
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_signin).setVisible(true);
            nav_Menu.findItem(R.id.nav_cart).setVisible(false);
            nav_Menu.findItem(R.id.nav_scan).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            setCurrentFragment(R.id.nav_login);
        }

    }

    public void sign(){
        Toast.makeText(this, "sign", Toast.LENGTH_SHORT).show();
        setCurrentFragment(R.id.nav_signin);
    }

    public void scan(String idProduct){
        getIntent().putExtra("idProduct", idProduct);
        getIntent().putExtra("idCustomer", idCustomer);
        getIntent().putExtra("idCart", idCart);
        Log.e("scan","********* idCustomer="+idCustomer+"idCart="+idCart+"idProduct="+idProduct);
        setCurrentFragment(R.id.ItemDetaillblProduct);
    }

    public void backToScan(){
        Toast.makeText(this, "back to scan", Toast.LENGTH_SHORT).show();
        setCurrentFragment(R.id.nav_cart);
    }

    public void putData(String id_Cust,String id_Cart){
        idCart=id_Cart;
        idCustomer=id_Cust;
        getIntent().putExtra("idCustomer", id_Cust);
        getIntent().putExtra("idCart", id_Cart);
        Log.e("menuAct","********* idCustomer="+idCustomer+"idCart="+idCart);
    }
    public void signOk(){
        Toast.makeText(this, "you can login to your account after activation", Toast.LENGTH_SHORT).show();
        setCurrentFragment(R.id.nav_cart);
    }

    public void logout(){
        Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
        setCurrentFragment(R.id.nav_logout);
    }

    @Override
    public boolean onSupportNavigateUp() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setCurrentFragment(int id) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        String fragTitle=null;
        switch(id) {
            case R.id.nav_signin:
                fragmentClass = SignInFragment.class;
                fragTitle="Sign In";
                break;
            case R.id.nav_cart:
                fragmentClass = CartFragment.class;
                fragTitle="Cart";
                break;
            case R.id.nav_scan:
                fragmentClass = ScanFragment.class;
                fragTitle="Scan Product";
                break;

            case R.id.ItemDetaillblProduct:
                fragmentClass = ItemDetailFragment.class;
                fragTitle="Item Detail";
                Log.e("frag Item Detail","*********");
                break;
            case R.id.nav_logout:
                fragmentClass = LoginFragment.class;
                fragTitle="Login";
                break;
            default:
                fragmentClass = LoginFragment.class;
                fragTitle="Login";
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_menu, fragment)
                .addToBackStack(null)
                .commit();
        // Set action bar title
        ((AppCompatActivity) this).getSupportActionBar().setTitle(fragTitle);
        //setTitle(fragTitle);
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
}