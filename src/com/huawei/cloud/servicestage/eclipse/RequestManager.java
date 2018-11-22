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

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;

import com.huawei.cloud.servicestage.eclipse.preferences.PreferenceConstants;
import com.huawei.cloud.servicestage.client.AuthClient;
import com.huawei.cloud.servicestage.client.HuaweiCloudClient;
import com.huawei.cloud.servicestage.client.ServiceInstanceRequestBody;
import com.huawei.cloud.servicestage.client.ServiceStageClient;
import com.huawei.cloud.servicestage.client.SimpleResponse;
import com.huawei.cloud.servicestage.client.Token;
import com.huawei.cloud.servicestage.client.UploadClient;

/**
 * This class manages requests made to ServiceStage via the ServiceStage
 * Client.<br>
 * <br>
 * It reads information from the Preferences and DialogSettings and passes it to
 * the requests as needed. <br>
 * <br>
 * It is also responsible for managing the Authentication Token, stored in the
 * plug-in's secure preference store.
 * 
 * @author Farhan Arshad
 */
public class RequestManager {
    private static RequestManager instance = null;

    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmss");

    // cached
    private Map<String, String> appTShirtSizes = null;

    private Map<String, String> applicationTypes = null;

    private Map<String, String> regions = null;

    private Map<String, String> elbs = null;

    private Map<String, String> vpcs = null;

    private Map<String, String> dcsInstances = null;

    private Map<String, String> rdsInstances = null;

    private Map<String, String> ccseClusters = null;

    private Set<String> repos = null;

    public RequestManager() {
    }

    public static RequestManager getInstance() {
        if (instance == null) {
            instance = new RequestManager();
        }

        return instance;
    }

    /**
     * Creates or updates a ServiceStage application instance.
     * 
     * @param project
     * @return
     * @throws IOException
     * @throws StorageException
     */
    public SimpleResponse createOrUpdateApplication(IProject project)
            throws IOException {
        IDialogSettings ds = Util.loadDialogSettings(project);

        String serviceInstanceId = ds.get(ConfigConstants.SERVICE_INSTANCE_ID);
        Token token = getAuthToken();

        if (getServiceStageClient().applicationExists(serviceInstanceId,
                token)) {
            ServiceInstanceRequestBody requestBody = getUpdateAppRequestBody(ds,
                    token);
            Logger.info(requestBody.toString());
            return getServiceStageClient().updateService(serviceInstanceId,
                    requestBody, token);
        } else {
            ServiceInstanceRequestBody requestBody = getCreateAppRequestBody(ds,
                    token);
            Logger.info(requestBody.toString());
            return getServiceStageClient().createService(serviceInstanceId,
                    requestBody, token);
        }
    }

