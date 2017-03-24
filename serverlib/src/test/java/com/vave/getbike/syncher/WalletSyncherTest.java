package com.vave.getbike.syncher;

import com.vave.getbike.model.BankAccount;
import com.vave.getbike.model.Wallet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by adarsht on 09/12/16.
 */

public class WalletSyncherTest {
    WalletSyncher sut;

    @Test
    public void updateBankAccountDetailsTESTHappyFlow() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountHolderName("Adarsh T");
        bankAccount.setAccountNumber("123456");
        bankAccount.setIfscCode("A0000123");
        bankAccount.setBankName("Axis");
        bankAccount.setBranchName("Kavali Axis");
        sut.updateBankAccountDetails(bankAccount);
        BankAccount updatedBankAccount = sut.getBankAccountDetails();
        assertEquals(bankAccount.getAccountHolderName(),updatedBankAccount.getAccountHolderName());
        assertEquals(bankAccount.getAccountNumber(),updatedBankAccount.getAccountNumber());
        assertEquals(bankAccount.getIfscCode(),updatedBankAccount.getIfscCode());
        assertEquals(bankAccount.getBankName(),updatedBankAccount.getBankName());
        assertEquals(bankAccount.getBranchName(),updatedBankAccount.getBranchName());

    }

    @Test
    public void getWalletDetailsTESTHappyFlow() {
        Wallet wallet = sut.getWalletDetails();
        assertNotNull(wallet.getCashBalance());
        assertNotNull(wallet.getPromoBalance());
        assertNotNull(wallet.getUserBalance());
        assertNotNull(wallet.getFreeRidesEarned());
        assertNotNull(wallet.getFreeRidesSpent());

    }

    @Before
    public void setUp() {
        sut = new WalletSyncher();
        BaseSyncher.testSetup();
    }

    @After
    public void tearDown() {
        sut = null;
    }

}
