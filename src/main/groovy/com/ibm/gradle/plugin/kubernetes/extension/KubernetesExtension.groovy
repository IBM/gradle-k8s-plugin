package com.ibm.gradle.plugin.kubernetes.extension

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class KubernetesExtension {
    IbmCloudExtensionEnhance ibmCloudProvider
    Property<String> configFilePath
    Property<String> namespace
    ListProperty<String> yamlPaths

    @Inject
    KubernetesExtension(ObjectFactory objectFactory){
        ibmCloudProvider = objectFactory.newInstance(IbmCloudExtensionEnhance, objectFactory)
        configFilePath = objectFactory.property(String)
        namespace = objectFactory.property(String)
        yamlPaths = objectFactory.listProperty(String)
    }

    void ibmCloudProvider(Action<? super IbmCloudExtensionEnhance> action) {
        action.execute(ibmCloudProvider)
    }
}
