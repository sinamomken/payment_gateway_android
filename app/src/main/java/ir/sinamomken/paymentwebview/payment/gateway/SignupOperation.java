package ir.sinamomken.paymentwebview.payment.gateway;

import android.content.Context;

import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;

import java.util.ArrayList;

import ir.keloud.android.lib.common.operations.RemoteOperationResult;
import ir.keloud.android.lib.common.utils.Log_OC;
import ir.sinamomken.paymentwebview.R;
import ir.sinamomken.paymentwebview.auth_keloud.BaseAuthOperation;

/**
 * Created by momken on 8/26/15.
 */
public class SignupOperation extends BaseAuthOperation implements Runnable{
    private static final String TAG = SignupOperation.class.getSimpleName();

    private String mName;
    private String mGatewayPwd;
    private String mEmail;

    /**
     * {@inheritDoc}
     */
    public SignupOperation(Context context){
        super(context);
    }

    public RemoteOperationResult signup(String name,
                                  String password,
                                  String email,
                                  String phone){
        Log_OC.d(TAG, "Signup operation with"
                + " name = " + name
                + " password = " + password
                + " email = " + email
                + " phone = " + phone);
        mName = name;
        mGatewayPwd = password;
        mEmail = email;
        mMobileNumber = phone;
        run();
        return mResult;
    }

    public void run(){
        mPost = new PostMethod(mCallerContext.getString(R.string.payment_gateway_server_url)+"/application/signup");
        try{
            mRequestJson.put("name",mName);
            mRequestJson.put("password",mGatewayPwd);
            mRequestJson.put("email",mEmail);
            mRequestJson.put("phone",mMobileNumber);
            sendHttpPostOrPutRequest(mPost);

            //Filling mResult.mData with data from mResponseJson
            ArrayList<Object> mResultData = new ArrayList<Object>(2);
            mResultData.add(mResponseJson.getString("message"));
            mResultData.add(mResponseJson.getString("appId"));
            mResult.setData(mResultData);
        }catch(JSONException e){
            Log_OC.d(TAG, "JSONException thrown: " + e.getMessage());
            mResult = new RemoteOperationResult(false,495,null);
            resultSetMsgData("JSONException thrown: " + e.getMessage());
        }
    }
}
