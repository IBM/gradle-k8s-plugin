package com.ibm.gradle.plugin.kubernetes.extension.common

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class KubernetesDeploymentExtension {
    Property<String> image
    Property<Integer> replicas
    MapProperty<String, Object> envs
    MapProperty<String, String> volumeMounts
    MapProperty<String, Object> volumes
    @Inject
    KubernetesDeploymentExtension(ObjectFactory objectFactory) {
        image = objectFactory.property(String)
        replicas = objectFactory.property(Integer)
        envs = objectFactory.mapProperty(String, Object)
        volumeMounts = objectFactory.mapProperty(String, String)
        volumes = objectFactory.mapProperty(String, Object)
    }

}
