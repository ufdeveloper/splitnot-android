package com.megshan.splitnotandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.megshan.splitnotandroid.utils.RequestQueueUtil;
import com.plaid.splitnotandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddTransactionToSplitwiseActivity extends AppCompatActivity {

    private static String LOGGER = "AddTransactionToSplitwiseActivity";

    private static String EXTRA_TRANSACTION_NAME = "EXTRA_TRANSACTION_NAME";
    private static String EXTRA_TRANSACTION_AMOUNT = "EXTRA_TRANSACTION_AMOUNT";

    private TextView transactionTextView;
    private Button postToSplitwiseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction_to_splitwise);

        String transactionName = getIntent().getStringExtra(EXTRA_TRANSACTION_NAME);
        Double transactionAmount = getIntent().getDoubleExtra(EXTRA_TRANSACTION_AMOUNT, 0D);

        transactionTextView = findViewById(R.id.add_transaction_to_splitwise_text_view);
        transactionTextView.setText(transactionName + "     $" + transactionAmount);

        postToSplitwiseButton = findViewById(R.id.add_transaction_to_splitwise_button);
        postToSplitwiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.splitwise.com/api/v3.0/create_expense";
                StringRequest stringRequest = new StringRequest
                        (Request.Method.POST, url,
                                (response -> {
                                    Log.i(LOGGER, response);
                                }),
                                (error -> Log.e(LOGGER,
                                        "error adding expense to splitwise, error=" + error))
                        ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> pars = new HashMap<>();
                        pars.put("Content-Type", "application/x-www-form-urlencoded");
                        pars.put("Authorization", "Bearer " + Globals.getSplitwiseAccessToken());
                        return pars;
                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> pars = new HashMap<>();
                        pars.put("creation_method", "equal");
                        pars.put("cost", transactionAmount.toString());
                        pars.put("currency_code", "USD");
                        pars.put("description", transactionName);

                        pars.put("users__0__paid_share", String.valueOf(transactionAmount));
                        pars.put("users__0__owed_share", String.valueOf(transactionAmount/2D));
                        pars.put("users__0__email", "ufdeveloper@gmail.com");

                        pars.put("users__1__paid_share", "0.0");
                        pars.put("users__1__owed_share", String.valueOf(transactionAmount/2D));
                        pars.put("users__1__email", "shan.phreak@gmail.com");

                        return pars;
                    }
                };

                // Add request to queue
                RequestQueueUtil.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }
        });
    }
}
