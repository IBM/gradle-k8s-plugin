package com.ibm.gradle.plugin.kubernetes.extension

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class IbmCloudExtension {
    final Property<String> apiKey
    @Inject
    IbmCloudExtension(ObjectFactory objectFactory) {
        apiKey = objectFactory.property(String)
        apiKey.set("")
    }
}
