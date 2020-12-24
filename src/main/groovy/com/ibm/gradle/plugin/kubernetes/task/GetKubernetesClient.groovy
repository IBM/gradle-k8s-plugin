package com.ibm.gradle.plugin.kubernetes.task

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import com.ibm.gradle.plugin.kubernetes.enhance.authprovider.OidcOauthTokenProvider
import com.ibm.gradle.plugin.kubernetes.enhance.authprovider.entity.OidcAuthProviderConfig
import com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ClusterConfigProvider
import com.ibm.gradle.plugin.kubernetes.extension.KubernetesExtension
import groovy.transform.CompileStatic
import io.fabric8.kubernetes.api.model.AuthInfo
import io.fabric8.kubernetes.client.Config
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.OAuthTokenProvider
import io.fabric8.kubernetes.client.internal.KubeConfigUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

@CompileStatic
class GetKubernetesClient extends DefaultTask implements KubernetesExtensionAware {

    KubernetesExtension kubernetes

    Property<String> kubernetesMasterUrl

    Property<String> kubernetesCaCertPath

    Property<String> kubernetesOauthToken

    KubernetesClient kubernetesClient

    GetKubernetesClient(){
        kubernetes = project.objects.newInstance(KubernetesExtension, project.objects)
        kubernetesMasterUrl = project.objects.property(String)
        kubernetesCaCertPath = project.objects.property(String)
        kubernetesOauthToken = project.objects.property(String)
    }

    @TaskAction
    void run() {
        if (kubernetes.ibmCloudProvider.apiKey.isPresent() && kubernetes.ibmCloudProvider.clusterId.isPresent()){
            kubernetes.configFilePath.set(ClusterConfigProvider.get("IBMCLOUD").getClusterConfigYamlPath(kubernetes, project))
        }
        if (!kubernetes.configFilePath.isPresent() && (!kubernetesMasterUrl.isPresent() || !kubernetesCaCertPath.isPresent() || !kubernetesOauthToken.isPresent())){
            throw new Exception("kubernetes config is required!")
        }
        if (!kubernetes.configFilePath.isPresent()){
            Config config = new ConfigBuilder()
                    .withMasterUrl(kubernetesMasterUrl.get())
                    .withCaCertFile(kubernetesCaCertPath.get())
                    .withOauthToken(kubernetesOauthToken.get())
                    .build()
            kubernetesClient = new DefaultKubernetesClient(config)
        }else{
            io.fabric8.kubernetes.api.model.Config kubeConfig = KubeConfigUtils.parseConfig(new File(kubernetes.configFilePath.get()))
            AuthInfo currentAuthInfo = KubeConfigUtils.getUserAuthInfo(kubeConfig, KubeConfigUtils.getCurrentContext(kubeConfig).context)
            Map authConfigMap = currentAuthInfo.getAuthProvider().getConfig()
            ObjectMapper objectMapper = new ObjectMapper()
            OidcAuthProviderConfig oidcAuthProviderConfig = objectMapper.readValue(objectMapper.writeValueAsString(authConfigMap), OidcAuthProviderConfig.class)
            OAuthTokenProvider oauthTokenProvider = new OidcOauthTokenProvider(oidcAuthProviderConfig)
            Config config = Config.fromKubeconfig(null, readFile(kubernetes.configFilePath.get(), Charsets.UTF_8), kubernetes.configFilePath.get())
            config.setOauthToken(oauthTokenProvider.getToken())
            kubernetesClient = new DefaultKubernetesClient(config)
        }

    }

    @Override
    void kubernetes(Action<? super KubernetesExtension> action) {
        action.execute(kubernetes)
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path))
        return new String(encoded, encoding)
    }
}
