package com.ibm.gradle.plugin.kubernetes.enhance.configprovider;

import com.ibm.gradle.plugin.kubernetes.enhance.configprovider.impl.IbmClusterConfigProvider;
import com.ibm.gradle.plugin.kubernetes.extension.KubernetesExtension;
import com.ibm.gradle.plugin.kubernetes.extension.common.KubernetesJvmApplicationExtension;
import org.gradle.api.Project;

public interface ClusterConfigProvider {

    String getClusterConfigYamlPath(KubernetesExtension kubernetesExtension, Project project);

    static ClusterConfigProvider get(String provider){
        switch (provider){
            case "IBMCLOUD": return new IbmClusterConfigProvider();
            case "AWS": return null;
            case "AZURE": return null;
            case "ALICLOUD": return null;
            default: return null;
        }
    }
}
