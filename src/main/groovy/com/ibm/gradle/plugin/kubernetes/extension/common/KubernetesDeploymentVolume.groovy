package com.ibm.gradle.plugin.kubernetes.extension.common

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class KubernetesDeploymentVolume {
    Property<String> volumeFrom
    Property<String> name
    @Inject
    KubernetesDeploymentVolume(ObjectFactory objectFactory) {
        volumeFrom = objectFactory.property(String)
        name = objectFactory.property(String)
    }
}
