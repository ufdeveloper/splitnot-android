package com.megshan.splitnotandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megshan.splitnotandroid.adapters.TransactionsAdapter;
import com.megshan.splitnotandroid.dto.SplitwiseTokenResponse;
import com.megshan.splitnotandroid.dto.Transaction;
import com.megshan.splitnotandroid.utils.RequestQueueUtil;
import com.plaid.splitnotandroid.R;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SplitwiseAuthorizeActivity extends AppCompatActivity {

    private String LOGGER = "SplitwiseAuthorizeActivity";

    private String clientId = "";
    private String clientSecret = "";
        private String redirectUri = "futurestudio://callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splitwise_authorize);

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.splitwise.com/oauth/authorize" +
                        "?response_type=code" +
                        "&client_id=" + clientId +
                        "&redirect_uri=" + redirectUri));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        Log.i(LOGGER, "uri=" + uri);

        if (uri != null && uri.toString().startsWith(redirectUri)) {
            // use the parameter your API exposes for the code (mostly it's "code")
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // get access token
                // Create request
                String url = "https://www.splitwise.com/oauth/token"
                + "?client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&code=" + code +
                        "&redirect_uri=" + redirectUri;
                Log.i(LOGGER, "get Token uri = " + url);

                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        url,
//                        null,
                        (response -> {
                            Log.i(LOGGER, "token request response=" + response);
                            if(response!=null && !response.equals("false")) {
                                SplitwiseTokenResponse splitwiseTokenResponse =
                                        new Gson().fromJson(response, SplitwiseTokenResponse.class);
                                Log.i(LOGGER, "accessToken=" + splitwiseTokenResponse.getAccessToken());
                                Globals.setSplitwiseAccessToken(splitwiseTokenResponse.getAccessToken());
                            } else {
                                Log.i(LOGGER, "token response does not contain accessToken");
                            }
                        }),
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(LOGGER, "error fetching token, error="
                                        + error.getMessage());
                            }
                        })
                        {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> pars = new HashMap<>();
                                pars.put("Content-Type", "application/x-www-form-urlencoded");
                                pars.put("Authorization", "Basic");
                                return pars;
                            }

                            @Override
                            public Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> pars = new HashMap<String, String>();
//                                pars.put("client_id", clientId);
//                                pars.put("client_secret", clientSecret);
//                                pars.put("code", code);
//                                pars.put("redirect_uri", redirectUri);
                                pars.put("grant_type", "authorization_code");
                                return pars;
                            }
                        };

                // Add request to queue
                RequestQueueUtil.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here
                Log.e(LOGGER, "error during authorize");
            }
        }
    }
}
