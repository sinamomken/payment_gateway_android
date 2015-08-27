package ir.sinamomken.paymentwebview.payment.gateway;

import android.content.Context;

import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import ir.keloud.android.lib.common.operations.RemoteOperationResult;
import ir.keloud.android.lib.common.utils.Log_OC;
import ir.sinamomken.paymentwebview.R;
import ir.sinamomken.paymentwebview.auth_keloud.BaseAuthOperation;

/**
 * Created by momken on 8/26/15.
 */
public class SigninOperation extends BaseAuthOperation implements Runnable{
    private static final String TAG = SigninOperation.class.getSimpleName();

    private String mGatewayPwd;
    private String mAppId;

    /**
     * {@inheritDoc}
     */
    public SigninOperation(Context context){
        super(context);
    }

    public RemoteOperationResult signin(String password, String appId){
        Log_OC.d(TAG, "Signin operation with password = " + password + " appId = " + appId);

        mGatewayPwd = password;
        mAppId = appId;
        run();
        return mResult;
    }

    public void run(){
        mPost = new PostMethod(mCallerContext.getString(R.string.payment_gateway_server_url)+"/application/signin");
        try{
            mRequestJson.put("password",mGatewayPwd);
            mRequestJson.put("appId",mAppId);
            sendHttpPostOrPutRequest(mPost);

            //Filling mResult.mData with data from mResponseJson
            ArrayList<Object> mResultData = new ArrayList<Object>(6);
            mResultData.add(mResponseJson.getString("id"));
            mResultData.add(mResponseJson.getString("email"));
            mResultData.add(mResponseJson.getString("name"));
            mResultData.add(mResponseJson.getString("phone"));
            JSONArray rolesArray = mResponseJson.getJSONArray("roles");
            mResultData.add(rolesArray.getJSONObject(0).getString("roleName"));
            mResultData.add(mResponseJson.getString("token"));
            mResult.setData(mResultData);
        }catch(JSONException e){
            Log_OC.d(TAG, "JSONException thrown: " + e.getMessage());
            mResult = new RemoteOperationResult(false,495,null);
            resultSetMsgData("JSONException thrown: " + e.getMessage());
        }
    }
}
