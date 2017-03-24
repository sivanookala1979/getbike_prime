package com.vave.getbike.syncher;

import com.vave.getbike.model.BankAccount;
import com.vave.getbike.model.MobileRecharge;
import com.vave.getbike.model.RedeemWallet;
import com.vave.getbike.model.SaveResult;
import com.vave.getbike.model.Wallet;
import com.vave.getbike.model.WalletEntry;
import com.vave.getbike.utils.GsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adarsht on 06/12/16.
 */

public class WalletSyncher extends BaseSyncher {

    public SaveResult rechargeMobile(final MobileRecharge recharge) {
        final SaveResult result = new SaveResult();
        new JsonPostHandler("/wallet/rechargeMobile") {

            @Override
            protected void prepareRequest() {
                put("amount", recharge.getAmount());
                put("mobileNumber", recharge.getMobileNumber());
                put("circle", recharge.getCircle());
                put("operator", recharge.getOperator());
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValid(true);
                } else {
                    result.setErrorMessage("Not enough cash balance.");
                }
            }
        }.handle();
        return result;
    }

    public SaveResult redeemToWallet(final RedeemWallet redeem) {
        final SaveResult result = new SaveResult();
        new JsonPostHandler("/wallet/redeemToWallet") {

            @Override
            protected void prepareRequest() {
                put("amount", redeem.getAmount());
                put("mobileNumber", redeem.getMobileNumber());
                put("walletName", redeem.getWalletName());
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValid(true);
                } else {
                    result.setErrorMessage("Not enough cash balance.");
                }
            }
        }.handle();
        return result;
    }

    public SaveResult redeemToBank(final double amount) {
        final SaveResult result = new SaveResult();
        new JsonPostHandler("/wallet/redeemToBank") {

            @Override
            protected void prepareRequest() {
                put("amount", amount);
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValid(true);
                } else {
                    result.setErrorMessage("Not enough cash balance.");
                }
            }
        }.handle();
        return result;
    }

    public Wallet getWalletDetails() {
        final GetBikePointer<Wallet> result = new GetBikePointer<>(null);
        new JsonGetHandler("/wallet/getBalanceAmount") {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    Wallet wallet = GsonUtils.getGson().fromJson(jsonResult.toString(), Wallet.class);
                    result.setValue(wallet);
                }
            }
        }.handle();
        return result.getValue();
    }

    public List<WalletEntry> getWalletEntries() {
        final ArrayList<WalletEntry> result = new ArrayList<>();
        new JsonGetHandler("/wallet/myEntries") {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("entries")) {
                    JSONArray entriesArray = jsonResult.getJSONArray("entries");
                    for (int i = 0; i < entriesArray.length(); i++) {
                        JSONObject jsonRideObject = entriesArray.getJSONObject(i);
                        WalletEntry walletEntry = GsonUtils.getGson().fromJson(jsonRideObject.toString(), WalletEntry.class);
                        result.add(walletEntry);
                    }
                }
            }
        }.handle();
        return result;
    }

    public String generateOrderIDForWallet(String amount) {
        return generateOrderID("/generateOrderId?type=Wallet&amount=" + amount);
    }

    public String generateOrderIDForRide(long rideId) {
        return generateOrderID("/generateOrderId?type=Ride&rideId=" + rideId);
    }

    public String generateOrderID(final String url) {
        final GetBikePointer<String> result = new GetBikePointer<>(null);
        new JsonGetHandler(url) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result")) && jsonResult.has("orderIdentifier")) {
                    result.setValue(jsonResult.getString("orderIdentifier"));
                }
            }
        }.handle();
        return result.getValue();
    }

    public SaveResult updateBankAccountDetails(final BankAccount bankDetails) {
        final SaveResult result = new SaveResult();
        new JsonPostHandler("/profile/saveAccountDetails") {

            @Override
            protected void prepareRequest() {
                put("accountHolderName", bankDetails.getAccountHolderName());
                put("accountNumber", bankDetails.getAccountNumber());
                put("ifscCode", bankDetails.getIfscCode());
                put("bankName", bankDetails.getBankName());
                put("branchName", bankDetails.getBranchName());
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValid(true);
                }
            }
        }.handle();
        return result;
    }

    public BankAccount getBankAccountDetails() {
        final GetBikePointer<BankAccount> result = new GetBikePointer<>(null);
        new JsonGetHandler("/profile/getAccountDetails") {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    BankAccount bankAccount = GsonUtils.getGson().fromJson(jsonResult.get("accountDetails").toString(), BankAccount.class);
                    result.setValue(bankAccount);
                }
            }
        }.handle();
        return result.getValue();
    }
}