    /**
     * Responsible for gathering all information required by the request body
     * 
     * @param ds
     * @param token
     * @return
     * @throws StorageException
     */
    private ServiceInstanceRequestBody getCreateAppRequestBody(
            IDialogSettings ds, Token token) {
        ServiceInstanceRequestBody r = ServiceInstanceRequestBody
                .newEmptyInstance();

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        r.serviceId = store.getString(PreferenceConstants.SERVICE_ID);
        r.planId = store.getString(PreferenceConstants.PLAN_ID);
        r.organizationGuid = store
                .getString(PreferenceConstants.ORGANIZATION_GUID);
        r.spaceGuid = store.getString(PreferenceConstants.SPACE_GUID);
        r.context.orderId = store
                .getString(PreferenceConstants.CONTEXT_ORDER_ID);

        r.parameters.name = ds.get(ConfigConstants.APP_NAME);
        r.parameters.region = token.getRegion();
        r.parameters.version = ds.get(ConfigConstants.APP_VERSION);
        r.parameters.type = ds.get(ConfigConstants.APP_TYPE_OPTION);
        r.parameters.displayName = ds.get(ConfigConstants.APP_DISPLAY_NAME);
        r.parameters.listenerPort = ds.getInt(ConfigConstants.APP_PORT);
        r.parameters.desc = ds.get(ConfigConstants.APP_DESCRIPTION);
        r.parameters.size.id = ds.get(ConfigConstants.APP_SIZE_OPTION);
        r.parameters.size.replica = ds.getInt(ConfigConstants.APP_REPLICAS);
        r.parameters.source.type = ds.get(ConfigConstants.SOURCE_TYPE_OPTION);
        r.parameters.source.repoUrl = ds.get(ConfigConstants.SOURCE_PATH);
        r.parameters.source.secuToken = ds
                .get(ConfigConstants.SOURCE_SECU_TOKEN);
        r.parameters.source.repoNamespace = ds
                .get(ConfigConstants.SOURCE_NAMESPACE);
        r.parameters.source.projBranch = ds.get(ConfigConstants.SOURCE_BRANCH);
        r.parameters.platforms.vpc.id = ds.get(ConfigConstants.APP_VPC_ID);
        r.parameters.platforms.vpc.parameters.subnet.id = ds
                .get(ConfigConstants.APP_SUBNET_ID);
        r.parameters.platforms.cce.id = ds.get(ConfigConstants.APP_CLUSTER_ID);
        r.parameters.platforms.cce.parameters.namespace = ds.get(ConfigConstants.APP_CCE_NAMESPACE);
        r.parameters.platforms.elb.id = ds.get(ConfigConstants.APP_ELB_ID);

        if (ds.get(ConfigConstants.DCS_ID) == null
                || ds.get(ConfigConstants.DCS_ID).isEmpty()) {
            r.parameters.services.distributedSession = null;
        } else {
            r.parameters.services.distributedSession.id = ds
                    .get(ConfigConstants.DCS_ID);
            r.parameters.services.distributedSession.parameters.password = ds
                    .get(ConfigConstants.DCS_PASSWORD);
            r.parameters.services.distributedSession.parameters.cluster = "false";
        }

        if (ds.get(ConfigConstants.RDB_ID) == null
                || ds.get(ConfigConstants.RDB_ID).isEmpty()) {
            r.parameters.services.relationalDatabase = null;
        } else {
            r.parameters.services.relationalDatabase.id = ds
                    .get(ConfigConstants.RDB_ID);
            r.parameters.services.relationalDatabase.parameters.connectionType = ds
                    .get(ConfigConstants.RDB_CONNECTION_TYPE);
            r.parameters.services.relationalDatabase.parameters.dbName = ds
                    .get(ConfigConstants.RDB_DB_NAME);
            r.parameters.services.relationalDatabase.parameters.dbUser = ds
                    .get(ConfigConstants.RDB_USER);
            r.parameters.services.relationalDatabase.parameters.password = ds
                    .get(ConfigConstants.RDB_PASSWORD);
        }

        if (r.parameters.services.relationalDatabase == null
                && r.parameters.services.distributedSession == null) {
            r.parameters.services = null;
        }

        return r;
    }

    /**
     * Responsible for gathering all information required by the request body
     * 
     * @param ds
     * @param token
     * @return
     * @throws StorageException
     */
    private ServiceInstanceRequestBody getUpdateAppRequestBody(
            IDialogSettings ds, Token token) {
        ServiceInstanceRequestBody r = new ServiceInstanceRequestBody();

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        r.serviceId = store.getString(PreferenceConstants.SERVICE_ID);

        r.parameters = new ServiceInstanceRequestBody.Parameters();

        LocalDateTime dt = LocalDateTime.now();
        String date = dt.format(formatter);
        r.parameters.version = ds.get(ConfigConstants.APP_VERSION) + "-" + date;

        r.parameters.source = new ServiceInstanceRequestBody.Parameters.Source();

        r.parameters.source = new ServiceInstanceRequestBody.Parameters.Source();
        r.parameters.source.type = ds.get(ConfigConstants.SOURCE_TYPE_OPTION);
        r.parameters.source.repoUrl = ds.get(ConfigConstants.SOURCE_PATH);
        r.parameters.source.secuToken = ds
                .get(ConfigConstants.SOURCE_SECU_TOKEN);
        r.parameters.source.repoNamespace = ds
                .get(ConfigConstants.SOURCE_NAMESPACE);
        r.parameters.source.projBranch = ds.get(ConfigConstants.SOURCE_BRANCH);

        return r;
    }

