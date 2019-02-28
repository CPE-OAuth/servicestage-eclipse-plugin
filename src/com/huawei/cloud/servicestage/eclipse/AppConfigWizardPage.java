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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * @author Farhan Arshad
 */
public class AppConfigWizardPage extends AbstractConfigWizardPage
        implements Resources {

    public AppConfigWizardPage(RequestManager requestManger) {
        super(WIZARD_APP_PAGE_PAGE_NAME, requestManger);
        setTitle(WIZARD_APP_PAGE_TITLE);
        setDescription(WIZARD_APP_PAGE_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        // outer container
        Composite container = createContainer(parent);
        this.setPageComplete(false);

        //
        // service instance group
        //
        Group serviceInstanceGroup = createGroup(container,
                WIZARD_APP_PAGE_SERVICE_INSTANCE_GROUP_NAME);

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        serviceInstanceGroup.setLayout(layout);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 400;
        serviceInstanceGroup.setLayoutData(gd);

        // service instance id is auto generated as a uuid and can not be
        // modified
        Text sid = addField(ConfigConstants.SERVICE_INSTANCE_ID,
                WIZARD_APP_PAGE_SERVICE_INSTANCE_ID,
                UUID.randomUUID().toString(), false, true,
                serviceInstanceGroup);

        gd = new GridData();
        gd.widthHint = 325;
        sid.setLayoutData(gd);

        Button button = new Button(serviceInstanceGroup, SWT.NONE);
        button.setText("Reset ID");
        gd = new GridData();
        gd.widthHint = 100;
        button.setLayoutData(gd);

        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                sid.setText(UUID.randomUUID().toString());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        //
        // application group
        //
        Group appGroup = createGroup(container,
                WIZARD_APP_PAGE_APPLICATION_GROUP_NAME);

        // app name
        Text name = addField(ConfigConstants.APP_NAME, WIZARD_APP_PAGE_APP_NAME,
                true, appGroup);

        // restrict app name to alphanumeric
        final Pattern alphanumeric = Pattern.compile("^[a-zA-Z0-9-]*$");
        name.addVerifyListener(e -> {
            String currentName = name.getText();
            String newName = (currentName.substring(0, e.start) + e.text
                    + currentName.substring(e.end));

            Matcher matcher = alphanumeric.matcher(newName);
            e.doit = matcher.matches();
        });

        // display name
        Text displayName = addField(ConfigConstants.APP_DISPLAY_NAME,
                WIZARD_APP_PAGE_APP_DISPLAY_NAME, true, appGroup);

        // description
        Text desc = addField(ConfigConstants.APP_DESCRIPTION,
                WIZARD_APP_PAGE_APP_DESCRIPTION, false, appGroup);

        // version
        Text version = addField(ConfigConstants.APP_VERSION,
                WIZARD_APP_PAGE_APP_VERSION, "1.0", true, true, appGroup);

        // copy app name to display name and description
        // but only if the fields were initally empty, i.e. no saved values
        boolean displayNameInitallyEmpty = displayName.getText().isEmpty();
        boolean descInitallyEmpty = desc.getText().isEmpty();

        name.addModifyListener(event -> {
            if (displayNameInitallyEmpty) {
                displayName.setText(name.getText());
            }

            if (descInitallyEmpty) {
                desc.setText(name.getText());
            }
        });

        // app runtime types
        Map<String, String> types = Collections.emptyMap();
        try {
            types = this.getRequestManger().getApplicationTypes();
        } catch (Exception e) {
            this.setErrorMessage(WIZARD_APP_PAGE_APP_TYPE_ERROR);
            Util.showJobExceptionDialog(WIZARD_APP_PAGE_APP_TYPE_ERROR,
                    parent.getShell(), e);
        }

        Combo type = addDropdown(ConfigConstants.APP_TYPE_OPTION,
                WIZARD_APP_PAGE_APP_TYPE, types, true, true, appGroup);

        // category
        Map<String, String> categories = new LinkedHashMap<String, String>();
        categories.put(ConfigConstants.APP_CATEGORY_WEBAPP,
                ConfigConstants.APP_CATEGORY_WEBAPP);
        categories.put(ConfigConstants.APP_CATEGORY_WORDPRESS,
                ConfigConstants.APP_CATEGORY_WORDPRESS);
        categories.put(ConfigConstants.APP_CATEGORY_SERVICECOMB,
                ConfigConstants.APP_CATEGORY_SERVICECOMB);
        categories.put(ConfigConstants.APP_CATEGORY_MAGENTO,
                ConfigConstants.APP_CATEGORY_MAGENTO);
        categories.put(ConfigConstants.APP_CATEGORY_MOBILE,
                ConfigConstants.APP_CATEGORY_MOBILE);

        Combo category = addDropdown(ConfigConstants.APP_CATEGORY_OPTION,
                WIZARD_APP_PAGE_APP_CATEGORY, categories, false, true, appGroup);

        if (category.getText() == null || category.getText().isEmpty()) {
            category.setText(ConfigConstants.APP_CATEGORY_WEBAPP);
        }

        // port
        Spinner port = addSpinner(ConfigConstants.APP_PORT,
                WIZARD_APP_PAGE_PORT, 8080, 1, 99999, true, appGroup);

        // app group
        // app runtime types
        Map<String, String> appGroups = Collections.emptyMap();
        try {
        	appGroups = this.getRequestManger().getAppGroups();
        } catch (Exception e) {
            this.setErrorMessage(WIZARD_APP_PAGE_APP_GROUP_ERROR);
            Util.showJobExceptionDialog(WIZARD_APP_PAGE_APP_GROUP_ERROR,
                    parent.getShell(), e);
        }

        Combo appGroupstype = addDropdown(ConfigConstants.APP_GROUP_ID,
                WIZARD_APP_PAGE_APP_GROUP, appGroups, true, false, appGroup);
                
        appGroupstype.addModifyListener(event -> {
        	int selectionIndex = appGroupstype.getSelectionIndex();
        	String selectionLabel = appGroupstype.getText();
        });
        
        
        //
        // swr upload info group
        //
        Group localGroup = createGroup(container,
                WIZARD_SRC_PAGE_SWR_GROUP_NAME);

        // only local file is currently supported
        getDialogSettings().put(ConfigConstants.SOURCE_TYPE_OPTION,
                ConfigConstants.SOURCE_TYPE_LOCAL_FILE);

        // repo where binary will be uploaded
        Set<String> repos = Collections.emptySet();
        try {
            repos = this.getRequestManger().getRepos();
        } catch (Exception e) {
            this.setErrorMessage(WIZARD_APP_PAGE_SWR_REPO_ERROR);
            Util.showJobExceptionDialog(WIZARD_APP_PAGE_SWR_REPO_ERROR,
                    parent.getShell(), e);
        }

        Combo repo = addDropdown(ConfigConstants.SWR_REPO,
                WIZARD_SRC_PAGE_SWR_REPO, repos, false, true, localGroup);

        //
        // platform group
        //
        Group platformGroup = createGroup(container,
                WIZARD_APP_PAGE_PLATFORM_GROUP_NAME);

        // CCE clusters
        final Map<String, String> cceClusters = new LinkedHashMap<>();
        try {
            cceClusters.putAll(this.getRequestManger().getCCEClusters());
        } catch (Exception e) {
            this.setErrorMessage(WIZARD_APP_PAGE_APP_CLUSTER_ERROR);
            Util.showJobExceptionDialog(WIZARD_APP_PAGE_APP_CLUSTER_ERROR,
                    parent.getShell(), e);
        }

        Combo cce = addDropdown(ConfigConstants.APP_CLUSTER_ID,
                WIZARD_APP_PAGE_APP_CLUSTER, cceClusters, true, true,
                platformGroup);

        // namespaces
        String cceName = cce.getText();
        final Set<String> namespaces = new HashSet<>();
        if (cceName != null && !cceName.isEmpty()) {
            String cceId = null;

            for (Entry<String, String> entry : cceClusters.entrySet()) {
                if (entry.getValue().equals(cceName)) {
                    cceId = entry.getKey();
                }
            }

            try {
                namespaces.addAll(this.getRequestManger().getNamespaces(cceId));
            } catch (Exception e) {
                this.setErrorMessage(WIZARD_APP_PAGE_APP_SUBNET_ERROR);
                Util.showJobExceptionDialog(WIZARD_APP_PAGE_APP_SUBNET_ERROR,
                        parent.getShell(), e);
            }
        }

        Combo namespace = addDropdown(ConfigConstants.APP_CCE_NAMESPACE,
                WIZARD_APP_PAGE_APP_CLUSTER_NAMESPACE, namespaces, true, true,
                platformGroup);

        cce.addModifyListener(event -> {
            namespace.removeAll();

            String cceNamel = cce.getText();
            if (cceNamel != null && !cceNamel.isEmpty()) {
                String cceId = null;

                for (Entry<String, String> entry : cceClusters.entrySet()) {
                    if (entry.getValue().equals(cceNamel)) {
                        cceId = entry.getKey();
                    }
                }

                try {
                    namespaces.clear();
                    namespaces.addAll(
                            this.getRequestManger().getNamespaces(cceId));

                    for (String s : namespaces) {
                        namespace.add(s);
                    }
                } catch (Exception e) {
                    this.setErrorMessage(
                            WIZARD_APP_PAGE_APP_CLUSTER_NAMESPACE_ERROR);
                    Util.showJobExceptionDialog(
                            WIZARD_APP_PAGE_APP_CLUSTER_NAMESPACE_ERROR,
                            parent.getShell(), e);
                }
            }
        });

        // ELBs
        Map<String, String> elbs = Collections.emptyMap();
        try {
            elbs = this.getRequestManger().getELBs();
        } catch (Exception e) {
            this.setErrorMessage(WIZARD_APP_PAGE_APP_ELB_ERROR);
            Util.showJobExceptionDialog(WIZARD_APP_PAGE_APP_ELB_ERROR,
                    parent.getShell(), e);
        }

        Combo elb = addDropdown(ConfigConstants.APP_ELB_ID,
                WIZARD_APP_PAGE_APP_ELB, elbs, true, true, platformGroup);

        // VPCs
        final Map<String, String> vpcs = new LinkedHashMap<>();
        try {
            vpcs.putAll(this.getRequestManger().getVPCs());
        } catch (Exception e) {
            this.setErrorMessage(WIZARD_APP_PAGE_APP_VPC_ERROR);
            Util.showJobExceptionDialog(WIZARD_APP_PAGE_APP_VPC_ERROR,
                    parent.getShell(), e);
        }

        Combo vpc = addDropdown(ConfigConstants.APP_VPC_ID,
                WIZARD_APP_PAGE_APP_VPC, vpcs, true, true, platformGroup);

        String vpcName = vpc.getText();
        final Map<String, String> subnets = new LinkedHashMap<>();
        if (vpcName != null && !vpcName.isEmpty()) {
            String vpcId = null;

            for (Entry<String, String> entry : vpcs.entrySet()) {
                if (entry.getValue().equals(vpcName)) {
                    vpcId = entry.getKey();
                }
            }

            try {
                subnets.putAll(this.getRequestManger().getSubnets(vpcId));
            } catch (Exception e) {
                this.setErrorMessage(WIZARD_APP_PAGE_APP_SUBNET_ERROR);
                Util.showJobExceptionDialog(WIZARD_APP_PAGE_APP_SUBNET_ERROR,
                        parent.getShell(), e);
            }
        }

        Combo subnet = addDropdown(ConfigConstants.APP_SUBNET_ID,
                WIZARD_APP_PAGE_APP_SUBNET, subnets, true, true, platformGroup);

        vpc.addModifyListener(event -> {
            subnet.removeAll();

            String vpcNamel = vpc.getText();
            if (vpcNamel != null && !vpcNamel.isEmpty()) {
                String vpcId = null;

                for (Entry<String, String> entry : vpcs.entrySet()) {
                    if (entry.getValue().equals(vpcNamel)) {
                        vpcId = entry.getKey();
                    }
                }

                try {
                    subnets.clear();
                    subnets.putAll(this.getRequestManger().getSubnets(vpcId));

                    for (String s : subnets.values()) {
                        subnet.add(s);
                    }
                } catch (Exception e) {
                    this.setErrorMessage(WIZARD_APP_PAGE_APP_SUBNET_ERROR);
                    Util.showJobExceptionDialog(
                            WIZARD_APP_PAGE_APP_SUBNET_ERROR, parent.getShell(),
                            e);
                }
            }
        });

        // app sizes
        Map<String, String> sizes = Collections.emptyMap();
        try {
            sizes = this.getRequestManger().getAppTShirtSizes();
        } catch (Exception e) {
            this.setErrorMessage(WIZARD_APP_PAGE_APP_SIZE_ERROR);
            Util.showJobExceptionDialog(WIZARD_APP_PAGE_APP_SIZE_ERROR,
                    parent.getShell(), e);
        }

        Combo size = addDropdown(ConfigConstants.APP_SIZE_OPTION,
                WIZARD_APP_PAGE_APP_SIZE, sizes, true, true, platformGroup);

        // numer of replicas
        Spinner replicas = addSpinner(ConfigConstants.APP_REPLICAS,
                WIZARD_APP_PAGE_APP_REPLICAS, 1, 1, 99, true, platformGroup);

        // this listener checks the mandatory fields and only sets
        // page complete if all mandatory fields are non-empty
        Listener listener = new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                if (Util.isNotEmpty(name.getText())
                        && Util.isNotEmpty(displayName.getText())
                        && Util.isNotEmpty(version.getText())
                        && Util.isNotEmpty(type.getText())
                        && Util.isNotEmpty(category.getText())
                        && Util.isNotEmpty(port.getText())
                        && Util.isNotEmpty(repo.getText())
                        && Util.isNotEmpty(cce.getText())
                        && Util.isNotEmpty(elb.getText())
                        && Util.isNotEmpty(vpc.getText())
                        && Util.isNotEmpty(subnet.getText())
                        && Util.isNotEmpty(size.getText())
                        && Util.isNotEmpty(replicas.getText())) {
                    setPageComplete(true);
                } else {
                    setPageComplete(false);
                }
            }
        };

        // add the above listener to mandatory fields
        name.addListener(SWT.Modify, listener);
        displayName.addListener(SWT.Modify, listener);
        version.addListener(SWT.Modify, listener);
        type.addListener(SWT.Modify, listener);
        category.addListener(SWT.Modify, listener);
        port.addListener(SWT.Modify, listener);
        repo.addListener(SWT.Modify, listener);
        cce.addListener(SWT.Modify, listener);
        elb.addListener(SWT.Modify, listener);
        vpc.addListener(SWT.Modify, listener);
        subnet.addListener(SWT.Modify, listener);
        size.addListener(SWT.Modify, listener);
        replicas.addListener(SWT.Modify, listener);

        // little trick to trigger a modify for the first time the page is
        // opened
        name.setText(name.getText());
    }

    @Override
    protected int getPageLabelWidth() {
        return 120;
    }

}
