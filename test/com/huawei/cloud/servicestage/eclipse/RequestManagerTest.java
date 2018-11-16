/**
 * Copyright 2016 - 2018 Huawei Technologies Co., Ltd. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.cloud.servicestage.eclipse;

import static org.junit.Assert.*;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.huawei.cloud.servicestage.eclipse.Activator;
import com.huawei.cloud.servicestage.eclipse.RequestManager;
import com.huawei.cloud.servicestage.eclipse.preferences.PreferenceConstants;
import com.huawei.cloud.servicestage.client.Token;
import com.huawei.cloud.servicestage.client.Util;

public class RequestManagerTest {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @BeforeClass
    public static void setPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        store.putValue(PreferenceConstants.REGION_CHOICE, AuthHelper.REGION);
        store.putValue(PreferenceConstants.USERNAME, AuthHelper.USERNAME);
        store.putValue(PreferenceConstants.PASSWORD, AuthHelper.PASSWORD);
    }

    @Before
    @After
    public void cleanupPreferences() {
        // remove token from storage
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.putValue(PreferenceConstants.TOKEN, null);
    }

    @Test
    public void testGetExistingValidAuthToken() throws IOException {
        // add test token that expires in 2099
        String testTokenValue = "testToken";
        Token token = new Token(AuthHelper.USERNAME, AuthHelper.REGION,
                testTokenValue, AuthHelper.TENANT_ID,
                Util.stringToDate("2099-08-23T21:40:09.922000Z"));

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.putValue(PreferenceConstants.TOKEN, token.toString());

        // manager should return test token above
        assertEquals(testTokenValue,
                RequestManager.getInstance().getAuthToken().getToken());
    }

    @Test
    public void testGenerateNewAuthToken() throws IOException {
        String token = RequestManager.getInstance().getAuthToken(true)
                .getToken();
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
}
