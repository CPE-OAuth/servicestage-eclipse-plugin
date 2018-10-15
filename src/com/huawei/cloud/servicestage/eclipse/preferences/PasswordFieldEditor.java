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

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

import com.huawei.cloud.servicestage.eclipse.Activator;

/**
 * @author Farhan Arshad
 */
public class PasswordFieldEditor extends StringFieldEditor {

    public PasswordFieldEditor(String name, String labelText,
            Composite parent) {
        super(name, labelText, parent);
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        super.doFillIntoGrid(parent, numColumns);

        getTextControl().setEchoChar('*');
    }

    @Override
    protected void doLoad() {
        if (getTextControl() != null) {
            String value = getLoadValue();
            getTextControl().setText(value);
            oldValue = value;
        }
    }

    @Override
    protected void doLoadDefault() {
        if (getTextControl() != null) {
            String value = getLoadValue();
            getTextControl().setText(value);
        }
        valueChanged();
    }

    @Override
    protected void doStore() {
        ISecurePreferences node = getSecurePreferencesNode();
        try {
            boolean secure = Activator.getDefault().getPreferenceStore()
                    .getBoolean(PreferenceConstants.SECURE);
            node.put(PreferenceConstants.PASSWORD, getTextControl().getText(),
                    secure);
        } catch (StorageException e) {
            setErrorMessage(e.getLocalizedMessage());
            showErrorMessage();
            e.printStackTrace();
        }
    }

    private String getLoadValue() {
        try {
            return getSecurePreferencesNode().get(PreferenceConstants.PASSWORD,
                    "");
        } catch (StorageException e) {
            setErrorMessage(e.getLocalizedMessage());
            showErrorMessage();
            e.printStackTrace();
        }

        return "";
    }

    private ISecurePreferences getSecurePreferencesNode() {
        ISecurePreferences root = SecurePreferencesFactory.getDefault();
        return root.node(Activator.PLUGIN_ID);
    }
}
