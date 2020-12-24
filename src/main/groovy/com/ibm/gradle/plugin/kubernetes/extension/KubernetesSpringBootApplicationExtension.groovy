package com.ibm.gradle.plugin.kubernetes.extension

import com.ibm.gradle.plugin.kubernetes.extension.common.KubernetesJvmApplicationExtension
import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class KubernetesSpringBootApplicationExtension extends KubernetesJvmApplicationExtension {
    Property<String> springBootActiveProfiles
    @Inject
    KubernetesSpringBootApplicationExtension(ObjectFactory objectFactory) {
        super(objectFactory)
        springBootActiveProfiles = objectFactory.property(String)
    }
}
