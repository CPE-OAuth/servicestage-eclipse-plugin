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
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author Farhan Arshad
 */
public class ConfigWizard extends Wizard implements Resources {

    /**
     * because we don't know the keys for ext params, we have to store
     * them separately from DialogSettings, however, when writing the
     * DialogSettings to a file, we also include the ext params
     */
    private Map<String, String> extendedParams;

    private IProject project = null;

    public ConfigWizard(IProject project) {
        super();
        this.project = project;
        IDialogSettings settings;
        try {
            settings = Util.loadDialogSettings(project);
            setDialogSettings(settings);

            // since DialogSettings doesn't support a map, or a way to
            // query all keys, use two arrays where each index is a key-value
            // pair
            String[] keys = getDialogSettings()
                    .getArray(ConfigConstants.EXTENDED_PARAM_KEYS);
            String[] values = getDialogSettings()
                    .getArray(ConfigConstants.EXTENDED_PARAM_VALUES);

            extendedParams = Util.arraysToMap(keys, values);
        } catch (IOException e) {
            Util.showExceptionDialog("Unable to load settings", getShell(), e);
        }
    }

    @Override
    public boolean performFinish() {
        try {
            if (getExtendedParams().entrySet().size() > 0) {
                String[] keys = new String[getExtendedParams().entrySet()
                        .size()];
                String[] values = new String[getExtendedParams().entrySet()
                        .size()];
                int i = 0;
                for (Entry<String, String> entry : getExtendedParams()
                        .entrySet()) {
                    keys[i] = entry.getKey();
                    values[i] = entry.getValue();
                    i++;
                }

                getDialogSettings().put("extended_param_keys", keys);
                getDialogSettings().put("extended_param_values", values);
            } else {
                getDialogSettings().put("extended_param_keys", new String[0]);
                getDialogSettings().put("extended_param_values", new String[0]);
            }
            Util.saveDialogSettings(project, getDialogSettings());
        } catch (IOException e) {
            Util.showExceptionDialog("Unable to save settings", getShell(), e);
            return false;
        }

        return true;
    }

    public Map<String, String> getExtendedParams() {
        return extendedParams;
    }
}
