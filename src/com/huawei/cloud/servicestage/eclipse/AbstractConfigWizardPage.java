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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * Provides a ServiceStage specific wrapper around {@link WizardPage} by
 * providing some common method for creating widgets. Implementating classes
 * must construct the page by implementing
 * {@link WizardPage#createControl(Composite)}.
 * 
 * @author Farhan Arshad
 */
public abstract class AbstractConfigWizardPage extends WizardPage {
    private RequestManager requestManger = null;

    protected AbstractConfigWizardPage(String pageName,
            RequestManager requestManger) {
        super(pageName);
        this.requestManger = requestManger;
    }

    public Map<String, String> getExtendedParams() {
        return ((ConfigWizard) this.getWizard()).getExtendedParams();
    }

    protected Composite createContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        container.setLayout(layout);
        setControl(container);

        return container;
    }

    protected Group createGroup(Composite parent) {
        return createGroup(parent, "");
    }

    protected Group createGroup(Composite parent, String name) {
        Group group = new Group(parent, SWT.NONE);

        if (name != null && !name.isEmpty()) {
            group.setText(name);
        }

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 400;
        group.setLayoutData(gd);

        return group;
    }

    /**
     * This width is used to make sure all labels are aligned in the page.<br>
     * A typical value should be 100 (pixels). However, if the page has some
     * long labels, the value should be increased.
     * 
     * @return
     */
    protected abstract int getPageLabelWidth();

    private GridData getLabelFormattor() {
        GridData gd = new GridData();
        gd.widthHint = getPageLabelWidth();
        return gd;
    }

    protected Spinner addSpinner(String id, String labelText, int defaultVal,
            int min, int max, boolean required, Composite container) {
        Label label = new Label(container, SWT.NONE);

        if (required) {
            label.setText(labelText + "*");
        } else {
            label.setText(labelText);
        }

        label.setLayoutData(getLabelFormattor());

        Spinner spinner = new Spinner(container, SWT.BORDER);

        int savedValue = defaultVal;
        try {
            savedValue = getDialogSettings().getInt(id);
        } catch (NumberFormatException e) {
        }

        spinner.setValues(savedValue, min, max, 0, 1, 1);
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        gd.widthHint = 120;
        spinner.setLayoutData(gd);

        getDialogSettings().put(id, new Integer(spinner.getText()));

        spinner.addModifyListener(event -> getDialogSettings().put(id,
                new Integer(spinner.getText())));

        return spinner;
    }

    protected Text addField(String id, String labelText, boolean required,
            Composite container) {
        return addField(id, labelText, "", true, required, container);
    }

    protected Text addField(String id, String labelText, String defaultValue,
            boolean enabled, boolean required, Composite container) {
        Label label = new Label(container, SWT.NONE);

        if (required) {
            label.setText(labelText + "*");
        } else {
            label.setText(labelText);
        }

        label.setLayoutData(getLabelFormattor());

        Text text = new Text(container, SWT.BORDER | SWT.SINGLE);

        String savedValue = getDialogSettings().get(id);
        savedValue = savedValue == null ? defaultValue : savedValue;

        text.setText(savedValue);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(gd);

        if (!savedValue.isEmpty()) {
            getDialogSettings().put(id, text.getText());
        }

        text.setEnabled(enabled);

        text.addModifyListener(
                e -> getDialogSettings().put(id, text.getText()));

        return text;
    }

    protected Text addPasswordField(String id, String labelText,
            boolean required, Composite container) {
        Label label = new Label(container, SWT.NONE);

        if (required) {
            label.setText(labelText + "*");
        } else {
            label.setText(labelText);
        }

        label.setLayoutData(getLabelFormattor());

        Text text = new Text(container, SWT.BORDER | SWT.SINGLE);

        text.setEchoChar('*');

        String savedValue = getDialogSettings().get(id);
        savedValue = savedValue == null ? "" : savedValue;

        text.setText(savedValue);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(gd);

        text.addModifyListener(
                e -> getDialogSettings().put(id, text.getText()));

        return text;
    }

    /**
     * Values is a mapping from apiValue (id) to displayValue.<br>
     * <br>
     * For example, for App T-shirt sizes, the human readable value
     * (displayValue) is "Small: 10G Storage, 1 CPU, 2G Memory", and the value
     * that will be used by the API later on (apiValue) is "SMALL-10G:1.0C:2G"
     * <br>
     * <br>
     * Therefore, the values map would look like:<br>
     * apiValue -> displayValue<br>
     * "SMALL-10G:1.0C:2G" -> "Small: 10G Storage, 1 CPU, 2G Memory"<br>
     * <br>
     * The value stored in the dialog settings will be the apiValue.
     */
    protected Combo addDropdown(String id, String labelText,
            final Map<String, String> values, boolean readOnly,
            boolean required, Composite container) {
        Label label = new Label(container, SWT.NONE);

        if (required) {
            label.setText(labelText + "*");
        } else {
            label.setText(labelText);
        }

        label.setLayoutData(getLabelFormattor());

        Combo comboDropDown;
        if (readOnly) {
            comboDropDown = new Combo(container, SWT.READ_ONLY);
        } else {
            comboDropDown = new Combo(container, SWT.DROP_DOWN | SWT.BORDER);
        }

        if (!required) {
            comboDropDown.add("");
        }

        int largestLength = 15;
        for (String displayValue : values.values()) {
            comboDropDown.add(displayValue);

            if (largestLength < displayValue.length()) {
                largestLength = displayValue.length();
            }
        }

        String savedId = getDialogSettings().get(id);

        if (savedId != null && !savedId.isEmpty()) {
            String savedIdDisplayName = values.get(savedId);

            if (savedIdDisplayName != null) {
                comboDropDown.setText(savedIdDisplayName);
            }
        }

        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        gd.widthHint = largestLength * 10;
        comboDropDown.setLayoutData(gd);

        comboDropDown.addModifyListener(e -> {
            String displayValue = comboDropDown.getText();

            if (displayValue.isEmpty()) {
                getDialogSettings().put(id, "");
                return;
            }

            for (Entry<String, String> entry : values.entrySet()) {
                if (entry.getValue().equals(displayValue)) {
                    getDialogSettings().put(id, entry.getKey());
                    return;
                }
            }
        });

        return comboDropDown;
    }

    protected Combo addDropdown(String id, String labelText,
            Collection<String> values, boolean readOnly, boolean required,
            Composite container) {
        Label label = new Label(container, SWT.NONE);

        if (required) {
            label.setText(labelText + "*");
        } else {
            label.setText(labelText);
        }

        label.setLayoutData(getLabelFormattor());

        Combo comboDropDown;
        if (readOnly) {
            comboDropDown = new Combo(container, SWT.READ_ONLY);
        } else {
            comboDropDown = new Combo(container, SWT.DROP_DOWN | SWT.BORDER);
        }

        if (!required) {
            comboDropDown.add("");
        }

        int largestLength = 15;
        for (String key : values) {
            comboDropDown.add(key);

            if (largestLength < key.length()) {
                largestLength = key.length();
            }
        }

        String savedValue = getDialogSettings().get(id);
        savedValue = savedValue == null ? "" : savedValue;
        comboDropDown.setText(savedValue);

        // case when values are empty
        // width should be enough to fit the saved value
        if (largestLength < savedValue.length()) {
            largestLength = savedValue.length();
        }

        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        gd.widthHint = largestLength * 11;
        comboDropDown.setLayoutData(gd);

        comboDropDown.addModifyListener(
                e -> getDialogSettings().put(id, comboDropDown.getText()));

        return comboDropDown;
    }

    public RequestManager getRequestManger() {
        return requestManger;
    }
}
