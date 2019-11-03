package com.megshan.splitnotandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megshan.splitnotandroid.adapters.TransactionsAdapter;
import com.megshan.splitnotandroid.dto.Transaction;
import com.megshan.splitnotandroid.utils.RequestQueueUtil;
import com.plaid.splitnotandroid.R;

import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private static final String LOGGER = "TransactionsActivity";

    private RecyclerView recyclerView;
    private TextView transactionsEmptyView;
    private RecyclerView.Adapter transactionsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.transactions_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Create request
        String url = "http://10.0.2.2:8080/transactions?userKey=123";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null,
                        (response -> {
                            Log.i(LOGGER, response.toString());
                            transactionList = new Gson().fromJson(response.toString(),
                                    new TypeToken<List<Transaction>>() {}.getType());

                            if(transactionList.size() == 0) {
                                transactionsEmptyView = findViewById(R.id.transactions_empty_view);
                                transactionsEmptyView.setVisibility(View.VISIBLE);
                            } else {
                                transactionsAdapter = new TransactionsAdapter(transactionList);
                                recyclerView.setAdapter(transactionsAdapter);
                            }
                        }),
                        (error -> Log.e(LOGGER, "error fetching data from splitnot-api, error=" + error))
                );

        // Add request to queue
        RequestQueueUtil.getInstance(this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOGGER, "onResume invoked");
        if(transactionsAdapter != null) {
            transactionsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.accounts_menu, menu);
        return true;
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accounts:
                Intent intent = new Intent(this, ListItemsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
