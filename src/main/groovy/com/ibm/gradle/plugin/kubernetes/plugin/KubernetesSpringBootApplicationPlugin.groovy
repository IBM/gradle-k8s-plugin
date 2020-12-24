package com.ibm.gradle.plugin.kubernetes.plugin

import com.bmuschko.gradle.docker.DockerExtension
import com.bmuschko.gradle.docker.DockerSpringBootApplication
import com.bmuschko.gradle.docker.internal.MainClassFinder
import com.ibm.gradle.plugin.kubernetes.extension.KubernetesExtension
import com.ibm.gradle.plugin.kubernetes.extension.KubernetesSpringBootApplicationExtension
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware

@CompileStatic
class KubernetesSpringBootApplicationPlugin extends KubernetesPlugin<KubernetesSpringBootApplicationExtension, DockerSpringBootApplication> {

    @Override
    protected KubernetesSpringBootApplicationExtension configureExtension(ObjectFactory objectFactory, KubernetesExtension kubernetesExtension) {
        KubernetesSpringBootApplicationExtension kubernetesSpringBootApplicationExtension = ((ExtensionAware) kubernetesExtension).extensions.create('kubernetesSpringBootApplication', KubernetesSpringBootApplicationExtension, objectFactory)
        if (kubernetesSpringBootApplicationExtension.springBootActiveProfiles.getOrNull()){
            kubernetesSpringBootApplicationExtension.deployment.envs.put('SPRING_PROFILES_ACTIVE', kubernetesSpringBootApplicationExtension.springBootActiveProfiles.get())
        }
        return kubernetesSpringBootApplicationExtension
    }

    @Override
    protected DockerSpringBootApplication configureExtension(ObjectFactory objectFactory, DockerExtension dockerExtension) {
        ((ExtensionAware) dockerExtension).extensions.create('springBootApplication', DockerSpringBootApplication, objectFactory)
    }

    @Override
    protected String findMainClassName(File classesDir) {
        MainClassFinder.findSingleMainClass(classesDir, 'org.springframework.boot.autoconfigure.SpringBootApplication')
    }
}
