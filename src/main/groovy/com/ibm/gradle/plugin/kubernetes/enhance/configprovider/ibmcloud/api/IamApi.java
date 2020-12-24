package com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ibmcloud.api;

import com.google.common.collect.ImmutableMap;
import com.ibm.gradle.plugin.kubernetes.enhance.configprovider.ibmcloud.api.entity.IamToken;
import com.ibm.gradle.plugin.kubernetes.util.FeignBuilder;
import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public interface IamApi {

    class Builder{
        public static IamApi build(org.slf4j.Logger logger){
            return FeignBuilder.buildForm(logger)
                    .target(IamApi.class, "https://iam.cloud.ibm.com/identity");
        }
        public static KubernetesIamApi buildKubernetes(org.slf4j.Logger logger){
            return FeignBuilder.buildForm(logger)
                    .target(KubernetesIamApi.class, "https://iam.bluemix.net/identity");
        }
        public static BluemixIamApi buildBluemix(org.slf4j.Logger logger){
            return FeignBuilder.buildForm(logger)
                    .target(BluemixIamApi.class, "https://iam.cloud.ibm.com/identity");
        }

    }

    default IamToken getIamToken(String apiKey){
        return getIamToken("urn:ibm:params:oauth:grant-type:apikey", apiKey, new HashMap<>());
    }

    @Headers({"Content-Type: application/x-www-form-urlencoded", "User-Agent: PostmanRuntime/7.20.1"})
    @RequestLine("POST /token")
    IamToken getIamToken(@Param("grant_type") String grantType, @Param("apikey") String apikey, @HeaderMap Map<String, Object> headerMap);

    interface KubernetesIamApi extends IamApi {
        default IamToken getKubernetesToken(String apiKey){
            return getIamToken("urn:ibm:params:oauth:grant-type:apikey", apiKey, ImmutableMap.of("Authorization", "Basic " + Base64.getEncoder().encodeToString("kube:kube".getBytes())));
        }
    }

    interface BluemixIamApi extends IamApi {
        default IamToken getBluemixToken(String apiKey){
            return getIamToken("urn:ibm:params:oauth:grant-type:apikey", apiKey, ImmutableMap.of("Authorization", "Basic " + Base64.getEncoder().encodeToString("bx:bx".getBytes())));
        }
    }
}
