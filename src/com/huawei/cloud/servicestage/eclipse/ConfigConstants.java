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

/**
 * Constants for the settings file
 * 
 * @author Farhan Arshad
 */
public class ConfigConstants {
    public static final String SERVICE_INSTANCE_ID = "instanceId";

    public static final String APP_NAME = "name";

    public static final String APP_DISPLAY_NAME = "display_name";

    public static final String APP_VERSION = "version";

    public static final String APP_DESCRIPTION = "desc";

    public static final String APP_ELB_ID = "elb.id";

    public static final String APP_VPC_ID = "vpc.id";

    public static final String APP_SUBNET_ID = "vpc.parameters.subnet.id";

    public static final String APP_CLUSTER_ID = "cce.id";

    public static final String APP_CCE_NAMESPACE = "cce.parameters.namespace";

    public static final String APP_TYPE_OPTION = "type";

    public static final String APP_CATEGORY_OPTION = "category";

    public static final String APP_CATEGORY_SERVICECOMB = "ServiceComb";

    public static final String APP_CATEGORY_PAAS_CSE_SC_ENDPOINT = "PAAS_CSE_SC_ENDPOINT";

    public static final String APP_CATEGORY_PAAS_CSE_CC_ENDPOINT = "PAAS_CSE_CC_ENDPOINT";

    public static final String APP_CATEGORY_WEBAPP = "Webapp";

    public static final String APP_CATEGORY_MAGENTO = "Magento";

    public static final String APP_CATEGORY_WORDPRESS = "WordPress";

    public static final String APP_CATEGORY_MOBILE = "Mobile";

    public static final String APP_SIZE_OPTION = "size.id";

    public static final String APP_REPLICAS = "size.replica";

    public static final String APP_PORT = "listener_port";

    public static final String SOURCE_TYPE_OPTION = "source.type";

    public static final String SOURCE_TYPE_LOCAL_FILE = "BIN";

    public static final String SOURCE_TYPE_DEVCLOUD = "DevCloud";

    public static final String SOURCE_TYPE_GITHUB = "GitHub";

    public static final String SOURCE_TYPE_GITEE = "Gitee";

    public static final String SOURCE_TYPE_BITBUCKET = "Bitbucket";

    public static final String SOURCE_TYPE_GITLAB = "Gitlab";

    public static final String SOURCE_PATH = "source.repo_url";

    public static final String SOURCE_NAMESPACE = "source.namespace";

    public static final String SOURCE_BRANCH = "source.branch";

    public static final String SOURCE_SECU_TOKEN = "source.secu_token";

    public static final String SWR_REPO = "swr_repo";

    public static final String DIALOG_SETTINGS_FILE_NAME = "servicestage.xml";

    public static final String RDB_ID = "services.relational_database.id";

    public static final String RDB_CONNECTION_TYPE = "services.relational_database.parameters.connection_type";

    public static final String RDB_DB_NAME = "services.relational_database.parameters.db_name";

    public static final String RDB_USER = "services.relational_database.parameters.db_user";

    public static final String RDB_PASSWORD = "services.relational_database.parameters.password";

    public static final String DCS_ID = "distributed_session.id";

    public static final String DCS_PASSWORD = "distributed_session.parameters.password";

    public static final String EXTENDED_PARAM_KEYS = "extended_param_keys";

    public static final String EXTENDED_PARAM_VALUES = "extended_param_values";
}
