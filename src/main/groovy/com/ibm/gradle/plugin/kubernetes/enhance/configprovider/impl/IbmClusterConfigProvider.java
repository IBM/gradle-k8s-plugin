package com.ibm.gradle.plugin.kubernetes.enhance.configprovider.impl;

import com.google.common.io.Files;
import com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ClusterConfigProvider;
import com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ibmcloud.api.IamApi;
import com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ibmcloud.api.KubernetesApi;
import com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ibmcloud.api.entity.IamToken;
import com.ibm.gradle.plugin.kubernetes.extension.KubernetesExtension;
import com.ibm.gradle.plugin.kubernetes.extension.common.KubernetesJvmApplicationExtension;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


@Data
@Slf4j
public class IbmClusterConfigProvider implements ClusterConfigProvider {
    @Override
    public String getClusterConfigYamlPath(KubernetesExtension kubernetesExtension, Project project) {
        final String[] clusterConfigYamlPath = {""};
        final String[] clusterCertPath = {""};
        IamToken iamToken = IamApi.Builder.buildBluemix(log).getBluemixToken(kubernetesExtension.getIbmCloudProvider().getApiKey().get());
        byte[] clusterConfigZip  = KubernetesApi.Builder.buildKubernetesConfig(log).getClusterConfigZip(iamToken.getAccessToken(), iamToken.getRefreshToken(), kubernetesExtension.getIbmCloudProvider().getClusterId().get());
        String unZipPath = project.getBuildDir().getPath() + "/cluster/config/";
        File unZipDir = new File(unZipPath);
        File zipFile = new File(unZipPath + "clusterConfig.zip");
        try {
            if (!unZipDir.mkdirs()){
                deleteRecursive(unZipDir.getParentFile());
                unZipDir.mkdirs();
            }
            zipFile.createNewFile();
            Files.write(clusterConfigZip, zipFile);
            new ZipFile(unZipPath + "clusterConfig.zip").extractAll(unZipPath);
            Arrays.stream(Objects.requireNonNull(unZipDir.listFiles())).forEach(folder -> {
                if (folder.isDirectory()){
                    Arrays.stream(Objects.requireNonNull(folder.listFiles())).forEach(file -> {
                        if (file.getPath().endsWith("yml")){
                            clusterConfigYamlPath[0] = file.getPath();
                        }
                        if (file.getPath().endsWith("pem")){
                            clusterCertPath[0] = file.getPath();
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clusterConfigYamlPath[0];
    }

    static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) {
            throw new FileNotFoundException(path.getAbsolutePath());
        }
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

}
