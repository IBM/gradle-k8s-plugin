package com.ibm.gradle.plugin.kubernetes.extension

import com.ibm.gradle.plugin.kubernetes.extension.common.KubernetesJvmApplicationExtension
import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

@CompileStatic
class KubernetesJavaApplicationExtension extends KubernetesJvmApplicationExtension {
    @Inject
    KubernetesJavaApplicationExtension(ObjectFactory objectFactory) {
        super(objectFactory)
    }
}
