package com.ibm.gradle.plugin.kubernetes.task

import groovy.transform.CompileStatic
import io.fabric8.kubernetes.api.model.HasMetadata
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

@CompileStatic
class ApplyServices extends GetKubernetesClient {

    final Property<String> namespace

    final ListProperty<String> serviceYamlFilePaths

    final ListProperty<String> serviceYamlContents

    ApplyServices(){
        namespace = project.objects.property(String)
        serviceYamlFilePaths = project.objects.listProperty(String)
        serviceYamlContents = project.objects.listProperty(String)
        if (kubernetes.namespace.isPresent()){
            namespace.set(kubernetes.namespace.get())
        }
        if (kubernetes.yamlPaths.isPresent()){
            serviceYamlFilePaths.set(kubernetes.yamlPaths.get())
        }
    }

    @TaskAction
    @Override
    void run() {
        if (!namespace.isPresent()){
            namespace.set("default")
        }
        super.run()
        serviceYamlFilePaths.get().each {serviceYamlFilePath ->
            List<HasMetadata> results = kubernetesClient.load(new FileInputStream((String)serviceYamlFilePath)).get()
            kubernetesClient.resourceList(results).inNamespace(namespace.get()).deletingExisting().createOrReplace()
        }
        serviceYamlContents.get().each {serviceYamlContent ->
            List<HasMetadata> results = kubernetesClient.load(new ByteArrayInputStream((byte[])((String)serviceYamlContent).getBytes())).get()
            kubernetesClient.resourceList(results).inNamespace(namespace.get()).deletingExisting().createOrReplace()
        }
    }

}
