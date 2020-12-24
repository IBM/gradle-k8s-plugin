package com.ibm.gradle.plugin.kubernetes.enhance.authprovider.api;

import com.ibm.gradle.plugin.kubernetes.enhance.authprovider.api.entity.OpenIdToken;
import com.ibm.gradle.plugin.kubernetes.util.FeignBuilder;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Base64;

public interface OpenIdTokenApi {

    class Builder{
        public static OpenIdTokenApi build(org.slf4j.Logger logger, String url){
            return FeignBuilder.buildJson(logger)
                    .target(OpenIdTokenApi.class, url);
        }

        public static OpenIdTokenApi build(String url){
            return FeignBuilder.buildJson()
                    .target(OpenIdTokenApi.class, url);
        }
    }

    default OpenIdToken refreshToken(String refreshToken, String clientId, String clientSecret){
        return refreshToken(refreshToken, Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()));
    }

    @Headers({"Content-Type: application/json", "Authorization: Basic {basicToken}"})
    @RequestLine("POST /?grant_type=refresh_token&refresh_token={refreshToken}")
    OpenIdToken refreshToken(@Param("refreshToken") String refreshToken, @Param("basicToken") String basicToken);
}
