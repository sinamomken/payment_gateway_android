package ir.sinamomken.paymentwebview;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ir.keloud.android.lib.common.utils.Log_OC;

public class WebViewActivity extends ActionBarActivity {
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private String mIPGUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mIPGUrl = getIntent().getStringExtra("IPG_URL");
        Log_OC.d(TAG, "ipgUrl = " + mIPGUrl);

        WebView wv = (WebView) findViewById(R.id.web_view);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setBuiltInZoomControls(true);
        wv.loadUrl(mIPGUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
