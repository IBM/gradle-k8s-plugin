package com.ibm.gradle.plugin.kubernetes.task

import com.ibm.gradle.plugin.kubernetes.extension.common.ValueFrom
import com.ibm.gradle.plugin.kubernetes.extension.common.VolumeFrom
import com.ibm.gradle.plugin.kubernetes.task.entity.EnvValueFrom
import com.ibm.gradle.plugin.kubernetes.task.entity.Volume
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import io.fabric8.kubernetes.api.model.ContainerPort
import io.fabric8.kubernetes.api.model.ContainerPortBuilder
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.EnvVarBuilder
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.ServiceBuilder
import io.fabric8.kubernetes.api.model.ServicePort
import io.fabric8.kubernetes.api.model.ServicePortBuilder
import io.fabric8.kubernetes.api.model.VolumeBuilder
import io.fabric8.kubernetes.api.model.VolumeMount
import io.fabric8.kubernetes.api.model.VolumeMountBuilder
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction


@CompileStatic(TypeCheckingMode.SKIP)
class DeployToKubernetes extends GetKubernetesClient {

    boolean dependsOnDockerPlugin = true

    Property<String> appName

    Property<String> namespace

    Property<String> image

    Property<Integer> replicas

    ListProperty<Integer> ports

    MapProperty<String, Object> envs

    MapProperty<String, String> volumeMounts

    MapProperty<String, Volume> volumes

    MapProperty<Integer, Integer> servicePorts

    DeployToKubernetes(){
        appName = project.objects.property(String)
        namespace = project.objects.property(String)
        image = project.objects.property(String)
        replicas = project.objects.property(Integer)
        ports = project.objects.listProperty(Integer)
        envs = project.objects.mapProperty(String, Object)
        volumeMounts = project.objects.mapProperty(String, String)
        volumes = project.objects.mapProperty(String, Volume)
        servicePorts = project.objects.mapProperty(Integer, Integer)
    }

    @TaskAction
    @Override
    void run() {
        super.run()
        if (!appName.isPresent()){
            appName.set(project.name)
        }
        if (!namespace.isPresent()){
            namespace.set('default')
        }
        if (!image.isPresent()){
            throw new Exception("image is required!")
        }
        if (!replicas.isPresent()){
            replicas.set(1)
        }
        if (!ports.isPresent()){
            ports.add(8080)
        }
        if (!envs.isPresent()){
            envs.set(new HashMap<String, Object>())
        }
        if (!volumeMounts.isPresent()){
            volumeMounts.set(new HashMap<String, String>())
        }
        if (!volumes.isPresent()){
            volumes.set(new HashMap<String, Volume>())
        }
        if (!servicePorts.isPresent()){
            servicePorts.put(80, 8080)
        }
        // apply service
        List<ServicePort> servicePorts1 = new ArrayList<>()
        servicePorts.get().each {servicePorts1.add( new ServicePortBuilder().withNewProtocol("TCP").withNewPort(it.key).withNewTargetPort(it.value).build())}
        Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(appName.get())
                    .addToLabels("app", appName.get())
                .endMetadata()
                .withNewSpec()
                    .withType("NodePort")
                    .withPorts(servicePorts1)
                .endSpec()
                .build()
        logger.info("build service : " + service.toString())
        kubernetesClient.services().inNamespace(namespace.get()).createOrReplace(service)
        // apply deployment
        List<EnvVar> envVars1 = new ArrayList<>()
        envs.get().each {
            if (it.value instanceof String){
                envVars1.add(new EnvVarBuilder().withNewName(it.key).withNewValue(it.value as String).build())
            }else {
                EnvValueFrom envValueFrom = it.value as EnvValueFrom
                if (envValueFrom.valueFrom.equalsIgnoreCase("configmap")){
                    envVars1.add(new EnvVarBuilder().withNewName(it.key).withNewValueFrom().withNewConfigMapKeyRef(envValueFrom.key, envValueFrom.name, false).endValueFrom().build())
                }else if (envValueFrom.valueFrom.equalsIgnoreCase("secret")){
                    envVars1.add(new EnvVarBuilder().withNewName(it.key).withNewValueFrom().withNewSecretKeyRef(envValueFrom.key, envValueFrom.name, false).endValueFrom().build())
                }
            }
        }
        if (!volumes.get().keySet().containsAll(volumeMounts.get().keySet())){
            throw new Exception("wrong volumeMounts")
        }
        List<io.fabric8.kubernetes.api.model.Volume> volumes1 = new ArrayList<>()
        volumes.get().each {
            if (it.value.volumeFrom.equalsIgnoreCase("configmap")){
                volumes1.add(new VolumeBuilder().withNewName(it.key).withNewConfigMap().withNewName(it.value.name).endConfigMap().build())
            }else if (it.value.volumeFrom.equalsIgnoreCase("secret")){
                volumes1.add(new VolumeBuilder().withNewName(it.key).withNewSecret().withNewSecretName(it.value.name).endSecret().build())
            }
        }
        List<VolumeMount> volumeMounts1 = new ArrayList<>()
        volumeMounts.get().each {
            volumeMounts1.add(new VolumeMountBuilder().withNewName(it.key).withNewMountPath(it.value).build())
        }
        List<ContainerPort> containerPorts1 = new ArrayList<>()
        ports.get().each {
            containerPorts1.add(new ContainerPortBuilder().withNewContainerPort(it).build())
        }
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(appName.get())
                    .addToLabels("app", appName.get())
                .endMetadata()
                .withNewSpec()
                    .withReplicas(replicas.get())
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app", appName.get())
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName(appName.get())
                                .withImage(image.get())
                                .withImagePullPolicy("Always")
                                .withPorts(containerPorts1)
                                .withEnv(envVars1)
                                .withVolumeMounts(volumeMounts1)
                            .endContainer()
                            .withVolumes(volumes1)
                        .endSpec()
                    .endTemplate()
                    .withNewSelector()
                        .addToMatchLabels("app", appName.get())
                    .endSelector()
                .endSpec()
                .build()
        logger.info("build deployment : " + deployment.toString())
        kubernetesClient.apps().deployments().inNamespace(namespace.get()).createOrReplace(deployment)
    }
}
