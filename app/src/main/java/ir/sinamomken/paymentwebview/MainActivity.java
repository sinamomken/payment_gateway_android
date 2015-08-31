package ir.sinamomken.paymentwebview;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ir.keloud.android.lib.common.operations.RemoteOperationResult;
import ir.keloud.android.lib.common.utils.Log_OC;
import ir.sinamomken.paymentwebview.payment.gateway.GetPaymentGatewaysOperation;
import ir.sinamomken.paymentwebview.payment.gateway.GetPaymentUrlOperation;
import ir.sinamomken.paymentwebview.payment.gateway.PaymentVerificationOperation;
import ir.sinamomken.paymentwebview.payment.gateway.SigninOperation;
import ir.sinamomken.paymentwebview.payment.gateway.SignupOperation;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private String mAccountPassword = "123456";
    private String mAppId = "55dd4a4a01c890c135d1e007";
    private String mToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI1NWRkNGE0YTAxYzg5MGMxMzVkMWUwMDciLCJleHAiOjE0NDExNzEzMjA0NTl9.HL2QQdyuapJYlbrLYRjEfvt-FxOyfUtym3MfAWQBXc4";
    private String mGatewayId = "55db1af888233a8134a14781";
    private int mToman = 100;
    private String mIPGUrl = "https://www.zarinpal.com/pg/StartPay/000000000000000000000000000007505554";
    private String mUSSD = "";
    private String mResultUrl = "";
    private String mResultMsg = "no result yet!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSignupBtnClick(View view){
        new SignupAsyncTask().execute();
    }

    private class SignupAsyncTask extends AsyncTask<Void, Void, RemoteOperationResult> {
        private final String TAG = SignupAsyncTask.class.getSimpleName();
        @Override
        protected RemoteOperationResult doInBackground(Void... v){
            Log_OC.d(TAG, "trying to signup in background");

            SignupOperation signupOperation = new SignupOperation(MainActivity.this);
            mAccountPassword = "123456";
            RemoteOperationResult result = signupOperation.signup("sina_test3", "123456", "sina.momken@gmail.com", "09124574396");
            Log_OC.d(TAG, "appId in response = " + (String) result.getData().get(1));
            return result;
        }

        @Override
        protected void onPostExecute(RemoteOperationResult result){
            TextView tv = (TextView) findViewById(R.id.appid_txt);
            Log_OC.d(TAG, "trying to show appId or any error");
            if(result.isSuccess()){
                mAppId = (String)result.getData().get(1);
                tv.setText( mAppId );
            }else{
                tv.setText("Error");
            }
        }
    }


    public void onSigninBtnClick(View view){
        new SigninAsyncTask().execute();
    }

    private class SigninAsyncTask extends AsyncTask<Void, Void, RemoteOperationResult> {
        private final String TAG = SigninAsyncTask.class.getSimpleName();
        @Override
        protected RemoteOperationResult doInBackground(Void... v){
            Log_OC.d(TAG, "trying to signin in background");
            SigninOperation signinOperation = new SigninOperation(MainActivity.this);
            RemoteOperationResult result = signinOperation.signin(mAccountPassword, mAppId);
            Log_OC.d(TAG, "token in response = " + (String) result.getData().get(5));
            return result;
        }

        @Override
        protected void onPostExecute(RemoteOperationResult result){
            TextView tv = (TextView) findViewById(R.id.token_txt);
            Log_OC.d(TAG, "trying to show token or any error");
            if(result.isSuccess()){
                mToken = (String)result.getData().get(5);
                tv.setText( mToken );
            }else{
                tv.setText("Error");
            }
        }
    }


    public void onGetGatewaysBtnClick(View view){
        new GetGatewaysAsyncTask().execute();
    }

    private class GetGatewaysAsyncTask extends AsyncTask<Void, Void, RemoteOperationResult> {
        private final String TAG = GetGatewaysAsyncTask.class.getSimpleName();
        @Override
        protected RemoteOperationResult doInBackground(Void... v){
            Log_OC.d(TAG, "trying to get gateways in background");
            GetPaymentGatewaysOperation getGatewaysOperation = new GetPaymentGatewaysOperation(MainActivity.this);
            RemoteOperationResult result = getGatewaysOperation.getPaymentGateways(mToken);
            Log_OC.d(TAG, "IPGId in response = " + (String) result.getData().get(5));
            return result;
        }

        @Override
        protected void onPostExecute(RemoteOperationResult result){
            TextView tv = (TextView) findViewById(R.id.ipgid_txt);
            Log_OC.d(TAG, "trying to show IPGId or any error");
            if(result.isSuccess()){
                mGatewayId = (String) result.getData().get(5);
                tv.setText( mGatewayId );
            }else{
                tv.setText("Error");
            }
        }
    }


    public void onPaymentUrlBtnClick(View view){
        mToman = Integer.valueOf(((EditText) findViewById(R.id.toman_et)).getText().toString());
        new PaymentUrlAsyncTask().execute();
    }

    private class PaymentUrlAsyncTask extends AsyncTask<Void, Void, RemoteOperationResult> {
        private final String TAG = PaymentUrlAsyncTask.class.getSimpleName();
        @Override
        protected RemoteOperationResult doInBackground(Void... v){
            Log_OC.d(TAG, "trying to get payment url in background");
            GetPaymentUrlOperation paymentUrlOperation = new GetPaymentUrlOperation(MainActivity.this);
            RemoteOperationResult result = paymentUrlOperation.getPaymentUrl(mToken,mGatewayId,mToman,"sina android test","sina.momken@gmail.com","09124574396");
            //Log_OC.d(TAG, "IPGUrl in response = " + (String) result.getData().get(3));
            return result;
        }

        @Override
        protected void onPostExecute(RemoteOperationResult result){
            TextView ipgurl_tv = (TextView) findViewById(R.id.ipgurl_txt);
            TextView pm_tv = (TextView) findViewById(R.id.payment_method_txt);
            TextView ussd_tv = (TextView) findViewById(R.id.ussd_txt);
            Log_OC.d(TAG, "trying to show IPGUrl and PaymentMethod or any error");
            if(result.isSuccess()){
                mIPGUrl = (String) result.getData().get(3);
                ipgurl_tv.setText(mIPGUrl);
                mUSSD =  (String) result.getData().get(4);
                ussd_tv.setText(mUSSD);
                int paymentMethod = (int) result.getData().get(5);
                pm_tv.setText( Integer.toString(paymentMethod) );
            }else{
                ipgurl_tv.setText("Error");
                pm_tv.setText("Error");
            }
        }
    }

    private static final int WEBVIEW_REQUEST_CODE = 1;
    public void onOpenWebviewBtnClick(View view){
        Intent i = new Intent(this , WebViewActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("IPG_URL",mIPGUrl);
        startActivityForResult(i, WEBVIEW_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log_OC.d(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);
        if (requestCode == WEBVIEW_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                Log_OC.e(TAG, "result url = " + mResultUrl);
                mResultUrl = data.getStringExtra(WebViewActivity.KEY_RESULT_URL);
                onPaymentVerificationBtnClick(new View(this));
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void onOpenChromeBtnClick(View view){
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(mIPGUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead

        }
    }

    public void onOpenBrowserBtnClick(View view){
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.google.android.browser","com.google.android.browser.BrowserActivity");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.BROWSABLE");
        Uri uri = Uri.parse(mIPGUrl);
        intent.setData(uri);
        //try
        //{
            startActivity(intent);
        //}
        //catch (Exception e)
        //{
            //e.printStackTrace();
        //}
    }


    public void onPaymentVerificationBtnClick(View view){
        new PaymentVerificationAsyncTask().execute();
    }

    private class PaymentVerificationAsyncTask extends AsyncTask<Void, Void, RemoteOperationResult> {
        private final String TAG = PaymentVerificationAsyncTask.class.getSimpleName();
        @Override
        protected RemoteOperationResult doInBackground(Void... v){
            Log_OC.d(TAG, "trying to verify payment in background");
            PaymentVerificationOperation verificationOperation = new PaymentVerificationOperation(MainActivity.this);
            RemoteOperationResult result = verificationOperation.verifyPayment(mResultUrl);
            return result;
        }

        @Override
        protected void onPostExecute(RemoteOperationResult result){
            Log_OC.d(TAG, "trying to show result of payment");

            Dialog resultDialog = new Dialog(MainActivity.this);
            resultDialog.setContentView(R.layout.result_dialog);
            resultDialog.setTitle("Payment Result");
            TextView dialogText = (TextView) resultDialog.findViewById(R.id.dialog_text);

            if(result.isSuccess()){
                 mResultMsg = (String) result.getData().get(12);
            }else{
                mResultMsg = "Error in receiving payment result";
            }
            dialogText.setText(mResultMsg);
            resultDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