    /**
     * Gets an auth token for user. The user's information is obtained from the
     * plug-in preferences.
     * 
     * @return
     * @throws IOException
     * @throws StorageException
     */
    public Token getAuthToken() throws IOException {
        return getAuthToken(false);
    }

    /**
     * Gets an auth token for user. The user's information is obtained from the
     * plug-in preferences. <br>
     * <br>
     * This token will also be saved in the preference storage. <br>
     * <br>
     * 
     * @return a valid Auth Token or null
     * @throws IOException
     */
    public Token getAuthToken(boolean forceNewToken) throws IOException {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String region = store.getString(PreferenceConstants.REGION_CHOICE);
        String username = store.getString(PreferenceConstants.USERNAME);
        String password = store.getString(PreferenceConstants.PASSWORD);

        // get existing token, if any
        String tokenStr = store.getString(PreferenceConstants.TOKEN);

        Token token = null;

        // token found in store
        if (tokenStr != null && !tokenStr.isEmpty() && !forceNewToken) {
            Token t = Token.fromString(tokenStr);

            // check if token is valid and not expired
            if (t.getUsername().equals(username) && t.getRegion().equals(region)
                    && !t.isExpired()) {
                token = t;
            }
        }

        // no valid token found
        if (token == null) {
            Logger.info("No valid token found, getting new token");

            token = AuthClient.getAuthToken(region, username, password);
            store.putValue(PreferenceConstants.TOKEN, token.toString());
        }

        return token;
    }

    public void load(IProgressMonitor monitor) throws IOException {
        SubMonitor subMonitor = SubMonitor.convert(monitor, 90);

        getAppTShirtSizes();
        subMonitor.worked(10);

        if (subMonitor.isCanceled()) {
            return;
        }

        getELBs();
        subMonitor.worked(10);

        if (subMonitor.isCanceled()) {
            return;
        }

        getVPCs();
        subMonitor.worked(10);

        if (subMonitor.isCanceled()) {
            return;
        }

        getDCSInstances();
        subMonitor.worked(10);

        if (subMonitor.isCanceled()) {
            return;
        }

        getRDSInstances();
        subMonitor.worked(10);

        if (subMonitor.isCanceled()) {
            return;
        }

        getRegions();
        subMonitor.worked(10);

        if (subMonitor.isCanceled()) {
            return;
        }

        getApplicationTypes();
        subMonitor.worked(10);

        if (subMonitor.isCanceled()) {
            return;
        }

        getRepos();
        subMonitor.worked(10);
    }

    public Map<String, String> getELBs() throws IOException {
        if (this.elbs == null) {
            this.elbs = HuaweiCloudClient.getELBs(getAuthToken());
        }

        return this.elbs;
    }

    public Map<String, String> getVPCs() throws IOException {
        if (this.vpcs == null) {
            this.vpcs = HuaweiCloudClient.getVPCs(getAuthToken());
        }

        return this.vpcs;
    }

    public Set<String> getNamespaces(String clusterId) throws IOException {
        return HuaweiCloudClient.getNamespaces(getAuthToken(), clusterId);
    }

    public Map<String, String> getSubnets(String vpcId) throws IOException {
        return HuaweiCloudClient.getSubnets(getAuthToken(), vpcId);
    }

    public Map<String, String> getDCSInstances() throws IOException {
        if (this.dcsInstances == null) {
            // there is a bug with DCS, sometimes it doesn't accept an
            // existing token, therefore, always generate a new one
            this.dcsInstances = HuaweiCloudClient
                    .getDCSInstances(getAuthToken(true));
        }

        return this.dcsInstances;
    }

