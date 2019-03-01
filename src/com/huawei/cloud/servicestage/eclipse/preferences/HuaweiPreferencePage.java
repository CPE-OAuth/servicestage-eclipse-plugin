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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.huawei.cloud.servicestage.client.Token;
import com.huawei.cloud.servicestage.eclipse.Activator;
import com.huawei.cloud.servicestage.eclipse.Logger;
import com.huawei.cloud.servicestage.eclipse.RequestManager;
import com.huawei.cloud.servicestage.eclipse.Resources;

/**
 * @author Farhan Arshad
 */
public class HuaweiPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage, Resources {

	private StringFieldEditor usernameInput;
	private StringFieldEditor passwordInput;
    private StringFieldEditor domainInput;
    private String regionSelection;
    
   
    public HuaweiPreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(PREFERENCES_HUAWEI_DESCRIPTION);
    }

    @Override
    public void init(IWorkbench arg0) {
    	setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        // region group
        Group regionGroup = new Group(getFieldEditorParent(), SWT.NONE);
        initGroup(regionGroup);

        Map<String, String> regions = Collections.emptyMap();
        try {
            regions = RequestManager.getInstance().getRegions();
        } catch (IOException e) {
            Logger.exception(e);
        }
        addDropdown(PreferenceConstants.REGION_CHOICE,
                PREFERENCES_HUAWEI_SELECT_REGION, regions, regionGroup);

        // blank separator
        new Label(getFieldEditorParent(), SWT.NULL);

        // authentication group
        Group authenticationGroup = new Group(getFieldEditorParent(), SWT.NONE);
        initGroup(authenticationGroup);

        
        usernameInput = new StringFieldEditor(PreferenceConstants.USERNAME,
                PREFERENCES_HUAWEI_USERNAME, authenticationGroup);
        passwordInput = new PasswordFieldEditor(PreferenceConstants.PASSWORD,
                PREFERENCES_HUAWEI_PASSWORD, authenticationGroup);
        domainInput = new StringFieldEditor(PreferenceConstants.DOMAIN,
                PREFERENCES_HUAWEI_DOMAIN, authenticationGroup);
        
        addField(usernameInput);
        addField(passwordInput);
        addField(domainInput);

        addTokenButtonGroup(authenticationGroup);
    }

    private void addTokenButtonGroup(Composite parent) {

    	Composite authButtonGroup = new Composite(parent, SWT.NONE);
    	RowLayout rowLayout = new RowLayout();
    	rowLayout.wrap = true;
    	rowLayout.pack = true;
    	rowLayout.justify = false;
    	rowLayout.type = SWT.HORIZONTAL;
    	
    	authButtonGroup.setLayout(rowLayout);
    	authButtonGroup.setLayoutData(
                new GridData(SWT.END, SWT.CENTER, false, false, 2, 1));

    	Button testConnectButton = new Button(authButtonGroup, SWT.PUSH);
    	testConnectButton.setText(PREFERENCES_HUAWEI_TEST_TOKEN);
    	
    	Button resetTokenButton = new Button(authButtonGroup, SWT.PUSH);
        resetTokenButton.setText(PREFERENCES_HUAWEI_RESET_TOKEN);

        testConnectButton.addListener(SWT.Selection, new Listener() {
        	@Override
            public void handleEvent(Event arg0) {
        		try {
        			String username = usernameInput.getStringValue();
        			String password = passwordInput.getStringValue();
        			String domain = domainInput.getStringValue();
        			
        			Token token = RequestManager.getInstance().getAuthToken(true, false, regionSelection, username, password, domain);
        			if (token!=null) {
        				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Test Connection", "Test Connection Success!");
        			} else {
        				throw new Exception("Invalid Token");
        			}
        		} catch (Exception e) {
        			System.out.print(e);
        			MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Test Connection", "Test Connection Failed!");
        			//pop up error dialog here
        		}
            }
        });
        
        resetTokenButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                IPreferenceStore store = Activator.getDefault()
                        .getPreferenceStore();
                store.setValue(PreferenceConstants.TOKEN, "");
            }
        });
    }

    private void initGroup(Composite group) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 200;
        group.setLayoutData(gd);
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
        
        Iterator it = values.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
            String displayValue = pair.getValue();
            String displayId = pair.getKey();
            
            comboDropDown.add(displayValue);
            if (regionSelection==null) { // initalize selection 
            	regionSelection=displayId;
            	comboDropDown.select(0);;
            }

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
                regionSelection=savedId;
            }
        } else {
        	store.setValue(id, regionSelection);
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
                    regionSelection=entry.getKey();
                    return;
                }
            }
        });

        return comboDropDown;
    }
}
