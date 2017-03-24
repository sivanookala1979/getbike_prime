package com.vave.getbike.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.MobileRecharge;
import com.vave.getbike.model.RedeemWallet;
import com.vave.getbike.model.SaveResult;
import com.vave.getbike.model.Wallet;
import com.vave.getbike.syncher.WalletSyncher;
import com.vave.getbike.utils.StringUtils;

import java.util.List;

/**
 * Created by adarsht on 06/12/16.
 */

public class RedeemAmountActivity extends BaseActivity implements View.OnClickListener {

    ImageView rechargeMobile, redeemToWallet, redeemToBank;
    TextView promoBalance, cashBalance, yourBalance;
    EditText rechargeMobileNumber, rechargeAmount, redeemMobileNumber, redeemAmount, bankAmount;
    Spinner operator, circle, walletName;
    Button rechargeSubmit, walletSubmit, bankSubmit;
    LinearLayout rechargeMobileChild, redeemToWalletChild, redeemToBankChildView;
    WalletSyncher walletSyncher = new WalletSyncher();
    Wallet wallet = new Wallet();
    List<String> circles;
    List<String> operators;
    List<String> walletNames;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem_amount_screen);
        addToolbarView();
        //WALLET DETAILS
        promoBalance = (TextView) findViewById(R.id.promoBalance);
        cashBalance = (TextView) findViewById(R.id.cashBalance);
        yourBalance = (TextView) findViewById(R.id.yourBalance);

        //RECHARGE MOBILE
        rechargeMobile = (ImageView) findViewById(R.id.rechargeMobile);
        rechargeMobileChild = (LinearLayout) findViewById(R.id.rechargeMobileChild);
        rechargeMobileNumber = (EditText) findViewById(R.id.rechargeMobileNumber);
        rechargeAmount = (EditText) findViewById(R.id.rechargeAmount);
        operator = (Spinner) findViewById(R.id.operator);
        circle = (Spinner) findViewById(R.id.circle);
        rechargeSubmit = (Button) findViewById(R.id.rechargeSubmit);

        //REDEEM TO WALLET
        redeemToWallet = (ImageView) findViewById(R.id.redeemToWallet);
        redeemToWalletChild = (LinearLayout) findViewById(R.id.redeemToWalletChild);
        redeemMobileNumber = (EditText) findViewById(R.id.redeemMobileNumber);
        redeemAmount = (EditText) findViewById(R.id.redeemAmount);
        walletName = (Spinner) findViewById(R.id.walletName);
        walletSubmit = (Button) findViewById(R.id.redeemSubmit);

        //REDEEM TO BANK
        redeemToBank = (ImageView) findViewById(R.id.redeemToBank);
        redeemToBankChildView = (LinearLayout) findViewById(R.id.redeemToBankChildView);
        bankAmount = (EditText) findViewById(R.id.bankAmount);
        bankSubmit = (Button) findViewById(R.id.redeemBankSubmit);
        addOnClickListeners(rechargeMobile, rechargeSubmit, redeemToWallet, walletSubmit, redeemToBank, bankSubmit);
        setupSpinnersDate();
        loadWalletData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void loadWalletData() {
        new GetBikeAsyncTask(RedeemAmountActivity.this) {
            boolean result = false;

            @Override
            public void process() {
                wallet = walletSyncher.getWalletDetails();
            }

            @Override
            public void afterPostExecute() {
                promoBalance.setText("" + getCurrencySymbol() + " " + wallet.getPromoBalance());
                cashBalance.setText("" + getCurrencySymbol() + " " + wallet.getCashBalance());
                yourBalance.setText("" + getCurrencySymbol() + " " + wallet.getUserBalance());
            }
        }.execute();
    }

    private void addOnClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rechargeMobile:
                rechargeMobileChild.setVisibility((rechargeMobileChild.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                break;
            case R.id.redeemToWallet:
                redeemToWalletChild.setVisibility((redeemToWalletChild.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                break;
            case R.id.redeemToBank:
                redeemToBankChildView.setVisibility((redeemToBankChildView.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                break;
            case R.id.rechargeSubmit:
                MobileRecharge mobileRecharge = collectRechargeData();
                SaveResult result = mobileRecharge.isValid();
                if (result.isValid()) {
                    sendMobileRecharge(mobileRecharge);
                } else {
                    ToastHelper.redToast(getApplicationContext(), "Please enter valid " + result.getErrorMessage());
                }
                break;

            case R.id.redeemSubmit:
                RedeemWallet redeemWallet = collectWalletData();
                SaveResult status = redeemWallet.isValid();
                if (status.isValid()) {
                    sendWalletData(redeemWallet);
                } else {
                    ToastHelper.redToast(getApplicationContext(), "Please enter valid " + status.getErrorMessage());
                }
                break;
            case R.id.redeemBankSubmit:
                double amount = StringUtils.getDouble(bankAmount.getText().toString());
                if (amount > 0) {
                    sendBankAmount(amount);
                } else {
                    ToastHelper.redToast(getApplicationContext(), "Please enter valid amount.");
                }
                break;
        }
    }

    private void sendBankAmount(final double amount) {
        new GetBikeAsyncTask(RedeemAmountActivity.this) {
            SaveResult saveResult;

            @Override
            public void process() {
                saveResult = walletSyncher.redeemToBank(amount);
            }

            @Override
            public void afterPostExecute() {
                if (saveResult.isValid()) {
                    ToastHelper.redToast(getApplicationContext(), "Transaction successful.");
                    bankAmount.setText("");
                } else {
                    ToastHelper.redToast(getApplicationContext(), saveResult.getErrorMessage());
                }
            }
        }.execute();
    }

    private void sendWalletData(final RedeemWallet redeemWallet) {
        new GetBikeAsyncTask(RedeemAmountActivity.this) {
            SaveResult saveResult;

            @Override
            public void process() {
                saveResult = walletSyncher.redeemToWallet(redeemWallet);
            }

            @Override
            public void afterPostExecute() {
                if (saveResult.isValid()) {
                    ToastHelper.redToast(getApplicationContext(), "Transaction successful.");
                    redeemMobileNumber.setText("");
                    redeemAmount.setText("");
                } else {
                    ToastHelper.redToast(getApplicationContext(), saveResult.getErrorMessage());
                }
            }
        }.execute();
    }

    private RedeemWallet collectWalletData() {
        RedeemWallet redeemWallet = new RedeemWallet();
        redeemWallet.setMobileNumber(redeemMobileNumber.getText().toString());
        redeemWallet.setAmount(StringUtils.getDouble(redeemAmount.getText().toString()));
        redeemWallet.setWalletName(walletName.getSelectedItem().toString());
        return redeemWallet;
    }

    private void sendMobileRecharge(final MobileRecharge mobileRecharge) {
        new GetBikeAsyncTask(RedeemAmountActivity.this) {
            SaveResult saveResult;

            @Override
            public void process() {
                saveResult = walletSyncher.rechargeMobile(mobileRecharge);
            }

            @Override
            public void afterPostExecute() {
                if (saveResult.isValid()) {
                    ToastHelper.redToast(getApplicationContext(), "Recharge successful.");
                    rechargeMobileNumber.setText("");
                    rechargeAmount.setText("");
                } else {
                    ToastHelper.redToast(getApplicationContext(), saveResult.getErrorMessage());
                }
            }
        }.execute();
    }

    private MobileRecharge collectRechargeData() {
        MobileRecharge mobileRecharge = new MobileRecharge();
        mobileRecharge.setMobileNumber(rechargeMobileNumber.getText().toString());
        mobileRecharge.setAmount(StringUtils.getDouble(rechargeAmount.getText().toString()));
        mobileRecharge.setCircle(circle.getSelectedItem().toString());
        mobileRecharge.setOperator(operator.getSelectedItem().toString());
        return mobileRecharge;
    }

    private void setupSpinnersDate() {
        circles = StringUtils.getStringsList("Andhra Pradesh", "Arunachal Pradesh", "Telangana", "Tamil Nadu");
        operators = StringUtils.getStringsList("Airtel", "Aircel", "BSNL", "Jio", "Vodafone", "Idea");
        walletNames = StringUtils.getStringsList("PAYTM", "PAYU");
        setArrayAdapter(circle, circles);
        setArrayAdapter(operator, operators);
        setArrayAdapter(walletName, walletNames);
    }

    @NonNull
    private void setArrayAdapter(Spinner spinner, List<String> list) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_lay_out, list);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_text_lay_out);
        spinner.setAdapter(arrayAdapter);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("RedeemAmount Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
