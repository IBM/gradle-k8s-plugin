package com.ibm.gradle.plugin.kubernetes.extension.common

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class KubernetesDeploymentEnvValueFrom {
    Property<String> valueFrom
    Property<String> name
    Property<String> key

    @Inject
    KubernetesDeploymentEnvValueFrom(ObjectFactory objectFactory) {
        valueFrom = objectFactory.property(String)
        name = objectFactory.property(String)
        key = objectFactory.property(String)
    }
}
