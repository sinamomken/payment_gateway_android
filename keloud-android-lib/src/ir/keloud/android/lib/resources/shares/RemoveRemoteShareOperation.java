/* keloud Android Library is available under MIT license
 *   Copyright (C) 2015 keloud Inc.
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 *   BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 *   ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *
 */

package ir.keloud.android.lib.resources.shares;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;

import ir.keloud.android.lib.common.KeloudClient;
import ir.keloud.android.lib.common.operations.RemoteOperation;
import ir.keloud.android.lib.common.operations.RemoteOperationResult;
import ir.keloud.android.lib.common.operations.RemoteOperationResult.ResultCode;
import ir.keloud.android.lib.common.utils.Log_OC;

/**
 * Remove a share
 * 
 * @author masensio
 *
 */

public class RemoveRemoteShareOperation extends RemoteOperation {

	private static final String TAG = RemoveRemoteShareOperation.class.getSimpleName();
	
	private int mRemoteShareId;
	
	/**
	 * Constructor
	 * 
	 * @param remoteShareId		Share ID
	 */
	
	public RemoveRemoteShareOperation(int remoteShareId) {
		mRemoteShareId = remoteShareId;
		
	}

	@Override
	protected RemoteOperationResult run(KeloudClient client) {
		RemoteOperationResult result = null;
		int status = -1;

		DeleteMethod delete = null;

		try {
			String id = "/" + String.valueOf(mRemoteShareId);
			delete = new DeleteMethod(client.getBaseUri() + ShareUtils.SHARING_API_PATH + id);

			delete.addRequestHeader(OCS_API_HEADER, OCS_API_HEADER_VALUE);
            
			status = client.executeMethod(delete);

			if(isSuccess(status)) {
				String response = delete.getResponseBodyAsString();

				result = new RemoteOperationResult(ResultCode.OK);
				
				// Parse xml response
				// convert String into InputStream
				InputStream is = new ByteArrayInputStream(response.getBytes());
				ShareXMLParser xmlParser = new ShareXMLParser();
				xmlParser.parseXMLResponse(is);
				if (xmlParser.isSuccess()) {
					result = new RemoteOperationResult(ResultCode.OK);
				} else if (xmlParser.isFileNotFound()){
					result = new RemoteOperationResult(ResultCode.SHARE_NOT_FOUND);
				} else {
					result = new RemoteOperationResult(false, status, delete.getResponseHeaders());	
				}
				
				Log_OC.d(TAG, "Unshare " + id + ": " + result.getLogMessage());
			} else {
				result = new RemoteOperationResult(false, status, delete.getResponseHeaders());
			}
		} catch (Exception e) {
			result = new RemoteOperationResult(e);
			Log_OC.e(TAG, "Unshare Link Exception " + result.getLogMessage(), e);

		} finally {
			if (delete != null)
				delete.releaseConnection();
		}
		return result;
	}


	private boolean isSuccess(int status) {
		return (status == HttpStatus.SC_OK);
	}
}