package com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ibmcloud.api;

import com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ibmcloud.api.entity.ClusterInfo;
import com.ibm.gradle.plugin.kubernetes.util.FeignBuilder;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface KubernetesApi {

    class Builder {
        public static KubernetesApi build(org.slf4j.Logger logger) {
            return FeignBuilder.buildJson(logger)
                    .target(KubernetesApi.class, "https://containers.cloud.ibm.com");
        }

        public static KubernetesConfigApi buildKubernetesConfig(org.slf4j.Logger logger) {
            return FeignBuilder.buildDownload(logger)
                  .target(KubernetesConfigApi.class, "https://containers.cloud.ibm.com");
        }
    }
    // fake user agent, java is not acceptable by ibmcloud container api

    @Headers({"Content-Type: application/json","Authorization: Bearer {iamToken}","User-Agent: PostmanRuntime/7.20.1"})
    @RequestLine("GET /global/v1/clusters/{clusterId}?showResources=false")
    ClusterInfo getClusterInfo(@Param("iamToken") String iamToken, @Param("clusterId") String clusterId);

    interface KubernetesConfigApi {
        @Headers({"Content-Type: application/json","Authorization: Bearer {iamToken}", "X-Auth-Refresh-Token: {iamRefreshToken}", "User-Agent: PostmanRuntime/7.20.1"})
        @RequestLine("GET /global/v1/clusters/{clusterId}/config")
        byte[] getClusterConfigZip(@Param("iamToken") String iamToken, @Param("iamRefreshToken") String iamRefreshToken, @Param("clusterId") String clusterId);
    }

}
