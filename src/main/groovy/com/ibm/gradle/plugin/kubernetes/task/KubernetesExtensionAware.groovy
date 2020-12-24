package com.ibm.gradle.plugin.kubernetes.task

import com.ibm.gradle.plugin.kubernetes.extension.KubernetesExtension
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.tasks.Nested

@CompileStatic
interface KubernetesExtensionAware extends Task {

    @Nested
    KubernetesExtension getKubernetes()

    void kubernetes(Action<? super KubernetesExtension> action)
}
