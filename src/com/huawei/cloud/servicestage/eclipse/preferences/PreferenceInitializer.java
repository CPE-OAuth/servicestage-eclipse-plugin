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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.huawei.cloud.servicestage.eclipse.Activator;

/**
 * Class used to initialize default preference values.
 * 
 * @author Farhan Arshad
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
     * initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.USERNAME, "username");
        store.setDefault(PreferenceConstants.PASSWORD, "password");
        store.setDefault(PreferenceConstants.SECURE, false);
        store.setDefault(PreferenceConstants.SERVICESTAGE_API_URL,
                "https://svcstg.%s.myhuaweicloud.com/v1");
        store.setDefault(PreferenceConstants.ARTIFACT_NAMESPACE, "default");
        store.setDefault(PreferenceConstants.CAS_CLUSTER_NAMESPACE, "default");
        store.setDefault(PreferenceConstants.SERVICE_ID,
                "SRV238CASUFGHI09314A");
        store.setDefault(PreferenceConstants.PLAN_ID, "cas-pay2go");
        store.setDefault(PreferenceConstants.ORGANIZATION_GUID, "abc_guid");
        store.setDefault(PreferenceConstants.SPACE_GUID, "abc_guid");
        store.setDefault(PreferenceConstants.CONTEXT_ORDER_ID, "ord123");
    }

}
