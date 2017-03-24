package com.vave.getbike.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vave.getbike.R;

public class FAQActivity extends BaseActivity implements View.OnClickListener {

    ImageView accountIcon;
    ImageView promotionIcon;
    ImageView getbikeIcon;
    ImageView earnFreeRidesIcon;
    ImageView redeemMoneyIcon;
    ImageView addMoneyIcon;
    Button contactUsButton;

    RelativeLayout faqRelativeLayout;
    RelativeLayout webViewRelativeLayout;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        addToolbarView();
        accountIcon = (ImageView) findViewById(R.id.account_icon);
        promotionIcon = (ImageView) findViewById(R.id.promotions_icon);
        getbikeIcon = (ImageView) findViewById(R.id.getbike_icon);
        earnFreeRidesIcon = (ImageView) findViewById(R.id.free_rides_icon);
        redeemMoneyIcon = (ImageView) findViewById(R.id.redeem_icon);
        addMoneyIcon = (ImageView) findViewById(R.id.add_money_icon);
        contactUsButton = (Button) findViewById(R.id.contact_us_button);
        faqRelativeLayout = (RelativeLayout) findViewById(R.id.faq_relative_layout);
        webViewRelativeLayout = (RelativeLayout) findViewById(R.id.web_view_relative_layout);
        webView = (WebView) findViewById(R.id.web_view);

        accountIcon.setOnClickListener(this);
        promotionIcon.setOnClickListener(this);
        getbikeIcon.setOnClickListener(this);
        earnFreeRidesIcon.setOnClickListener(this);
        redeemMoneyIcon.setOnClickListener(this);
        addMoneyIcon.setOnClickListener(this);
        contactUsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_icon:
                faqRelativeLayout.setVisibility(View.GONE);
                webViewRelativeLayout.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("http://getbike.co.in");
                break;
            case R.id.promotions_icon:
                faqRelativeLayout.setVisibility(View.GONE);
                webViewRelativeLayout.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("http://getbike.co.in");
                break;
            case R.id.getbike_icon:
                faqRelativeLayout.setVisibility(View.GONE);
                webViewRelativeLayout.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("http://getbike.co.in");
                break;
            case R.id.free_rides_icon:
                faqRelativeLayout.setVisibility(View.GONE);
                webViewRelativeLayout.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("http://getbike.co.in");
                break;
            case R.id.redeem_icon:
                faqRelativeLayout.setVisibility(View.GONE);
                webViewRelativeLayout.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("http://getbike.co.in");
                break;
            case R.id.add_money_icon:
                faqRelativeLayout.setVisibility(View.GONE);
                webViewRelativeLayout.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("http://getbike.co.in");
                break;
            case R.id.contact_us_button:
                Toast.makeText(FAQActivity.this, "For more queries, write a mail to support@vave.co.in", Toast.LENGTH_LONG).show();
                break;
        }

    }
}
