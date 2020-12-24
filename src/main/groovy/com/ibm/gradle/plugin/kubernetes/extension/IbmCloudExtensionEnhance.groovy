package com.ibm.gradle.plugin.kubernetes.extension

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class IbmCloudExtensionEnhance extends IbmCloudExtension {
    Property<String> clusterId
    @Inject
    IbmCloudExtensionEnhance(ObjectFactory objectFactory) {
        super(objectFactory)
        clusterId = objectFactory.property(String)
    }
}
