package com.ibm.gradle.plugin.kubernetes.plugin

import com.bmuschko.gradle.docker.DockerConventionJvmApplicationExtension
import com.bmuschko.gradle.docker.DockerConventionJvmApplicationPlugin
import com.bmuschko.gradle.docker.DockerExtension
import com.fasterxml.jackson.databind.ObjectMapper
import com.ibm.gradle.plugin.kubernetes.extension.KubernetesExtension
import com.ibm.gradle.plugin.kubernetes.extension.common.KubernetesJvmApplicationExtension
import com.ibm.gradle.plugin.kubernetes.task.ApplyServices
import com.ibm.gradle.plugin.kubernetes.task.DeployToKubernetes
import com.ibm.gradle.plugin.kubernetes.task.GetConfigMaps
import com.ibm.gradle.plugin.kubernetes.task.GetKubernetesClient
import com.ibm.gradle.plugin.kubernetes.task.GetSecrets
import com.ibm.gradle.plugin.kubernetes.task.KubernetesExtensionAware
import com.ibm.gradle.plugin.kubernetes.task.entity.EnvValueFrom
import com.ibm.gradle.plugin.kubernetes.task.entity.Volume
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider


@CompileStatic(TypeCheckingMode.SKIP)
abstract class KubernetesPlugin<T extends KubernetesJvmApplicationExtension, E extends DockerConventionJvmApplicationExtension> extends DockerConventionJvmApplicationPlugin<E> {

    public static final String EXTENSION_NAME = "kubernetes"

    @Override
    void apply(Project project) {
        super.apply(project)
        createKubernetesBasicPlugin(project)
        KubernetesExtension kubernetesExtension = project.extensions.getByType(KubernetesExtension)
        T kubernetesJvmApplicationExtension = configureExtension(project.objects, kubernetesExtension)
        DockerConventionJvmApplicationExtension dockerConventionJvmApplicationExtension = ((ExtensionAware)project.extensions.getByType(DockerExtension)).extensions.getByType(DockerConventionJvmApplicationExtension)
        project.plugins.withType(JavaPlugin) {
            TaskProvider<DeployToKubernetes> deployToKubernetesTask = registerDeployToKubernetesTask(project, kubernetesJvmApplicationExtension, dockerConventionJvmApplicationExtension, kubernetesExtension)
                deployToKubernetesTask.configure(new Action<DeployToKubernetes>() {
                    @Override
                    void execute(DeployToKubernetes deployToKubernetes) {
                        if (deployToKubernetes.dependsOnDockerPlugin) {
                            deployToKubernetes.dependsOn(project.tasks.named("dockerPushImage"))
                        }
                    }
                })
        }
    }

    static void createKubernetesBasicPlugin(Project project){
        KubernetesExtension kubernetesExtension = project.extensions.create(EXTENSION_NAME, KubernetesExtension, project.objects)
        configureKubernetesExtensionAwareTasks(project, kubernetesExtension)
        project.getTasks().register("getKubernetesClient", GetKubernetesClient)
        project.getTasks().register("getConfigMaps", GetConfigMaps)
        project.getTasks().register("getSecrets", GetSecrets)
        project.getTasks().register("applyServices", ApplyServices)
    }

    private static void configureKubernetesExtensionAwareTasks(Project project, KubernetesExtension kubernetesExtension){
        project.tasks.withType(KubernetesExtensionAware).configureEach(new Action<KubernetesExtensionAware>() {
            @Override
            void execute(KubernetesExtensionAware task) {
                task.kubernetes.ibmCloudProvider.apiKey.set(kubernetesExtension.ibmCloudProvider.apiKey)
                task.kubernetes.ibmCloudProvider.clusterId.set(kubernetesExtension.ibmCloudProvider.clusterId)
                task.kubernetes.configFilePath.set(kubernetesExtension.configFilePath)
                task.kubernetes.namespace.set(kubernetesExtension.namespace)
                task.kubernetes.yamlPaths.set(kubernetesExtension.yamlPaths)
            }
        })
    }

    private static TaskProvider<DeployToKubernetes> registerDeployToKubernetesTask(Project project, KubernetesJvmApplicationExtension kubernetesJvmApplicationExtension, DockerConventionJvmApplicationExtension dockerConventionJvmApplicationExtension, KubernetesExtension kubernetesExtension) {
        project.tasks.register("deployToKubernetes", DeployToKubernetes, new Action<DeployToKubernetes>() {
            @Override
            void execute(DeployToKubernetes deployToKubernetes) {
                deployToKubernetes.with {
                    appName = kubernetesJvmApplicationExtension.appName
                    namespace = kubernetesExtension.namespace
                    image = kubernetesJvmApplicationExtension.deployment.image
                    if (!image.isPresent()){
                        if (dockerConventionJvmApplicationExtension.images.isPresent() && dockerConventionJvmApplicationExtension.images.get().size() > 0){
                            image.set(dockerConventionJvmApplicationExtension.images.get().toArray()[0] as String)
                        }else {
                            String tagVersion = project.version == 'unspecified' ? 'latest' : project.version
                            String artifactAndVersion = "${project.name}:${tagVersion}".toLowerCase().toString()
                            image.set(project.group ? "$project.group/$artifactAndVersion".toString() : artifactAndVersion)
                        }
                    }else {
                        dependsOnDockerPlugin = false
                    }
                    replicas = kubernetesJvmApplicationExtension.deployment.replicas
                    if (kubernetesJvmApplicationExtension.service.servicePorts.isPresent() && kubernetesJvmApplicationExtension.service.servicePorts.get().size() > 0){
                        ports.addAll(kubernetesJvmApplicationExtension.service.servicePorts.get().values())
                    }
                    if (!ports.isPresent() || ports.get().size() == 0){
                        if (dockerConventionJvmApplicationExtension.ports.isPresent() && dockerConventionJvmApplicationExtension.ports.get().size() > 0){
                            ports.addAll(dockerConventionJvmApplicationExtension.ports.get())
                        }else {
                            ports.add(8080)
                        }
                    }
                    volumeMounts = kubernetesJvmApplicationExtension.deployment.volumeMounts
                    servicePorts = kubernetesJvmApplicationExtension.service.servicePorts
                    if (!servicePorts.isPresent() || servicePorts.get().size() <= 0){
                        servicePorts.put(80, ports.get().get(0) as Integer)
                    }
                    if (kubernetesJvmApplicationExtension.deployment.envs.isPresent() && kubernetesJvmApplicationExtension.deployment.envs.get().size() > 0){
                        kubernetesJvmApplicationExtension.deployment.envs.get().each {
                            if (it.value instanceof String){
                                envs.put(it.key, it.value)
                            }else {
                                EnvValueFrom envValueFrom = new ObjectMapper().convertValue(it.value as Map, EnvValueFrom)
                                envs.put(it.key, envValueFrom)
                            }
                        }
                    }
                    if (kubernetesJvmApplicationExtension.deployment.volumes.isPresent() && kubernetesJvmApplicationExtension.deployment.volumes.get().size() > 0){
                        kubernetesJvmApplicationExtension.deployment.volumes.get().each {
                            Volume volume = new ObjectMapper().convertValue(it.value as Map, Volume)
                            volumes.put(it.key, volume)
                        }
                    }
                }
            }
        })
    }

    protected abstract T configureExtension(ObjectFactory objectFactory, KubernetesExtension kubernetesExtension)

}
