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
package com.huawei.cloud.servicestage.eclipse.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.huawei.cloud.servicestage.eclipse.Activator;
import com.huawei.cloud.servicestage.eclipse.Resources;

import org.eclipse.ui.IWorkbench;

/**
 * @author Farhan Arshad
 */
public class ServiceStagePreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage, Resources {

    public ServiceStagePreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(PREFERENCES_SERVICESTAGE_DESCRIPTION);
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void createFieldEditors() {
        new Label(getFieldEditorParent(), SWT.NULL); // blank

        Group group = new Group(getFieldEditorParent(), SWT.NONE);
        initGroup(group);

        addField(new StringFieldEditor(PreferenceConstants.SERVICESTAGE_API_URL,
                PREFERENCES_SERVICESTAGE_API_URL, group));
        addField(new StringFieldEditor(PreferenceConstants.ARTIFACT_NAMESPACE,
                PREFERENCES_SERVICESTAGE_ARTIFACT_NAMESPACE, group));
        addField(
                new StringFieldEditor(PreferenceConstants.CAS_CLUSTER_NAMESPACE,
                        PREFERENCES_SERVICESTAGE_CAS_CLUSTER_NAMESPACE, group));
        addField(new StringFieldEditor(PreferenceConstants.SERVICE_ID,
                PREFERENCES_SERVICESTAGE_SERVICE_ID, group));
        addField(new StringFieldEditor(PreferenceConstants.PLAN_ID,
                PREFERENCES_SERVICESTAGE_PLAN_ID, group));
        addField(new StringFieldEditor(PreferenceConstants.ORGANIZATION_GUID,
                PREFERENCES_SERVICESTAGE_ORGANIZATION_GUID, group));
        addField(new StringFieldEditor(PreferenceConstants.SPACE_GUID,
                PREFERENCES_SERVICESTAGE_SPACE_GUID, group));
        addField(new StringFieldEditor(PreferenceConstants.CONTEXT_ORDER_ID,
                PREFERENCES_SERVICESTAGE_CONTEXT_ORDER_ID, group));
    }

    private void initGroup(Composite group) {
        GridLayout layout = new GridLayout();
        group.setLayout(layout);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 400;
        group.setLayoutData(gd);
    }

    public void init(IWorkbench workbench) {
    }

}