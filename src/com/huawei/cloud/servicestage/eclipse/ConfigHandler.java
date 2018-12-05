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
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Farhan Arshad
 */
public class ConfigHandler extends ServiceStageHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Integer result = (Integer) super.execute(event);
        if (result != 0) {
            return result;
        }

        // if using RequestManager.getInstance(), then some of the data
        // is persisted and opening the config dialog is faster
        // however, if the user makes a change a resource on the Huawei Cloud
        // these changes will not be picked up until eclipse is restarted
        // for now, create a new RequestManager each time until a
        // refresh button is added - this will force a refresh of the
        // persisted data
        // RequestManager requestManager = RequestManager.getInstance();
        RequestManager requestManager = new RequestManager();

        // the wizard we want to show
        Wizard wizard = new ConfigWizard(project);
        wizard.addPage(new AppConfigWizardPage(requestManager));
        wizard.addPage(new ServicesConfigWizardPage(requestManager));
        /*- ExtendedParamsConfigWizardPage is not being added here b/c it 
        depends on a value from AppConfigWizardPage, however, if we
        add the page here, it doesn't have that value yet
        to get around this, ExtendedParamsConfigWizardPage is added
        in the getNextPage method of ServicesConfigWizardPage
        this means when the ExtendedParamsConfigWizardPage object is created
        it has access to the value needed from AppConfigWizardPage */
        // wizard.addPage(new ExtendedParamsConfigWizardPage(requestManager));
        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);

        // needs to be opened inside an Action b/c it will be opened by another
        // thread
        Action action = new Action() {
            public void run() {
                dialog.open();
            }
        };

        // perform the ReqestManger#load operation inside a
        // progressmonitordialog because it takes a few seconds
        ProgressMonitorDialog loading = new ProgressMonitorDialog(
                window.getShell());
        try {
            loading.run(true, true, monitor -> {
                SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

                subMonitor.worked(10);
                try {
                    requestManager.load(subMonitor.newChild(90));
                } catch (IOException e) {
                    Util.showJobExceptionDialog(e.getMessage(),
                            window.getShell(), e);
                    return;
                }

                // once load is done, show the config dialog
                if (!monitor.isCanceled()) {
                    Display.getDefault().asyncExec(() -> {
                        action.run();
                    });
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            Util.showJobExceptionDialog(e.getMessage(), window.getShell(), e);
        }

        return 0;
    }
}
