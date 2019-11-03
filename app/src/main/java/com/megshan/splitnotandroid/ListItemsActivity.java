package com.megshan.splitnotandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megshan.splitnotandroid.adapters.ItemsAdapter;
import com.megshan.splitnotandroid.dto.Item;
import com.megshan.splitnotandroid.utils.RequestQueueUtil;
import com.plaid.link.Plaid;
import com.plaid.linkbase.models.LinkCancellation;
import com.plaid.linkbase.models.LinkConfiguration;
import com.plaid.linkbase.models.LinkConnection;
import com.plaid.linkbase.models.LinkConnectionMetadata;
import com.plaid.linkbase.models.LinkEventListener;
import com.plaid.linkbase.models.PlaidApiError;
import com.plaid.linkbase.models.PlaidProduct;
import com.plaid.splitnotandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

public class ListItemsActivity extends AppCompatActivity {

    private static final String LOGGER = "ListItemsActivity";
    private static final int LINK_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private TextView itemsEmptyView;
    private RecyclerView.Adapter itemsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;
    private List<Item> itemList;

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

        // Link add account button
        fab = findViewById(R.id.open_link_fab);
        fab.setOnClickListener(view -> {
            Plaid.setLinkEventListener(new LinkEventListener(it -> {
                Log.i("Event", it.toString());
                return Unit.INSTANCE;
            }));
            ArrayList<PlaidProduct> products = new ArrayList<>();
            products.add(PlaidProduct.TRANSACTIONS);
            Plaid.openLink(
                    ListItemsActivity.this,
                    new LinkConfiguration.Builder("Splitnot", products).build(),
                    LINK_REQUEST_CODE);
        });

        // Create request
        String url = "http://10.0.2.2:8080/items?userKey=123";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null,
                        (response -> {
                            Log.i(LOGGER, response.toString());
                            itemList = new Gson().fromJson(response.toString(),
                                    new TypeToken<List<Item>>() {}.getType());
                            if(itemList.size() == 0) {
                                Log.i(LOGGER, "showing empty items text view");
                                itemsEmptyView = findViewById(R.id.items_empty_view);
                                itemsEmptyView.setVisibility(View.VISIBLE);
                            } else {
                                itemsAdapter = new ItemsAdapter(itemList);
                                recyclerView.setAdapter(itemsAdapter);
                            }
                        }),
                        (error -> Log.e(LOGGER, "error fetching data from splitnot-api, error=" + error))
                );

        // Add request to queue
        RequestQueueUtil.getInstance(this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LINK_REQUEST_CODE && data != null) {
            if (resultCode == Plaid.RESULT_SUCCESS) {
                LinkConnection item = (LinkConnection) data.getSerializableExtra(Plaid.LINK_RESULT);
                if (item != null) {

                    LinkConnectionMetadata metadata = item.getLinkConnectionMetadata();
                    Log.i(LOGGER, " successfully fetched item, " +
                            item.getPublicToken() + " " +
                            metadata.getAccounts() + " " +
                            metadata.getInstitutionId() + " " +
                            metadata.getInstitutionName());

                    // create new item
                    Item newItem = new Item();
                    newItem.setItemId("item125");
                    newItem.setUserKey("123");
                    newItem.setItemName(metadata.getInstitutionName() + " "
                            +  metadata.getAccounts().get(0).getAccountName());
                    newItem.setPublicToken(item.getPublicToken());

                    // add new item to list and update the list
                    itemList.add(newItem);
                    if(itemsAdapter == null) {
                        itemsAdapter = new ItemsAdapter(itemList);
                        recyclerView.setAdapter(itemsAdapter);
                        itemsEmptyView.setVisibility(View.INVISIBLE);
                    }
                    itemsAdapter.notifyItemInserted(itemList.size()-1);

                    // Save item to DB
                    String url = "http://10.0.2.2:8080/items";
                    JsonObjectRequest jsonArrayRequest;
                    try {
                        jsonArrayRequest = new JsonObjectRequest
                                (Request.Method.POST, url, new JSONObject(new Gson().toJson(newItem)),
                                        (response -> {
                                            Log.i(LOGGER, response.toString());
                                        }),
                                        (error -> Log.e(LOGGER,
                                                "error saving data to splitnot-api, error=" + error))
                                );
                    } catch (JSONException j) {
                        Log.e(LOGGER, "error creating JSONObject");
                        return;
                    }

                    // Add request to queue
                    RequestQueueUtil.getInstance(this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);

                    Log.i(LOGGER, " successfully added item, " +
                            item.getPublicToken() + " " +
                            metadata.getAccounts().get(0).getAccountId() + " " +
                            metadata.getAccounts().get(0).getAccountName() + " " +
                            metadata.getInstitutionId() + " " +
                            metadata.getInstitutionName());
                }
            } else if (resultCode == Plaid.RESULT_CANCELLED) {
                LinkCancellation cancellation = (LinkCancellation) data.getSerializableExtra(Plaid.LINK_RESULT);
                if (cancellation != null) {
                    Log.i(LOGGER, "canceled adding item, " +
                            cancellation.getInstitutionId() + " " +
                            cancellation.getInstitutionName() + " " +
                            cancellation.getLinkSessionId() + " " +
                            cancellation.getStatus());
                }
            } else if (resultCode == Plaid.RESULT_EXIT) {
                PlaidApiError error = (PlaidApiError) data.getSerializableExtra(Plaid.LINK_RESULT);
                if (error != null) {
                    Log.e(LOGGER, "error adding item, " +
                            error.getDisplayMessage() + " " +
                            error.getErrorCode() + " " +
                            error.getErrorMessage() + " " +
                            error.getLinkExitMetadata().getInstitutionId()  + " " +
                            error.getLinkExitMetadata().getInstitutionName() + " " +
                            error.getLinkExitMetadata().getStatus());
                }
            } else if (resultCode == Plaid.RESULT_EXCEPTION) {
                Exception exception = (Exception) data.getSerializableExtra(Plaid.LINK_RESULT);
                if (exception != null) {
                    Log.e(LOGGER, "exception adding item, " +
                            exception.getClass().toString() +  " " +
                            exception.getMessage());
                }
            }
        }
    }
}
