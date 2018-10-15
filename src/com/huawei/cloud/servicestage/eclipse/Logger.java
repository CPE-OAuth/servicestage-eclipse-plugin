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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

/**
 * @author Farhan Arshad
 */
public class Logger {

    private static ILog logger = Activator.getDefault().getLog();

    public static void info(String title, String message) {
        logger.log(new Status(Status.INFO, Activator.PLUGIN_ID,
                title + "; " + message));
    }

    public static void info(String title, String message, String details) {
        logger.log(new Status(Status.INFO, Activator.PLUGIN_ID,
                title + "; " + message + "; " + details));
    }

    public static void info(String message) {
        logger.log(new Status(Status.INFO, Activator.PLUGIN_ID, message));
    }

    public static void exception(Exception e) {
        exception("", e);
    }

    public static void exception(String message, Exception e) {
        logger.log(new Status(Status.ERROR, Activator.PLUGIN_ID, message, e));
    }

    public static void error(String message) {
        logger.log(new Status(Status.ERROR, Activator.PLUGIN_ID, message));
    }
}
