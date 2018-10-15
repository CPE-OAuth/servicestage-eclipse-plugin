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

/**
 * @author Farhan Arshad
 */
public class AppStatus {

    public static String UNKNOWN = "UNKNOWN";

    public static String INITIALIZING = "INITIALIZING";

    public static String UPGRADING = "UPGRADING";

    public static String FAILED = "FAILED";

    public static String DELETING = "DELETING";

    public static String SUCCEEDED = "SUCCEEDED";

    public static String RUNNING = "RUNNING";

    private final String status;

    private final String details;

    public AppStatus(String status, String details) {
        this.status = status;
        this.details = details;
    }

    public String getStatus() {
        return this.status;
    }

    public String getDetails() {
        return this.details;
    }
}
