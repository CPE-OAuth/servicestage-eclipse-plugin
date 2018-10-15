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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * General handler that makes sure a project/file is selected.
 * 
 * @author Farhan Arshad
 */
public class ServiceStageHandler extends AbstractHandler implements Resources {

    protected IProject project;

    protected IResource file;

    protected IWorkbenchWindow window;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        this.window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        if (window != null) {
            // get the current selection then get the project of the selected
            // item
            ISelection selection = (ISelection) window.getSelectionService()
                    .getSelection();
            if (selection instanceof ITreeSelection) {
                TreeSelection treeSelection = (TreeSelection) selection;
                TreePath[] treePaths = treeSelection.getPaths();

                if (treePaths.length > 0) {
                    TreePath treePath = treePaths[0];

                    if (treePath.getSegmentCount() > 0) {
                        Object firstSegmentObj = treePath.getFirstSegment();
                        this.project = (IProject) ((IAdaptable) firstSegmentObj)
                                .getAdapter(IProject.class);
                    }

                    if (treePath.getSegmentCount() > 1) {
                        Object lastSegmentObj = treePath.getLastSegment();
                        this.file = (IResource) ((IAdaptable) lastSegmentObj)
                                .getAdapter(IResource.class);
                    }
                }
            }
        }

        if (this.project == null) {
            MessageDialog.openError(window.getShell(),
                    DIALOG_NO_RESOURCE_SELECTED_TITLE,
                    DIALOG_NO_RESOURCE_SELECTED_MESSAGE);
            return -1;
        }

        return 0;
    }
}
