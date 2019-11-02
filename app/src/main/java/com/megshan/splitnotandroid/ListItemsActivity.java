package com.megshan.splitnotandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megshan.splitnotandroid.adapters.ItemsAdapter;
import com.megshan.splitnotandroid.dto.Item;
import com.megshan.splitnotandroid.utils.RequestQueueUtil;
import com.plaid.splitnotandroid.R;

import java.util.ArrayList;
import java.util.List;

public class ListItemsActivity extends AppCompatActivity {

    private static final String LOGGER = "ListItemsActivity";

    private RecyclerView recyclerView;
    private TextView itemsEmptyView;
    private RecyclerView.Adapter itemsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.items_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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
                            if(itemList.size() == 0) {
                                Log.i(LOGGER, "showing empty items text view");
                                itemsEmptyView = findViewById(R.id.items_empty_view);
                                itemsEmptyView.setVisibility(View.VISIBLE);
                            } else {
                                List<String> itemNames = new ArrayList<>(itemList.size());
                                for (Item item : itemList) {
                                    itemNames.add(item.getItemName());
                                }
                                itemsAdapter = new ItemsAdapter(itemNames.toArray(new String[itemNames.size()]));
                                recyclerView.setAdapter(itemsAdapter);
                            }
                        }),
                        (error -> Log.e(LOGGER, "error fetching data from splitnot-api, error=" + error))
                );

        // Add request to queue
        RequestQueueUtil.getInstance(this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
}
