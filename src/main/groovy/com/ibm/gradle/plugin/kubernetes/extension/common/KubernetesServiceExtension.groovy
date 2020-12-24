package com.ibm.gradle.plugin.kubernetes.extension.common

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty

import javax.inject.Inject

@CompileStatic
class KubernetesServiceExtension {
    MapProperty<Integer, Integer> servicePorts;
    @Inject
    KubernetesServiceExtension(ObjectFactory objectFactory) {
        servicePorts = objectFactory.mapProperty(Integer, Integer)
    }
}
