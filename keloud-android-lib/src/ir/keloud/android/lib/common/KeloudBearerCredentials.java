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
package ir.keloud.android.lib.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;

import ir.keloud.android.lib.common.network.BearerAuthScheme;
import ir.keloud.android.lib.common.network.BearerCredentials;

public class KeloudBearerCredentials implements KeloudCredentials {

	private String mAccessToken;
	
	public KeloudBearerCredentials(String accessToken) {
		mAccessToken = accessToken != null ? accessToken : "";
	}

	@Override
	public void applyTo(KeloudClient client) {
	    AuthPolicy.registerAuthScheme(BearerAuthScheme.AUTH_POLICY, BearerAuthScheme.class);
	    
	    List<String> authPrefs = new ArrayList<String>(1);
	    authPrefs.add(BearerAuthScheme.AUTH_POLICY);
	    client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);        
	    
	    client.getParams().setAuthenticationPreemptive(true);
	    client.getState().setCredentials(
	    		AuthScope.ANY, 
	    		new BearerCredentials(mAccessToken)
		);
	}

	@Override
	public String getUsername() {
		// its unknown
		return null;
	}
	
	@Override
	public String getAuthToken() {
		return mAccessToken;
	}

	@Override
	public boolean authTokenExpires() {
		return true;
	}

}
