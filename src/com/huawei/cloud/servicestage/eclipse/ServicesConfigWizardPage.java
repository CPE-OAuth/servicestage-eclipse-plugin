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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Farhan Arshad
 */
public class ServicesConfigWizardPage extends AbstractConfigWizardPage
        implements Resources {

    protected ServicesConfigWizardPage(RequestManager requestManger) {
        super(WIZARD_SERVICES_PAGE_PAGE_NAME, requestManger);
        setTitle(WIZARD_SERVICES_PAGE_TITLE);
        setDescription(WIZARD_SERVICES_PAGE_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        setPageComplete(true);

        // outer container
        Composite container = createContainer(parent);

        //
        // distributed caching service group
        //
        Group dcsGroup = createGroup(container,
                WIZARD_SERVICES_PAGE_DCS_GROUP_NAME);

        // dcs instances
        Map<String, String> dcsInstances = Collections.emptyMap();
        try {
            dcsInstances = this.getRequestManger().getDCSInstances();
        } catch (IOException e) {
            this.setErrorMessage(WIZARD_SERVICES_PAGE_DCS_ERROR);
            Util.showJobExceptionDialog(WIZARD_SERVICES_PAGE_DCS_ERROR,
                    parent.getShell(), e);
        }

        Combo dcsInstance = addDropdown(ConfigConstants.DCS_ID,
                WIZARD_SERVICES_PAGE_DCS_INSTANCE, dcsInstances, true, false,
                dcsGroup);

        // dcs password
        Text dcsPassword = addPasswordField(ConfigConstants.DCS_PASSWORD,
                WIZARD_SERVICES_PAGE_DCS_PASSWORD, true, dcsGroup);

        // only enable the dcs password field if a dcs instance is selected
        // since dcs instance is optional
        dcsInstance.addModifyListener(e -> {
            if (dcsInstance.getText().isEmpty()) {
                dcsPassword.setEnabled(false);
            } else {
                dcsPassword.setEnabled(true);
            }
        });

        // little trick to trigger a modify for the first time the page is
        // opened
        dcsInstance.setText(dcsInstance.getText());

        //
        // relational database group
        //
        Group rdbGroup = createGroup(container,
                WIZARD_SERVICES_PAGE_RDS_GROUP_NAME);

        // rds instance
        Map<String, String> rdsInstances = Collections.emptyMap();
        try {
            rdsInstances = this.getRequestManger().getRDSInstances();
        } catch (IOException e) {
            this.setErrorMessage(WIZARD_SERVICES_PAGE_RDS_ERROR);
            Util.showJobExceptionDialog(WIZARD_SERVICES_PAGE_RDS_ERROR,
                    parent.getShell(), e);
        }

        Combo rdsInstance = addDropdown(ConfigConstants.RDB_ID,
                WIZARD_SERVICES_PAGE_RDS_INSTANCE, rdsInstances, true, false,
                rdbGroup);

        // rds connection type
        Set<String> connectionTypes = new LinkedHashSet<>();
        connectionTypes.add("SPRING_CLOUD_CONNECTOR");
        connectionTypes.add("JNDI");

        Combo rdsConnectionTypeCombo = addDropdown(
                ConfigConstants.RDB_CONNECTION_TYPE,
                WIZARD_SERVICES_PAGE_RDS_CONNECTION_TYPE, connectionTypes, true,
                true, rdbGroup);

        // rds db anme
        Text rdsDbName = addField(ConfigConstants.RDB_DB_NAME,
                WIZARD_SERVICES_PAGE_RDS_DB_NAME, true, rdbGroup);

        // rds db user
        Text rdsDbUser = addField(ConfigConstants.RDB_USER,
                WIZARD_SERVICES_PAGE_RDS_DB_USER, true, rdbGroup);

        // rds db password
        Text rdsPassword = addPasswordField(ConfigConstants.RDB_PASSWORD,
                WIZARD_SERVICES_PAGE_RDS_DB_PASSWORD, true, rdbGroup);

        // only enable the rds fields if a rds instance is selected
        // since rds instance is optional
        rdsInstance.addModifyListener(e -> {
            if (rdsInstance.getText().isEmpty()) {
                rdsConnectionTypeCombo.setEnabled(false);
                rdsDbName.setEnabled(false);
                rdsDbUser.setEnabled(false);
                rdsPassword.setEnabled(false);
            } else {
                rdsConnectionTypeCombo.setEnabled(true);
                rdsDbName.setEnabled(true);
                rdsDbUser.setEnabled(true);
                rdsPassword.setEnabled(true);
            }
        });

        // little trick to trigger a modify for the first time the page is
        // opened
        rdsInstance.setText(rdsInstance.getText());

        // this listener checks the mandatory fields and only sets
        // page complete if all mandatory fields are non-empty
        Listener listener = new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                if (Util.isNotEmpty(dcsInstance.getText())) {
                    if (Util.isNotEmpty(dcsPassword.getText())) {
                        setPageComplete(true);
                    } else {
                        setPageComplete(false);
                    }
                } else {
                    setPageComplete(true);
                }

                if (Util.isNotEmpty(rdsInstance.getText())) {
                    if (Util.isNotEmpty(rdsConnectionTypeCombo.getText())
                            && Util.isNotEmpty(rdsDbName.getText())
                            && Util.isNotEmpty(rdsDbUser.getText())
                            && Util.isNotEmpty(rdsPassword.getText())) {
                        if (isPageComplete()) {
                            setPageComplete(true);
                        }
                    } else {
                        setPageComplete(false);
                    }
                } else {
                    if (isPageComplete()) {
                        setPageComplete(true);
                    }
                }
            }
        };

        // add the above listener to mandatory fields
        dcsInstance.addListener(SWT.Modify, listener);
        dcsPassword.addListener(SWT.Modify, listener);
        rdsInstance.addListener(SWT.Modify, listener);
        rdsConnectionTypeCombo.addListener(SWT.Modify, listener);
        rdsDbName.addListener(SWT.Modify, listener);
        rdsDbUser.addListener(SWT.Modify, listener);
        rdsPassword.addListener(SWT.Modify, listener);

        // little trick to trigger a modify for the first time the page is
        // opened
        rdsInstance.setText(rdsInstance.getText());
        dcsInstance.setText(dcsInstance.getText());
    }

    @Override
    protected int getPageLabelWidth() {
        return 125;
    }
}
