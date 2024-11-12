package be.kuleuven.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CustomListAdapter  extends BaseAdapter {
    private List<Product> listData;
    private LayoutInflater layoutInflater;
    private Context context;
    private RequestQueue requestQueue;
    private static final String GET_IMAGE_URL = "https://studev.groept.be/api/a21pt325/getImageById/";
    private JSONObject dataObject;

    public CustomListAdapter(Context aContext,  List<Product> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        requestQueue = Volley.newRequestQueue(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.pimage = (ImageView) convertView.findViewById(R.id.pimage);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.description = (TextView) convertView.findViewById(R.id.desc);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.quantity = (TextView) convertView.findViewById(R.id.quant);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product_i = this.listData.get(position);
        holder.name.setText(product_i.getName());
        holder.description.setText( product_i.getDescription());
        holder.price.setText(""+(int)product_i.getPrice());
        holder.quantity.setText(""+ (int)product_i.getQuantity());
        //holder.pimage=?
        retrieveImage(holder.pimage, product_i.getIdImage());

        //int imageId = this.getMipmapResIdByName(product_i.getIdImage());

        //holder.pimage.setImageResource(imageId);

        return convertView;
    }

    // Find Image ID corresponding to the name of the image (in the directory mipmap).
    public int getMipmapResIdByName(String resName)  {
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName , "mipmap", pkgName);
        Log.i("CustomListView", "Res Name: "+ resName+"==> Res ID = "+ resID);
        return resID;
    }

    static class ViewHolder {
        ImageView pimage;
        TextView name;
        TextView description;
        TextView price;
        TextView quantity;
    }
    public void retrieveImage(ImageView iv,String imageId) {
        Log.e("retrieveImage Item Detail", " ***********   requested =>"+imageId);
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
                                iv.setImageBitmap(bitmap2);

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
    private void message(String msg) {
        Log.e("CustomListAdapter",msg);
    }
}