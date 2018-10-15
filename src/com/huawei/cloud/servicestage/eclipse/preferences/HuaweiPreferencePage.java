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

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.huawei.cloud.servicestage.eclipse.Activator;
import com.huawei.cloud.servicestage.eclipse.Logger;
import com.huawei.cloud.servicestage.eclipse.RequestManager;
import com.huawei.cloud.servicestage.eclipse.Resources;

/**
 * @author Farhan Arshad
 */
public class HuaweiPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage, Resources {

    public HuaweiPreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(PREFERENCES_HUAWEI_DESCRIPTION);
    }

    @Override
    public void init(IWorkbench arg0) {
    }

    @Override
    protected void createFieldEditors() {
        // region group
        Group regionGroup = new Group(getFieldEditorParent(), SWT.NONE);
        initGroup(regionGroup);

        Map<String, String> regions = Collections.emptyMap();
        try {
            regions = RequestManager.getInstance().getRegions();
        } catch (IOException | StorageException e) {
            Logger.exception(e);
        }
        addDropdown(PreferenceConstants.REGION_CHOICE,
                PREFERENCES_HUAWEI_SELECT_REGION, regions, regionGroup);

        // blank separator
        new Label(getFieldEditorParent(), SWT.NULL);

        // authentication group
        Group authenticationGroup = new Group(getFieldEditorParent(), SWT.NONE);
        initGroup(authenticationGroup);

        addField(new StringFieldEditor(PreferenceConstants.USERNAME,
                PREFERENCES_HUAWEI_USERNAME, authenticationGroup));
        addField(new PasswordFieldEditor(PreferenceConstants.PASSWORD,
                PREFERENCES_HUAWEI_PASSWORD, authenticationGroup));
        addField(new BooleanFieldEditorWithListener(PreferenceConstants.SECURE,
                PREFERENCES_HUAWEI_STORE_SECURELY, authenticationGroup));
        createLinkArea(authenticationGroup);

        addResetTokenButton(authenticationGroup);
    }

    private void addResetTokenButton(Composite parent) {
        Button resetTokenButton = new Button(parent, SWT.PUSH);
        resetTokenButton.setText(PREFERENCES_HUAWEI_RESET_TOKEN);
        resetTokenButton.setLayoutData(
                new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1));

        resetTokenButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                ISecurePreferences node = SecurePreferencesFactory.getDefault()
                        .node(Activator.PLUGIN_ID);
                node.remove(PreferenceConstants.TOKEN);
            }
        });
    }

    private void initGroup(Composite group) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 400;
        group.setLayoutData(gd);
    }

    private void createLinkArea(Composite parent) {
        IPreferenceNode node = getPreferenceNode(
                "org.eclipse.equinox.security.ui.storage");
        if (node != null) {
            PreferenceLinkArea linkArea = new PreferenceLinkArea(parent,
                    SWT.WRAP, "org.eclipse.equinox.security.ui.storage",
                    PREFERENCES_HUAWEI_SECURE_STORAGE_NOTE,
                    (IWorkbenchPreferenceContainer) getContainer(), null);
            GridData data = new GridData(
                    GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
            linkArea.getControl().setLayoutData(data);
        }
    }

    private IPreferenceNode getPreferenceNode(String pageId) {
        Iterator<IPreferenceNode> iterator = PlatformUI.getWorkbench()
                .getPreferenceManager().getElements(PreferenceManager.PRE_ORDER)
                .iterator();
        while (iterator.hasNext()) {
            IPreferenceNode next = iterator.next();
            if (next.getId().equals(pageId)) return next;
        }
        return null;
    }

    private Combo addDropdown(String id, String labelText,
            final Map<String, String> values, Composite container) {
        Label label = new Label(container, SWT.NONE);
        label.setText(labelText);
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        label.setLayoutData(gd);

        Combo comboDropDown = new Combo(container, SWT.READ_ONLY);

        int largestLength = 15;
        for (String displayValue : values.values()) {
            comboDropDown.add(displayValue);

            if (largestLength < displayValue.length()) {
                largestLength = displayValue.length();
            }
        }

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        String savedId = store.getString(id);

        if (savedId != null && !savedId.isEmpty()) {
            String savedIdDisplayName = values.get(savedId);

            if (savedIdDisplayName != null) {
                comboDropDown.setText(savedIdDisplayName);
            }
        }

        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.widthHint = largestLength * 10;
        comboDropDown.setLayoutData(gd);

        comboDropDown.addModifyListener(e -> {
            String displayValue = comboDropDown.getText();
            for (Entry<String, String> entry : values.entrySet()) {
                if (entry.getValue().equals(displayValue)) {
                    store.putValue(id, entry.getKey());
                    return;
                }
            }
        });

        return comboDropDown;
    }
}
