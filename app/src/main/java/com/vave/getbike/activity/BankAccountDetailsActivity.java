package com.vave.getbike.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.BankAccount;
import com.vave.getbike.model.SaveResult;
import com.vave.getbike.syncher.WalletSyncher;

/**
 * Created by adarsht on 06/12/16.
 */

public class BankAccountDetailsActivity extends BaseActivity implements View.OnClickListener {
    EditText accountHoldername, accountNumber, ifscCode, bankName, branchName;
    Button update;
    WalletSyncher walletSyncher = new WalletSyncher();
    BankAccount bankDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_account_details);
        addToolbarView();
        accountHoldername = (EditText) findViewById(R.id.accountHolderName);
        accountNumber = (EditText) findViewById(R.id.accountNumber);
        ifscCode = (EditText) findViewById(R.id.ifscCode);
        bankName = (EditText) findViewById(R.id.bankName);
        branchName = (EditText) findViewById(R.id.branchName);
        update = (Button) findViewById(R.id.updateBankDetails);
        update.setOnClickListener(this);
        getBankDetails();
    }

    private void getBankDetails() {
        new GetBikeAsyncTask(BankAccountDetailsActivity.this) {
            SaveResult saveResult;

            @Override
            public void process() {
                bankDetails = walletSyncher.getBankAccountDetails();
            }

            @Override
            public void afterPostExecute() {
                if (bankDetails!=null) {
                    accountHoldername.setText(bankDetails.getAccountHolderName());
                    accountNumber.setText(bankDetails.getAccountNumber());
                    ifscCode.setText(bankDetails.getIfscCode());
                    bankName.setText(bankDetails.getBankName());
                    branchName.setText(bankDetails.getBranchName());
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.updateBankDetails) {
            BankAccount bankDetails = collectAccountDetails();
            SaveResult result = bankDetails.isDataValid();
            if (result.isValid()) {
                updateBankDetails(bankDetails);
            } else {
                ToastHelper.redToast(getApplicationContext(), "Please enter valid " + result.getErrorMessage());
            }
        }
    }

    private void updateBankDetails(final BankAccount bankDetails) {
        new GetBikeAsyncTask(BankAccountDetailsActivity.this) {
            SaveResult saveResult = null;

            @Override
            public void process() {
                saveResult = walletSyncher.updateBankAccountDetails(bankDetails);
            }

            @Override
            public void afterPostExecute() {
                if (saveResult.isValid()) {
                    ToastHelper.blueToast(getApplicationContext(), R.string.success_account_details);
                    finish();
                } else {
                    ToastHelper.redToast(getApplicationContext(), saveResult.getErrorMessage());
                }
            }
        }.execute();
    }

    private BankAccount collectAccountDetails() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountHolderName(accountHoldername.getText().toString());
        bankAccount.setAccountNumber(accountNumber.getText().toString());
        bankAccount.setIfscCode(ifscCode.getText().toString());
        bankAccount.setBankName(bankName.getText().toString());
        bankAccount.setBranchName(branchName.getText().toString());
        return bankAccount;
    }
}