    public Map<String, String> getRDSInstances() throws IOException {
        if (this.rdsInstances == null) {
            this.rdsInstances = HuaweiCloudClient
                    .getRDSInstances(getAuthToken());
        }

        return this.rdsInstances;
    }

    public Map<String, String> getRegions() throws IOException {
        if (this.regions == null) {
            // hard code regions for now
            this.regions = new LinkedHashMap<>();
            this.regions.put("cn-north-1", "CN North-Beijing1");
            this.regions.put("ap-southeast", "AP-Hong Kong");
            this.regions.put("cn-east", "CN East-Shanghai2");
            this.regions.put("cn-north-3", "CN North-Beijing3");
            this.regions.put("cn-north-5", "CN North-Ulanqab1");
            this.regions.put("cn-north-6", "CN North-Ulanqab2");
            this.regions.put("cn-northeast-1", "CN Northeast-Dalian");
            this.regions.put("cn-south-1", "CN South-Guangzhou");
            this.regions.put("cn-south-2", "CN South-Shenzhen");

            // this.regions = HuaweiCloudClient.getRegions(getAuthToken());
        }

        return this.regions;
    }

    public Map<String, String> getCCEClusters() throws IOException {
        if (this.ccseClusters == null) {
            this.ccseClusters = HuaweiCloudClient
                    .getCCEClusters(getAuthToken());
        }

        return this.ccseClusters;
    }

    private ServiceStageClient getServiceStageClient() throws IOException {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        String apiUrl = store
                .getString(PreferenceConstants.SERVICESTAGE_API_URL);

        // sub in the region code if it is missing
        apiUrl = String.format(apiUrl, this.getAuthToken().getRegion());

        return new ServiceStageClient(apiUrl);
    }

    /**
     * Gets the available App T-Shirt sizes.
     * 
     * @return a map where the keys are size display names, and values are size
     *         ids for the api
     * @throws IOException
     * @throws StorageException
     */
    public Map<String, String> getAppTShirtSizes() throws IOException {
        if (this.appTShirtSizes == null) {
            this.appTShirtSizes = getServiceStageClient()
                    .getAppTShirtSizes(getAuthToken());
        }

        return this.appTShirtSizes;
    }

    /**
     * Gets the available application types (e.g. Tomcat, Node.js).
     * 
     * @return a map where the keys are type display names, and values are type
     *         ids for the api
     * @throws IOException
     * @throws StorageException
     */
    public Map<String, String> getApplicationTypes() throws IOException {
        if (this.applicationTypes == null) {
            this.applicationTypes = getServiceStageClient()
                    .getApplicationTypes(getAuthToken());
        }

        return this.applicationTypes;
    }

    /**
     * Gets a set of repos for the user
     * 
     * @return
     * @throws IOException
     * @throws StorageException
     */
    public Set<String> getRepos() throws IOException {
        if (this.repos == null) {
            Token token = getAuthToken();
            String domain = token.getUsername();
            String namespace = domain; // hardcode the namespace using username
            this.repos = new UploadClient().getRepos(domain, namespace, token);
        }

        return this.repos;
    }

    /**
     * Gets a set of packages for the repo
     * 
     * @param repo
     * @return
     * @throws IOException
     * @throws StorageException
     */
    public Set<String> getPackages(String repo) throws IOException {
        Token token = getAuthToken();
        String domain = token.getUsername();
        String namespace = domain;
        return new UploadClient().getPackages(domain, namespace, repo, token);
    }

    /**
     * Gets a set of verions for the package in the repo
     * 
     * @param repo
     * @param packageName
     * @return
     * @throws IOException
     * @throws StorageException
     */
    public Set<String> getVersions(String repo, String packageName)
            throws IOException {
        Token token = getAuthToken();
        String domain = token.getUsername();
        String namespace = domain;
        return new UploadClient().getVersions(domain, namespace, repo,
                packageName, token);
    }

