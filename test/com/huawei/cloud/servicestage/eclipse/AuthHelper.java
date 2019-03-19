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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.huawei.cloud.servicestage.client.AuthClient;
import com.huawei.cloud.servicestage.client.Token;

public class AuthHelper {
    public static final String REGION = "cn-north-1";

    public static final String USERNAME = "";

    public static final String PASSWORD = "";

    public static final String TENANT_ID = "";
    
    public static final String DOMAIN = "";

    private static AuthHelper instance = null;

    private Token token = null;

    protected AuthHelper() throws IOException {
        this.token = AuthClient.getAuthToken(REGION, USERNAME, PASSWORD, DOMAIN);
        assertNotNull("Failed to get Auth Token.", this.token);
    }

    public static AuthHelper getInstance() throws IOException {
        if (instance == null) {
            instance = new AuthHelper();
        }

        return instance;
    }

    public Token getToken() {
        return this.token;
    }
}
