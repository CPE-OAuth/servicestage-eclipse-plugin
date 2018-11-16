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

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

public class StatusHandler extends ServiceStageHandler implements Resources {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Integer result = (Integer) super.execute(event);
        if (result != 0) {
            return result;
        }

        Shell shell = window.getShell();

        AppStatus status = null;
        try {
            status = RequestManager.getInstance().getApplicationStatus(project);
        } catch (IOException e) {
            Util.showExceptionDialog(DIALOG_STATUS_ERROR, shell, e);
            return -1;
        }

        String url = null;
        if (status.getStatus().equals(AppStatus.RUNNING)) {
            try {
                url = RequestManager.getInstance().getApplicationUrl(project);
            } catch (IOException e) {
                Logger.exception("Failed to get application URL", e);
            }
        }

        String message;
        if (url == null) {
            message = String.format(DIALOG_STATUS_MESSAGE, status.getStatus());
        } else {
            message = String.format(DIALOG_STATUS_URLMESSAGE,
                    status.getStatus(), url);
        }

        Logger.info(status.getDetails());

        Util.showInfoDialog(status.getStatus(), message, window.getShell());

        return 0;
    }
}
