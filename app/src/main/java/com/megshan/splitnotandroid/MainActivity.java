package com.megshan.splitnotandroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megshan.splitnotandroid.dto.Item;
import com.megshan.splitnotandroid.utils.RequestQueueUtil;
import com.plaid.splitnotandroid.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String LOGGER = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a request queue
        RequestQueue requestQueue =
                RequestQueueUtil.getInstance(this.getApplicationContext()).getRequestQueue();

        // Create request
        String url = "http://10.0.2.2:8080/items?userKey=123";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null,
                        (response -> {
                            Log.i(LOGGER, response.toString());
                            List<Item> itemList = new Gson().fromJson(response.toString(),
                                    new TypeToken<List<Item>>() {}.getType());
                            List<String> itemNames = new ArrayList<>(itemList.size());
                            for(Item item : itemList) {
                                itemNames.add(item.getItemName());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_list_item_1, itemNames);
                            setListAdapter(adapter);
                        }),
                        (error -> Log.e(LOGGER, "error fetching data from splitnot-api, error=" + error))
                );

        // Add request to queue
        RequestQueueUtil.getInstance(this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
}
