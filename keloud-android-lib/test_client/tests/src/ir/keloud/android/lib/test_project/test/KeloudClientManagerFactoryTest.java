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
package ir.keloud.android.lib.test_project.test;

import ir.keloud.android.lib.common.KeloudClientManager;
import ir.keloud.android.lib.common.KeloudClientManagerFactory;
import ir.keloud.android.lib.common.SingleSessionManager;
import ir.keloud.android.lib.common.KeloudClientManagerFactory.Policy;
import ir.keloud.android.lib.common.SimpleFactoryManager;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Unit test for KeloudClientManagerFactory
 * 
 * @author David A. Velasco
 */
public class KeloudClientManagerFactoryTest extends TestCase {
	
	@Override
	protected void setUp() {
		KeloudClientManagerFactory.setDefaultPolicy(Policy.ALWAYS_NEW_CLIENT);
	}

	public void testGetDefaultPolicy() {
		Policy defaultPolicy = KeloudClientManagerFactory.getDefaultPolicy();
		assertNotNull("Returned null value", defaultPolicy);
		assertTrue("Returned unknown value", 
						(Policy.ALWAYS_NEW_CLIENT.equals(defaultPolicy) ||
						(Policy.SINGLE_SESSION_PER_ACCOUNT.equals(defaultPolicy))));
	}
	
	public void testSetDefaultPolicy() {
		KeloudClientManagerFactory.setDefaultPolicy(Policy.SINGLE_SESSION_PER_ACCOUNT);
		Policy defaultPolicy = KeloudClientManagerFactory.getDefaultPolicy();
		assertEquals("SINGLE_SESSION_PER_ACCOUNT not set", 
				Policy.SINGLE_SESSION_PER_ACCOUNT, defaultPolicy);
		
		KeloudClientManagerFactory.setDefaultPolicy(Policy.ALWAYS_NEW_CLIENT);
		defaultPolicy = KeloudClientManagerFactory.getDefaultPolicy();
		assertEquals("ALWAYS_NEW_CLIENT not set", Policy.ALWAYS_NEW_CLIENT, defaultPolicy);
		
		try {
			KeloudClientManagerFactory.setDefaultPolicy(null);
			throw new AssertionFailedError("Accepted NULL parameter");
			
		} catch(Exception e) {
			assertTrue("Unexpected exception when setting default policy null", 
					(e instanceof IllegalArgumentException));
		}
		defaultPolicy = KeloudClientManagerFactory.getDefaultPolicy();
		assertEquals("ALWAYS_NEW_CLIENT changed after setting null", 
				Policy.ALWAYS_NEW_CLIENT, defaultPolicy);
		
	}

	
	public void testGetDefaultSingleton() {
		KeloudClientManager mgr = KeloudClientManagerFactory.getDefaultSingleton();
		assertNotNull("Returned NULL default singleton", mgr);
		assertTrue("Default singleton does not implement default policy", 
				mgr instanceof SimpleFactoryManager);
		
		KeloudClientManager mgr2 = KeloudClientManagerFactory.getDefaultSingleton();
		assertSame("Not singleton", mgr, mgr2);
		
		KeloudClientManagerFactory.setDefaultPolicy(Policy.SINGLE_SESSION_PER_ACCOUNT);
		mgr = KeloudClientManagerFactory.getDefaultSingleton();
		assertNotNull("Returned NULL default singleton", mgr);
		assertTrue("Default singleton does not implement default policy", 
				mgr instanceof SingleSessionManager);
		
		mgr2 = KeloudClientManagerFactory.getDefaultSingleton();
		assertSame("Not singleton", mgr, mgr2);
	}
    
	
	public void testNewDefaultKeloudClientManager() {
		KeloudClientManager mgr = KeloudClientManagerFactory.newDefaultKeloudClientManager();
		assertNotNull("Returned NULL default manager", mgr);
		assertTrue("New manager does not implement default policy", 
				mgr instanceof SimpleFactoryManager);
		assertNotSame("Not new instance", 
				mgr, KeloudClientManagerFactory.getDefaultSingleton());
		assertNotSame("Not new instance", 
				mgr, KeloudClientManagerFactory.newDefaultKeloudClientManager());
		
		KeloudClientManagerFactory.setDefaultPolicy(Policy.SINGLE_SESSION_PER_ACCOUNT);
		mgr = KeloudClientManagerFactory.newDefaultKeloudClientManager();
		assertNotNull("Returned NULL default manager", mgr);
		assertTrue("New manager does not implement default policy", 
				mgr instanceof SingleSessionManager);
		assertNotSame("Not new instance", 
				mgr, KeloudClientManagerFactory.getDefaultSingleton());
		assertNotSame("Not new instance", 
				mgr, KeloudClientManagerFactory.newDefaultKeloudClientManager());
		
	}
	
	
	public void testNewKeloudClientManager() {
		KeloudClientManager mgr = KeloudClientManagerFactory.
				newKeloudClientManager(Policy.ALWAYS_NEW_CLIENT);
		
		assertNotNull("Returned NULL manager", mgr);
		assertTrue("New manager does not implement policy ALWAYS_NEW_CLIENT", 
				mgr instanceof SimpleFactoryManager);
		assertNotSame("Not new instance", 
				mgr, KeloudClientManagerFactory.getDefaultSingleton());
		assertNotSame("Not new instance", 
				mgr, KeloudClientManagerFactory.newDefaultKeloudClientManager());
		assertNotSame("Not new instance", 
				mgr, KeloudClientManagerFactory.newKeloudClientManager(
						Policy.ALWAYS_NEW_CLIENT));
		
		
		KeloudClientManager mgr2 = KeloudClientManagerFactory.
				newKeloudClientManager(Policy.SINGLE_SESSION_PER_ACCOUNT);
		
		assertNotNull("Returned NULL manager", mgr2);
		assertTrue("New manager does not implement policy SINGLE_SESSION_PER_ACCOUNT", 
				mgr2 instanceof SingleSessionManager);
		assertNotSame("Not new instance", 
				mgr2, KeloudClientManagerFactory.getDefaultSingleton());
		assertNotSame("Not new instance", 
				mgr2, KeloudClientManagerFactory.newDefaultKeloudClientManager());
		assertNotSame("Not new instance", 
				mgr2, KeloudClientManagerFactory.newKeloudClientManager(
						Policy.SINGLE_SESSION_PER_ACCOUNT));
	}

	
}
