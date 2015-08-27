package ir.sinamomken.paymentwebview.auth_keloud;

import android.content.Context;
import android.net.Uri;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import ir.sinamomken.paymentwebview.R;
import ir.keloud.android.lib.common.KeloudClient;
import ir.keloud.android.lib.common.KeloudClientFactory;
import ir.keloud.android.lib.common.operations.RemoteOperationResult;
import ir.keloud.android.lib.common.utils.Log_OC;

/**
 * Created by momken on 7/8/15.
 */
public class BaseAuthOperation {
    private static final String TAG = BaseAuthOperation.class.getSimpleName();

    protected String mMobileNumber = "+989127654567";
    protected RemoteOperationResult mResult;
    protected int mHttpStatusCode = -1;
    protected Context mCallerContext = null;

    protected KeloudClient mClient = null;
    protected PostMethod mPost = null;
    protected JSONObject mResponseJson = null;
    protected JSONObject mRequestJson = null;
    /**
     * Constructor which sets mCallerContext and makes a new HttpClient
     * @param context
     */
    public BaseAuthOperation(Context context){
        // Initializing mResult, so if any error occurs it doesn't return null
        mResult = new RemoteOperationResult(false,495,null);
        resultSetMsgData("not received any msg");

        mCallerContext = context;
        Uri uri = Uri.parse(mCallerContext.getString(R.string.server_url));
        mClient = KeloudClientFactory.createKeloudClient(uri, mCallerContext, true);
        mClient.getParams().setParameter("http.useragent", "Keloud Android Client");

        mRequestJson = new JSONObject();
        mResponseJson = new JSONObject();
    }

    protected void sendHttpPostOrPutRequest(EntityEnclosingMethod postOrPutMethod){
        try {
            String requestBody = mRequestJson.toString();
            Log_OC.d(TAG, "http request body = "+requestBody);
            StringRequestEntity requestEntity = new StringRequestEntity(requestBody,"application/json","UTF-8");
            postOrPutMethod.setRequestEntity(requestEntity);
            mHttpStatusCode = mClient.executeMethod(postOrPutMethod);
            Log_OC.d(TAG, "http request completed with response code = "+mHttpStatusCode);

            mResult = new RemoteOperationResult(mHttpStatusCode == HttpStatus.SC_OK,
                    mHttpStatusCode, postOrPutMethod.getResponseHeaders());

            //TODO: Check whether you can use getResponseBodyAsString() and whether it removes BOM?
            byte[] responseBody = postOrPutMethod.getResponseBody();
            //Checking whether response body has unicode BOM?
            byte[] trimmedResponseBody = checkAndRemoveBOM(responseBody);
            String responseBodyString = new String(trimmedResponseBody);
            Log_OC.d(TAG, "resposeBody = " + responseBodyString);

            mResponseJson = new JSONObject(responseBodyString);
            // Returning mResponseJson data as mResult.mData is responsibility of
            // sendHttpPostOrPutRequestAndFillWithMsg or the child class itself
        }catch(JSONException e){
            Log_OC.d(TAG, ": JSONException thrown: " + e.getMessage());
            mResult = new RemoteOperationResult(false, mHttpStatusCode, postOrPutMethod.getResponseHeaders());
            resultSetMsgData(mCallerContext.getString(R.string.auth_json_error));
        }catch (UnsupportedEncodingException e){
            Log_OC.d(TAG, ": UnsupportedEncodingException thrown: " + e.getMessage());
            resultSetMsgData(mCallerContext.getString(R.string.auth_unsupported_encoding_error));
        }catch (HttpException e){
            Log_OC.d(TAG, ": HttpException thrown: " + e.getMessage());
            resultSetMsgData(mCallerContext.getString(R.string.auth_http_error));
        }catch (IOException e){
            Log_OC.d(TAG, ": IOException thrown: " + e.getMessage());
            resultSetMsgData(mCallerContext.getString(R.string.auth_timeout_title));
        }finally {
            postOrPutMethod.releaseConnection();
        }
    }

    protected void sendHttpPostOrPutRequestAndFillWithMsg(EntityEnclosingMethod postOrPutMethod){
        try {
            sendHttpPostOrPutRequest(postOrPutMethod);
            resultSetMsgData(mResponseJson.getString("msg"));
        }catch(JSONException e) {
            Log_OC.d(TAG, ": JSONException thrown: " + e.getMessage());
            mResult = new RemoteOperationResult(false, mHttpStatusCode, postOrPutMethod.getResponseHeaders());
            resultSetMsgData(mCallerContext.getString(R.string.auth_json_error));
        }
    }

    /**
     * @return Is setting message of mResult's data successful?
     */
    protected boolean resultSetMsgData (String msg){
        try{
            ArrayList<Object> msgData = new ArrayList<Object>(1);
            msgData.add(msg);
            mResult.setData(msgData);
            return true;
        }catch(Exception e){
            Log_OC.d(TAG, ": Exception thrown in setting msgData of mResult: " + e.getMessage());
            return false;
        }
    }

    public byte[] checkAndRemoveBOM(byte[] input){
        if (input.length<3)
            return input;

        // If had UTF8 BOM
        if ((input[0] == (byte) 0xEF) && (input[1] == (byte) 0xBB) && (input[2] == (byte) 0xBF)) {
            return Arrays.copyOfRange(input, 3, input.length);
        }else{
            return input;
        }
    }
}
