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

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Farhan Arshad
 */
public class ExtendedParamsConfigWizardPage extends AbstractConfigWizardPage
        implements Resources {

    protected ExtendedParamsConfigWizardPage(RequestManager requestManger) {
        super(WIZARD_SERVICES_PAGE_PAGE_NAME, requestManger);
        setTitle(WIZARD_SERVICES_PAGE_TITLE);
        setDescription(WIZARD_SERVICES_PAGE_DESCRIPTION);
    }

    public Map<String, String> getExtendedParams() {
        return ((ConfigWizard) this.getWizard()).getExtendedParams();
    }

    @Override
    public void createControl(Composite parent) {
        this.setPageComplete(false);

        // outer container
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());
        setControl(container);

        final ScrolledComposite scrolledComposite = new ScrolledComposite(
                container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        final Composite mainComposite = new Composite(scrolledComposite,
                SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));
        scrolledComposite.setContent(mainComposite);
        scrolledComposite
                .setSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        final Composite paramsComposite = new Composite(mainComposite,
                SWT.NONE);
        paramsComposite.setLayout(new GridLayout(3, false));
        paramsComposite
                .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite buttonComposite = new Composite(mainComposite,
                SWT.NONE);
        buttonComposite.setLayout(new GridLayout());
        buttonComposite.setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, false, false));

        final Button btnAdd = new Button(buttonComposite, SWT.NONE);
        btnAdd.setText("Add");
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                addParam(null, null, false, false, paramsComposite,
                        scrolledComposite, mainComposite);

                scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(
                        mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        });

        String category = getDialogSettings()
                .get(ConfigConstants.APP_CATEGORY_OPTION);

        // since DialogSettings doesn't support a map, or a way to
        // query all keys, use two arrays where each index is a key-value
        // pair
        String[] keys = getDialogSettings()
                .getArray(ConfigConstants.EXTENDED_PARAM_KEYS);
        String[] values = getDialogSettings()
                .getArray(ConfigConstants.EXTENDED_PARAM_VALUES);

        Map<String, String> extendedParams = Util.arraysToMap(keys, values);
        extendedParams.putAll(super.getExtendedParams());

        Set<Text> mandatory = new HashSet<>();

        if (category != null
                && category.equals(ConfigConstants.APP_CATEGORY_SERVICECOMB)) {
            mandatory.add(addParam(
                    ConfigConstants.APP_CATEGORY_PAAS_CSE_SC_ENDPOINT,
                    extendedParams.getOrDefault(
                            ConfigConstants.APP_CATEGORY_PAAS_CSE_SC_ENDPOINT,
                            ""),
                    true, true, paramsComposite, scrolledComposite,
                    mainComposite));
            extendedParams
                    .remove(ConfigConstants.APP_CATEGORY_PAAS_CSE_SC_ENDPOINT);

            mandatory.add(addParam(
                    ConfigConstants.APP_CATEGORY_PAAS_CSE_CC_ENDPOINT,
                    extendedParams.getOrDefault(
                            ConfigConstants.APP_CATEGORY_PAAS_CSE_CC_ENDPOINT,
                            ""),
                    true, true, paramsComposite, scrolledComposite,
                    mainComposite));
            extendedParams
                    .remove(ConfigConstants.APP_CATEGORY_PAAS_CSE_CC_ENDPOINT);
        }

        for (Entry<String, String> entry : extendedParams.entrySet()) {
            addParam(entry.getKey(), entry.getValue(), false, false,
                    paramsComposite, scrolledComposite, mainComposite);
        }

        // this listener checks the mandatory fields and only sets
        // page complete if all mandatory fields are non-empty
        Listener listener = new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                for (Text t : mandatory) {
                    if (t.getText().isEmpty()) {
                        setPageComplete(false);
                        return;
                    }
                }

                setPageComplete(true);
            }
        };

        // add the above listener to mandatory fields
        for (Text t : mandatory) {
            t.addListener(SWT.Modify, listener);
        }

        for (Text t : mandatory) {
            t.setText(t.getText());
        }
    }

    protected Text addParam(String defaultKey, String defaultValue,
            boolean required, boolean readOnly, Composite container,
            ScrolledComposite scrolledComposite, Composite mainComposite) {
        Text key = readOnly ? new Text(container, SWT.BORDER | SWT.READ_ONLY)
                : new Text(container, SWT.BORDER);
        key.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        if (defaultKey != null && !defaultKey.isEmpty()) {
            key.setText(defaultKey);
        }

        Text value = new Text(container, SWT.BORDER);
        value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        if (defaultValue != null && !defaultValue.isEmpty()) {
            value.setText(defaultValue);
        }

        final Map<String, String> extendedParams = super.getExtendedParams();
        extendedParams.put(key.getText(), value.getText());

        key.addVerifyListener(e -> {
            String prevKey = key.getText();
            extendedParams.remove(prevKey);
            String newKey = (prevKey.substring(0, e.start) + e.text
                    + prevKey.substring(e.end));
            extendedParams.put(newKey, value.getText());
            e.doit = true;
        });

        value.addVerifyListener(e -> {
            String prevValue = value.getText();
            extendedParams.remove(prevValue);
            String newValue = (prevValue.substring(0, e.start) + e.text
                    + prevValue.substring(e.end));
            extendedParams.put(key.getText(), newValue);
            e.doit = true;
        });

        Button button = new Button(container, SWT.NONE);
        button.setText("Delete");
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        button.setEnabled(!required);

        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                extendedParams.remove(key.getText());

                key.dispose();
                value.dispose();
                button.dispose();

                scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(
                        mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        return value;
    }

    @Override
    protected int getPageLabelWidth() {
        return 125;
    }
}
