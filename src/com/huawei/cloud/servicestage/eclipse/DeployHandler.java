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

import java.io.File;
import java.io.IOException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.huawei.cloud.servicestage.client.SimpleResponse;

public class DeployHandler extends ServiceStageHandler {
    // timeout after 6 minutes
    private static final long TIMEOUT = 5 * 60 * 1000;

    // check status every 5 seconds
    private static final long INCREMENT = 5 * 1 * 1000;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Integer result = (Integer) super.execute(event);
        if (result != 0) {
            return result;
        }

        Shell shell = window.getShell();

        File settingsFile = Util.getSettingsFile(project);

        if (settingsFile == null || !settingsFile.exists()) {
            MessageDialog.openError(shell, DIALOG_NO_SETTINGS_FILE_TITLE,
                    DIALOG_NO_SETTINGS_FILE_MESSAGE);
            return -1;
        }

        // a file or a project can be deployed
        // if the project is supported, it will be zipped and the zip file will
        // be deployed
        boolean supportedProject = hasNature(this.project,
                "org.eclipse.wst.jsdt.core.jsNature")
                || hasNature(this.project, "org.eclipse.php.core.PHPNature");

        // make sure a either file is selected, or project is supported
        if (this.file == null && !supportedProject) {
            MessageDialog.openError(shell, DIALOG_NO_RESOURCE_SELECTED_TITLE,
                    DIALOG_NO_FILE_SELECTED_MESSAGE);
            return -1;
        }

        // get file path
        String dialogMessage = null;
        String lfp = null;
        if (supportedProject) {
            try {
                lfp = Util.createZipFile(this.project);
            } catch (IOException e) {
                Util.showExceptionDialog("Error creating project zip file.",
                        shell, e);
                return -1;
            }
            dialogMessage = String.format(DIALOG_DEPLOY_MESSAGE,
                    this.project.getName());
        } else if (this.file != null) {
            lfp = file.getRawLocation().makeAbsolute().toString();
            dialogMessage = String.format(DIALOG_DEPLOY_MESSAGE,
                    file.getProjectRelativePath().toString());
        }

        // final needed
        final String localFilePath = lfp;

        // ask user to confirm deployment
        boolean answer = MessageDialog.openConfirm(shell, DIALOG_DEPLOY_TITLE,
                dialogMessage);

        if (!answer) {
            return 0;
        }

        // deployment job
        // 1. upload
        // 2. deploy
        // 3. monitor deployment
        // 4. get app url
        String jobName = String.format(JOB_DEPLOY_NAME, localFilePath);
        Job job = new Job(jobName) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

                // upload
                subMonitor.setTaskName(
                        String.format(JOB_DEPLOY_UPLOAD, localFilePath));
                subMonitor.worked(5);
                try {
                    String uploadUrl = RequestManager.getInstance()
                            .upload(localFilePath, project);

                    IDialogSettings ds = Util.loadDialogSettings(project);
                    ds.put(ConfigConstants.SOURCE_PATH, uploadUrl);
                    Util.saveDialogSettings(project, ds);

                    subMonitor.worked(20);

                    Logger.info(
                            "Uploaded " + localFilePath + " to " + uploadUrl);
                } catch (Exception e) {
                    // error while uploading
                    Util.showJobExceptionDialog(ERROR, JOB_DEPLOY_UPLOAD_FAILED,
                            shell, e);
                    return Status.OK_STATUS;
                }

                if (monitor.isCanceled()) {
                    return Status.OK_STATUS;
                }

                // deploy
                subMonitor.setTaskName(JOB_DEPLOY_DEPLOY);
                subMonitor.worked(5);
                SimpleResponse response;
                try {
                    response = RequestManager.getInstance()
                            .createOrUpdateApplication(project);

                    subMonitor.worked(5);

                    if (!response.isOk()) {
                        Util.showJobInfoDialog(ERROR, JOB_DEPLOY_DEPLOY_FAILED,
                                response.getMessage(), shell);
                        return Status.OK_STATUS;
                    }

                    Logger.info(
                            "Deployment request accepted. Monitoring status now...");
                } catch (Exception e) {
                    // error while deploying
                    Util.showJobExceptionDialog(JOB_DEPLOY_DEPLOY_FAILED, shell,
                            e);
                    return Status.OK_STATUS;
                }

                if (monitor.isCanceled()) {
                    return Status.OK_STATUS;
                }

                // monitor
                long totalSleep = 0;
                AppStatus status = null;
                try {
                    // check status every 5 seconds
                    status = RequestManager.getInstance()
                            .getApplicationStatus(project);
                    subMonitor.setTaskName(status.getStatus());
                    subMonitor.worked(5);
                    Logger.info(status.getStatus());
                    while ((status.getStatus().equals(AppStatus.INITIALIZING)
                            || status.getStatus().equals(AppStatus.UPGRADING))
                            && !monitor.isCanceled()) {
                        subMonitor.worked(1);

                        status = RequestManager.getInstance()
                                .getApplicationStatus(project);

                        subMonitor.setTaskName(status.getStatus());
                        Logger.info(status.getStatus());

                        // sleep
                        Thread.sleep(INCREMENT);
                        totalSleep += INCREMENT;

                        // check if timeout
                        if (totalSleep > TIMEOUT) {
                            Util.showJobInfoDialog(JOB_DEPLOY_MONITOR_TIMEOUT,
                                    JOB_DEPLOY_MONITOR_TIMEOUT, shell);
                            return Status.OK_STATUS;
                        }
                    }
                } catch (Exception e) {
                    // error while monitoring
                    Util.showJobExceptionDialog(JOB_DEPLOY_MONITOR_ERROR, shell,
                            e);
                    return Status.OK_STATUS;
                }

                if (monitor.isCanceled()) {
                    return Status.OK_STATUS;
                }

                if (status == null
                        || !status.getStatus().equals(AppStatus.RUNNING)) {
                    try {
                        String taskLogs = RequestManager.getInstance()
                                .getApplicationTaskLogs(project);
                        Util.showJobInfoDialog(FAILED, JOB_DEPLOY_DEPLOY_FAILED,
                                taskLogs, shell);
                        return Status.OK_STATUS;
                    } catch (IOException e) {
                        Util.showJobExceptionDialog(JOB_DEPLOY_MONITOR_ERROR,
                                shell, e);
                        return Status.OK_STATUS;
                    }
                }

                // get app url
                subMonitor.setTaskName(JOB_DEPLOY_URL);
                subMonitor.worked(5);
                try {
                    String url = RequestManager.getInstance()
                            .getApplicationUrl(project);

                    String message = String.format(JOB_DEPLOY_SUCCESSFUL,
                            localFilePath, url);

                    // deployment was successful
                    Util.showJobInfoDialog(SUCCESSFUL, message, shell);
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    // getting app url failed
                    Util.showJobExceptionDialog(JOB_DEPLOY_URL_FAILED, shell,
                            e);
                    return Status.OK_STATUS;
                }
            }
        };

        job.setUser(true);
        job.setPriority(Job.LONG);
        job.schedule();

        return 0;
    }

    private boolean hasNature(IProject project, String natureId) {
        try {
            return project.getNature(natureId) != null;
        } catch (CoreException e) {
            return false;
        }
    }
}
