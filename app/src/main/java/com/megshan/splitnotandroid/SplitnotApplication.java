package com.megshan.splitnotandroid;

import android.app.Application;

import com.plaid.link.BuildConfig;
import com.plaid.link.Plaid;
import com.plaid.linkbase.models.PlaidOptions;
import com.plaid.linkbase.models.PlaidEnvironment;
import com.plaid.plog.LogLevel;

public class SplitnotApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    PlaidOptions plaidOptions = new PlaidOptions.Builder()
        .environment(PlaidEnvironment.DEVELOPMENT)
        .logLevel(BuildConfig.DEBUG ? LogLevel.DEBUG : LogLevel.ASSERT)
        .build();

    Plaid.create(this, plaidOptions);
  }
}
