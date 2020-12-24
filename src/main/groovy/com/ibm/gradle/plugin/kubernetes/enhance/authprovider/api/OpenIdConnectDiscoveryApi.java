package com.ibm.gradle.plugin.kubernetes.enhance.authprovider.api;

import com.ibm.gradle.plugin.kubernetes.enhance.authprovider.api.entity.OpenIdConfiguration;
import com.ibm.gradle.plugin.kubernetes.util.FeignBuilder;
import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

public interface OpenIdConnectDiscoveryApi {

    class Builder{
        public static OpenIdConnectDiscoveryApi build(org.slf4j.Logger logger, String url){
            return FeignBuilder.buildJson(logger)
                    .target(OpenIdConnectDiscoveryApi.class, url);
        }

        public static OpenIdConnectDiscoveryApi build(String url){
            return FeignBuilder.buildJson()
                    .target(OpenIdConnectDiscoveryApi.class, url);
        }
    }

    @Headers({"Content-Type: application/json"})
    @RequestLine("GET /")
    OpenIdConfiguration getOpenIdConfiguration();
}
