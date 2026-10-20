package com.ronistone.browser;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import android.view.Gravity;

public class MainActivity extends Activity {
    private WebView webView;
    private EditText address;
    private ProgressBar progress;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(18,59,93));
        getWindow().setNavigationBarColor(Color.rgb(18,59,93));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        TextView brand = new TextView(this);
        brand.setText("RONI STONE");
        brand.setTextColor(Color.WHITE);
        brand.setTextSize(19);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        brand.setPadding(dp(16), dp(10), dp(16), dp(10));
        brand.setBackgroundColor(Color.rgb(18,59,93));
        brand.setTypeface(null, 1);
        root.addView(brand, new LinearLayout.LayoutParams(-1, dp(48)));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setPadding(dp(8), dp(8), dp(8), dp(6));
        address = new EditText(this);
        address.setSingleLine(true);
        address.setTextSize(14);
        address.setHint("Rechercher ou saisir une adresse web");
        address.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        address.setImeOptions(EditorInfo.IME_ACTION_GO);
        bar.addView(address, new LinearLayout.LayoutParams(0, dp(44), 1));
        Button go = new Button(this);
        go.setText("Aller");
        bar.addView(go, new LinearLayout.LayoutParams(dp(72), dp(44)));
        root.addView(bar);

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        root.addView(progress, new LinearLayout.LayoutParams(-1, dp(3)));

        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                address.setText(url);
                super.onPageFinished(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
                progress.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
            @Override public void onReceivedTitle(WebView view, String title) {
                if (title != null && !title.isEmpty()) setTitle(title + " - Roni Stone");
            }
        });
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout nav = new LinearLayout(this);
        nav.setPadding(dp(8), dp(3), dp(8), dp(3));
        nav.setBackgroundColor(Color.rgb(245,247,249));
        Button back = new Button(this); back.setText("← Retour");
        Button forward = new Button(this); forward.setText("Avancer →");
        Button home = new Button(this); home.setText("Accueil");
        nav.addView(back, new LinearLayout.LayoutParams(0, dp(46), 1));
        nav.addView(forward, new LinearLayout.LayoutParams(0, dp(46), 1));
        nav.addView(home, new LinearLayout.LayoutParams(0, dp(46), 1));
        root.addView(nav);
        setContentView(root);

        go.setOnClickListener(v -> loadAddress());
        address.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) { loadAddress(); return true; }
            return false;
        });
        back.setOnClickListener(v -> { if (webView.canGoBack()) webView.goBack(); else Toast.makeText(this, "Pas de page précédente", Toast.LENGTH_SHORT).show(); });
        forward.setOnClickListener(v -> { if (webView.canGoForward()) webView.goForward(); else Toast.makeText(this, "Pas de page suivante", Toast.LENGTH_SHORT).show(); });
        home.setOnClickListener(v -> webView.loadUrl("https://www.google.com"));
        webView.loadUrl("https://www.google.com");
    }

    private void loadAddress() {
        String text = address.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;
        String url;
        if (text.contains(" ") || (!text.contains(".") && !text.startsWith("http://") && !text.startsWith("https://"))) {
            url = "https://www.google.com/search?q=" + android.net.Uri.encode(text);
        } else {
            url = text.contains("://") ? text : "https://" + text;
        }
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(address.getWindowToken(), 0);
        webView.loadUrl(url);
    }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }
    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
}
