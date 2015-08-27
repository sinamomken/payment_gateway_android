package ir.sinamomken.paymentwebview.payment.gateway;

import android.content.Context;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ir.keloud.android.lib.common.operations.RemoteOperationResult;
import ir.keloud.android.lib.common.utils.Log_OC;
import ir.sinamomken.paymentwebview.R;
import ir.sinamomken.paymentwebview.auth_keloud.BaseAuthOperation;

/**
 * Created by momken on 8/26/15.
 */
public class PaymentVerificationOperation extends BaseAuthOperation implements Runnable{
    private static final String TAG = PaymentVerificationOperation.class.getSimpleName();

    private String mResultUrl;

    /**
     * {@inheritDoc}
     */
    public PaymentVerificationOperation(Context context){
        super(context);
    }

    public RemoteOperationResult verifyPayment(String resultUrl){
        Log_OC.d(TAG, "verifying payment with resultUrl = " + resultUrl);
        mResultUrl = resultUrl;
        run();
        return mResult;
    }

    public void run(){
        GetMethod get = new GetMethod(mResultUrl);
        try {
            mHttpStatusCode = mClient.executeMethod(get);
            Log_OC.d(TAG, "http GET returned response code = "+mHttpStatusCode);
            mResult = new RemoteOperationResult(mHttpStatusCode == HttpStatus.SC_OK,
                    mHttpStatusCode, get.getResponseHeaders());

            byte[] responseBody = get.getResponseBody();
            //Checking whether response body has unicode BOM?
            byte[] trimmedResponseBody = checkAndRemoveBOM(responseBody);
            String responseBodyString = new String(trimmedResponseBody);
            Log_OC.d(TAG, "resposeBody = " + responseBodyString);
            mResponseJson = new JSONObject(responseBodyString);

            ArrayList<Object> resultData = new ArrayList<Object>(14);
            resultData.add(mResponseJson.getString("responseRefId"));
            resultData.add(mResponseJson.getInt("__v"));
            resultData.add(mResponseJson.getString("_id"));
            resultData.add(mResponseJson.getInt("amount"));
            resultData.add(mResponseJson.getString("appId"));
            resultData.add(mResponseJson.getString("desc"));
            resultData.add(mResponseJson.getString("email"));
            resultData.add(mResponseJson.getString("gatewayId"));
            resultData.add(mResponseJson.getString("mobile"));
            resultData.add(mResponseJson.getString("refId"));
            resultData.add(mResponseJson.getString("requestDate"));
            resultData.add(mResponseJson.getString("responseResultCode"));
            resultData.add(mResponseJson.getString("transactionState"));
            resultData.add(mResponseJson.getString("ussdCode"));
            mResult.setData(resultData);
        } catch (JSONException e){
            Log_OC.d(TAG, ": JSONException thrown: " + e.getMessage());
            mResult = new RemoteOperationResult(false, mHttpStatusCode, get.getResponseHeaders());
            resultSetMsgData(mCallerContext.getString(R.string.auth_json_error));
        } catch (UnsupportedEncodingException e) {
            Log_OC.d(TAG, ": UnsupportedEncodingException thrown: " + e.getMessage());
            resultSetMsgData(mCallerContext.getString(R.string.auth_unsupported_encoding_error));
        } catch (HttpException e) {
            Log_OC.d(TAG, ": HttpException thrown: " + e.getMessage());
            resultSetMsgData(mCallerContext.getString(R.string.auth_http_error));
        } catch (IOException e) {
            Log_OC.d(TAG, ": IOException thrown: " + e.getMessage());
            mResult = new RemoteOperationResult(false, 495, null);
            resultSetMsgData(mCallerContext.getString(R.string.auth_timeout_title));
        } finally {
            get.releaseConnection();
        }
    }
}