    public String upload(IResource file, IProject project) throws IOException {
        String localAbsoluteFilePath = file.getRawLocation().makeAbsolute()
                .toString();

        return upload(localAbsoluteFilePath, project);
    }

    /**
     * Uploads the specified file to SWR
     * 
     * @param file
     * @param project
     * @return
     * @throws StorageException
     * @throws IOException
     */
    public String upload(String localAbsoluteFilePath, IProject project)
            throws IOException {
        Token token = getAuthToken();

        String name = new File(localAbsoluteFilePath).getName();

        String domain = token.getUsername();
        String namespace = domain; // hard-coded domain as the namespace for now

        IDialogSettings ds = Util.loadDialogSettings(project);

        String repo = ds.get(ConfigConstants.SWR_REPO);
        // String packageName = ds.get(ConfigConstants.APP_NAME);
        String packageName = project.getName();
        String version = ds.get(ConfigConstants.APP_VERSION);

        SimpleResponse uploadResponse = new UploadClient().upload(
                localAbsoluteFilePath, domain, namespace, repo, packageName,
                version, name, token);

        if (!uploadResponse.isOk()) {
            throw new IOException(uploadResponse.getMessage());
        }

        String url = new UploadClient().getExternalUrl(domain, namespace, repo,
                packageName, version, name, token);

        if (url == null || url.isEmpty()) {
            throw new IOException("Unabled to find uploaded file");
        }

        return url;
    }

    /**
     * Gets information about the application instance
     * 
     * @param project
     * @return
     * @throws IOException
     * @throws StorageException
     */
    public SimpleResponse getApplicationInfo(IProject project)
            throws IOException {
        Token token = getAuthToken();
        IDialogSettings ds = Util.loadDialogSettings(project);

        String instanceId = ds.get(ConfigConstants.SERVICE_INSTANCE_ID);

        return getServiceStageClient().getApplicationInfo(instanceId, token);
    }

    /**
     * Get application task longs
     * @param project
     * @return
     * @throws IOException
     */
    public String getApplicationTaskLogs(IProject project)
            throws IOException {
        Token token = getAuthToken();
        IDialogSettings ds = Util.loadDialogSettings(project);

        String instanceId = ds.get(ConfigConstants.SERVICE_INSTANCE_ID);

        SimpleResponse response = getServiceStageClient()
                .getApplicationTaskLogs(instanceId, token);

        if (response.getMessage() == null || response.getMessage().isEmpty()) {
            return "Error";
        } else {
            return response.getMessage();
        }
    }

    /**
     * Gets status of the application
     * 
     * @param project
     * @return {@link AppStatus}
     * @throws IOException
     * @throws StorageException
     */
    public AppStatus getApplicationStatus(IProject project) throws IOException {
        Token token = getAuthToken();
        IDialogSettings ds = Util.loadDialogSettings(project);

        String instanceId = ds.get(ConfigConstants.SERVICE_INSTANCE_ID);

        String status = getServiceStageClient().getApplicationStatus(instanceId,
                token);

        if (status == null) {
            return new AppStatus(AppStatus.UNKNOWN, "");
        }

        SimpleResponse appInfoResponse = getApplicationInfo(project);
        if (appInfoResponse.isOk()) {
            return new AppStatus(status, appInfoResponse.getMessage());
        }

        return new AppStatus(AppStatus.UNKNOWN, "");
    }

    /**
     * Gets the url for the application
     * 
     * @param project
     * @return
     * @throws IOException
     * @throws StorageException
     */
    public String getApplicationUrl(IProject project) throws IOException {
        Token token = getAuthToken();
        IDialogSettings ds = Util.loadDialogSettings(project);

        String instanceId = ds.get(ConfigConstants.SERVICE_INSTANCE_ID);

        return getServiceStageClient().getApplicationUrl(instanceId, token);
    }
}
