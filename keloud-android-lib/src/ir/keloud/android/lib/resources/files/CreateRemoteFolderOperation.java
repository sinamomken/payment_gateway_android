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

package ir.keloud.android.lib.resources.files;

import android.util.Log;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;

import ir.keloud.android.lib.common.KeloudClient;
import ir.keloud.android.lib.common.network.WebdavUtils;
import ir.keloud.android.lib.common.operations.RemoteOperation;
import ir.keloud.android.lib.common.operations.RemoteOperationResult;
import ir.keloud.android.lib.common.operations.RemoteOperationResult.ResultCode;
import ir.keloud.android.lib.common.utils.Log_OC;



/**
 * Remote operation performing the creation of a new folder in the keloud server.
 * 
 * @author David A. Velasco 
 * @author masensio
 *
 */
public class CreateRemoteFolderOperation extends RemoteOperation {
    
    private static final String TAG = CreateRemoteFolderOperation.class.getSimpleName();

    private static final int READ_TIMEOUT = 30000;
    private static final int CONNECTION_TIMEOUT = 5000;
    

    protected String mRemotePath;
    protected boolean mCreateFullPath;
    
    /**
     * Constructor
     * 
     * @param remotePath            Full path to the new directory to create in the remote server.
     * @param createFullPath        'True' means that all the ancestor folders should be created if don't exist yet.
     */
    public CreateRemoteFolderOperation(String remotePath, boolean createFullPath) {
        mRemotePath = remotePath;
        mCreateFullPath = createFullPath;
    }

    /**
     * Performs the operation
     * 
     * @param   client      Client object to communicate with the remote keloud server.
     */
    @Override
    protected RemoteOperationResult run(KeloudClient client) {
        RemoteOperationResult result = null;
        boolean noInvalidChars = FileUtils.isValidPath(mRemotePath);
        if (noInvalidChars) {
        	result = createFolder(client);
            Log_OC.d(TAG,"SinaMomken: result after initial createFolder() = " + result.getCode().toString());

            //SinaMomken: Checking for unknown condition
            if (!result.isSuccess() && mCreateFullPath &&
    				RemoteOperationResult.ResultCode.CONFLICT == result.getCode()) {
    			result = createParentFolder(FileUtils.getParentPath(mRemotePath), client);
                Log_OC.d(TAG,"SinaMomken: result after createParentFolder() = " + result.getCode().toString());
                if (result.isSuccess()) {
	    			result = createFolder(client);	// second (and last) try
                    Log_OC.d(TAG,"SinaMomken: result after second createFolder() = " + result.getCode().toString());
    			}
    		}

        } else {
        	result = new RemoteOperationResult(ResultCode.INVALID_CHARACTER_IN_NAME);
        }

        Log_OC.d(TAG,"SinaMomken: result at end of run() = " + result.getCode().toString());
        return result;
    }

    
    private RemoteOperationResult createFolder(KeloudClient client) {
        RemoteOperationResult result = null;
        MkColMethod mkcol = null;
    	try {
    		mkcol = new MkColMethod(client.getWebdavUri() + WebdavUtils.encodePath(mRemotePath));
    		int status =  client.executeMethod(mkcol, READ_TIMEOUT, CONNECTION_TIMEOUT);
    		result = new RemoteOperationResult(mkcol.succeeded(), status, mkcol.getResponseHeaders());
            Log_OC.d(TAG, "SinaMomken: middle of createFolder()");
    		Log_OC.d(TAG, "Create directory " + mRemotePath + ": " + result.getLogMessage());
    		client.exhaustResponse(mkcol.getResponseBodyAsStream());

    	} catch (Exception e) {
    		result = new RemoteOperationResult(e);
    		Log_OC.e(TAG, "Create directory " + mRemotePath + ": " + result.getLogMessage(), e);

    	} finally {
    		if (mkcol != null)
    			mkcol.releaseConnection();
    	}
    	return result;
	}

	private RemoteOperationResult createParentFolder(String parentPath, KeloudClient client) {
        RemoteOperation operation = new CreateRemoteFolderOperation(parentPath,
                                                                mCreateFullPath);
        return operation.execute(client);
    }
    
   

}
