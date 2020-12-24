package com.ibm.gradle.plugin.kubernetes.task

import groovy.transform.CompileStatic
import io.fabric8.kubernetes.api.model.ConfigMapList
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

@CompileStatic
class GetConfigMaps extends GetKubernetesClient {

    Property<String> namespace

    GetConfigMaps(){
        namespace = project.objects.property(String)
        if (kubernetes.namespace.isPresent()){
            namespace.set(kubernetes.namespace.get())
        }
    }

    ConfigMapList configMapList

    @TaskAction
    @Override
    void run() {
        if (!namespace.isPresent()){
            namespace.set("default")
        }
        super.run()
        configMapList = kubernetesClient.configMaps().inNamespace(namespace.get()).list()
    }

}
