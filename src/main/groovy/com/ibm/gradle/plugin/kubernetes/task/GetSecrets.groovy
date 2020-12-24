package com.ibm.gradle.plugin.kubernetes.task

import groovy.transform.CompileStatic
import io.fabric8.kubernetes.api.model.ConfigMapList
import io.fabric8.kubernetes.api.model.SecretList
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

@CompileStatic
class GetSecrets extends GetKubernetesClient {

    final Property<String> namespace

    GetSecrets(){
        namespace = project.objects.property(String)
        if (kubernetes.namespace.isPresent()){
            namespace.set(kubernetes.namespace.get())
        }
    }

    SecretList secretList

    @TaskAction
    @Override
    void run() {
        if (!namespace.isPresent()){
            namespace.set("default")
        }
        super.run()
        secretList = kubernetesClient.secrets().inNamespace(namespace.get()).list()
    }

}
