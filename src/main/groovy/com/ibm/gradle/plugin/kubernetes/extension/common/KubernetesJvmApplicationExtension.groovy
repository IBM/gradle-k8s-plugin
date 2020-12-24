package com.ibm.gradle.plugin.kubernetes.extension.common

import com.bmuschko.gradle.docker.DockerRegistryCredentials
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class KubernetesJvmApplicationExtension {
    Property<String> appName
    KubernetesDeploymentExtension deployment
    KubernetesServiceExtension service

    @Inject
    KubernetesJvmApplicationExtension(ObjectFactory objectFactory) {
        appName = objectFactory.property(String)
        deployment = objectFactory.newInstance(KubernetesDeploymentExtension, objectFactory)
        service = objectFactory.newInstance(KubernetesServiceExtension, objectFactory)
    }

    void deployment(Action<? super KubernetesDeploymentExtension> action) {
        action.execute(deployment)
    }

    void service(Action<? super KubernetesServiceExtension> action) {
        action.execute(service)
    }
}
