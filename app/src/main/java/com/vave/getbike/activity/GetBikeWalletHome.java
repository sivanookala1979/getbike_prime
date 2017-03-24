package com.vave.getbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.adapter.WalletAdapter;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.Wallet;
import com.vave.getbike.model.WalletEntry;
import com.vave.getbike.syncher.WalletSyncher;

import java.util.List;

/**
 * Created by adarsht on 06/12/16.
 */

public class GetBikeWalletHome extends BaseActivity implements View.OnClickListener {

    TextView promoBalance, cashBalance, yourBalance, freeRidesTextView;
    ListView history;
    Button redeem, addMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_home_screen);
        addToolbarView();
        promoBalance = (TextView) findViewById(R.id.promoBalance);
        cashBalance = (TextView) findViewById(R.id.cashBalance);
        yourBalance = (TextView) findViewById(R.id.yourBalance);
        freeRidesTextView = (TextView) findViewById(R.id.freeRidesTextView);
        history = (ListView) findViewById(R.id.history);
        redeem = (Button) findViewById(R.id.redeem);
        addMoney = (Button) findViewById(R.id.addMoney);
        redeem.setOnClickListener(this);
        addMoney.setOnClickListener(this);

    }

    private void updateWalletAmount() {
        new GetBikeAsyncTask(GetBikeWalletHome.this) {
            Wallet wallet = null;
            List<WalletEntry> walletEntryList;

            @Override
            public void process() {
                WalletSyncher walletSyncher = new WalletSyncher();
                wallet = walletSyncher.getWalletDetails();
                walletEntryList = walletSyncher.getWalletEntries();
            }

            @Override
            public void afterPostExecute() {
                if (wallet != null) {
                    promoBalance.setText("" + getCurrencySymbol() + " " + wallet.getPromoBalance());
                    cashBalance.setText("" + getCurrencySymbol() + " " + wallet.getCashBalance());
                    yourBalance.setText("" + getCurrencySymbol() + " " + wallet.getUserBalance());
                    freeRidesTextView.setText("" + String.valueOf(wallet.getFreeRidesEarned() - wallet.getFreeRidesSpent()));
                    history.setAdapter(new WalletAdapter(GetBikeWalletHome.this, walletEntryList));
                } else {
                    ToastHelper.redToast(GetBikeWalletHome.this, "Failed to load the wallet details.");
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.redeem:
                startActivity(new Intent(GetBikeWalletHome.this, RedeemAmountActivity.class));
                break;
            case R.id.addMoney:
                startActivity(new Intent(GetBikeWalletHome.this, PayUPaymentActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWalletAmount();
    }
}
//GetBikeWalletHome
