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
public class GetPaymentUrlOperation extends BaseAuthOperation implements Runnable{
    private static final String TAG = GetPaymentUrlOperation.class.getSimpleName();

    private String mToken = "";
    private String mIPGId = "";
    private int mToman = 0;
    private String mDesc = "";
    private String mEmail = "";

    /**
     * {@inheritDoc}
     */
    public GetPaymentUrlOperation(Context context){
        super(context);
    }

    public RemoteOperationResult getPaymentUrl(String token,
                                               String ipgId,
                                               int toman,
                                               String desc,
                                               String email,
                                               String mobile){
        Log_OC.d(TAG, "Get payment url with"
                + " token = " + token
                + " ipgId = " + ipgId
                + " toman = " + toman
                + " desc = " + desc
                + " email = " + email
                + " mobile = " + mobile);
        mToken = token;
        mIPGId = ipgId;
        mToman = toman;
        mDesc = desc;
        mEmail = email;
        mMobileNumber = mobile;
        run();
        return mResult;
    }

    public void run(){
        mPost = new PostMethod(mCallerContext.getString(R.string.payment_gateway_server_url)+"/payment/makePayment");
        try{
            //Adds token header.
            mPost.setRequestHeader("token", mToken);

            mRequestJson.put("IPGId",mIPGId);
            mRequestJson.put("amount",Integer.toString(mToman));
            mRequestJson.put("desc",mDesc);
            mRequestJson.put("email",mEmail);
            mRequestJson.put("mobile",mMobileNumber);

            sendHttpPostOrPutRequest(mPost);

            //Filling mResult.mData with data from mResponseJson
            ArrayList<Object> mResultData = new ArrayList<Object>(6);
            mResultData.add(mResponseJson.getInt("Status"));
            mResultData.add(mResponseJson.getString("Authority"));
            mResultData.add(mResponseJson.getString("IPGName"));
            mResultData.add(mResponseJson.getString("IPGUrl"));
            mResultData.add(mResponseJson.getString("USSD"));
            mResultData.add(mResponseJson.getInt("paymentMethod"));
            mResult.setData(mResultData);
        }catch(JSONException e){
            Log_OC.d(TAG, "JSONException thrown: " + e.getMessage());
            mResult = new RemoteOperationResult(false,495,null);
            resultSetMsgData("JSONException thrown: " + e.getMessage());
        }
    }
}
