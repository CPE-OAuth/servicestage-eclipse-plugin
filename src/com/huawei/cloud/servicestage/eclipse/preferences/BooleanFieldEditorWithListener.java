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

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.huawei.cloud.servicestage.eclipse.Activator;

/**
 * Rather than waiting for the user to click Apply, this button updates the
 * preference store on each change.
 * 
 * @author Farhan Arshad
 */
public class BooleanFieldEditorWithListener extends BooleanFieldEditor {

    public BooleanFieldEditorWithListener(String name, String label,
            Group parent) {
        super(name, label, parent);
    }

    // adds an additional listener that updates the preference right away
    protected Button getChangeControl(Composite parent) {
        Button button = super.getChangeControl(parent);

        button.addSelectionListener(widgetSelectedAdapter(e -> {
            boolean isSelected = button.getSelection();
            Activator.getDefault().getPreferenceStore()
                    .setValue(getPreferenceName(), isSelected);
        }));

        return button;
    }
}