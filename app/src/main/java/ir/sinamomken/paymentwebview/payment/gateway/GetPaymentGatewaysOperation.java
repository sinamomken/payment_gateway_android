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
public class GetPaymentGatewaysOperation extends BaseAuthOperation implements Runnable{
    private static final String TAG = GetPaymentGatewaysOperation.class.getSimpleName();

    private JSONArray mResponseJson = null;
    private String mToken = "";
    /**
     * {@inheritDoc}
     */
    public GetPaymentGatewaysOperation(Context context){
        super(context);
    }

    public RemoteOperationResult getPaymentGateways(String token){
        Log_OC.d(TAG, "getting list of payment gateways with token = "+token);
        mToken = token;
        run();
        return mResult;
    }

    @Override
    public void run(){
        GetMethod get = new GetMethod(mCallerContext.getString(R.string.payment_gateway_server_url)+"/payment/getPaymentGateways");
        try {
            //Adds token header.
            get.setRequestHeader("token", mToken);
            mHttpStatusCode = mClient.executeMethod(get);
            Log_OC.d(TAG, "http GET returned response code = "+mHttpStatusCode);
            mResult = new RemoteOperationResult(mHttpStatusCode == HttpStatus.SC_OK,
                    mHttpStatusCode, get.getResponseHeaders());

            byte[] responseBody = get.getResponseBody();
            //Checking whether response body has unicode BOM?
            byte[] trimmedResponseBody = checkAndRemoveBOM(responseBody);
            String responseBodyString = new String(trimmedResponseBody);
            Log_OC.d(TAG, "resposeBody = " + responseBodyString);

            mResponseJson = new JSONArray(responseBodyString);
            JSONObject firstObject = (JSONObject) mResponseJson.get(0);

            ArrayList<Object> resultData = new ArrayList<Object>(7);
            resultData.add(firstObject.getString("name"));
            resultData.add(firstObject.getString("login"));
            resultData.add(firstObject.getString("key"));
            resultData.add(firstObject.getString("url"));
            resultData.add(firstObject.getString("createdDate"));
            resultData.add(firstObject.getString("_id"));
            resultData.add(firstObject.getString("__v"));
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
