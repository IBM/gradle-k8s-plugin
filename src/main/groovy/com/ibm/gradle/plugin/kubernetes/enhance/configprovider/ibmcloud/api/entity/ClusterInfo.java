package com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ibmcloud.api.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class ClusterInfo {
    private String id;
    private String name;
    private String region;
    private String resourceGroup;
    private String resourceGroupName;
    private String serverURL;
    private String ingressSecretName;
    private String ingressHostname;
    private String masterStatus;
    private String masterHealth;
    private String masterState;
    private String privateServiceEndpointURL;
    private String publicServiceEndpointURL;

}
