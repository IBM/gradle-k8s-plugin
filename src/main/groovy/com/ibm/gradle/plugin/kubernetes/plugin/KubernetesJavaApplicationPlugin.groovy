package com.ibm.gradle.plugin.kubernetes.plugin

import com.bmuschko.gradle.docker.DockerExtension
import com.bmuschko.gradle.docker.DockerJavaApplication
import com.bmuschko.gradle.docker.internal.MainClassFinder
import com.ibm.gradle.plugin.kubernetes.extension.KubernetesExtension
import com.ibm.gradle.plugin.kubernetes.extension.KubernetesJavaApplicationExtension
import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware

@CompileStatic
class KubernetesJavaApplicationPlugin extends KubernetesPlugin<KubernetesJavaApplicationExtension, DockerJavaApplication> {


    @Override
    protected KubernetesJavaApplicationExtension configureExtension(ObjectFactory objectFactory, KubernetesExtension kubernetesExtension) {
        KubernetesJavaApplicationExtension kubernetesJavaApplicationExtension = ((ExtensionAware) kubernetesExtension).extensions.create('kubernetesJavaApplication', KubernetesJavaApplicationExtension, objectFactory)
        return kubernetesJavaApplicationExtension
    }

    @Override
    protected DockerJavaApplication configureExtension(ObjectFactory objectFactory, DockerExtension dockerExtension) {
        ((ExtensionAware) dockerExtension).extensions.create('javaApplication', DockerJavaApplication, objectFactory)
    }

    @Override
    protected String findMainClassName(File classesDir) {
        MainClassFinder.findSingleMainClass(classesDir)
    }
}
