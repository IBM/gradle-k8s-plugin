package com.ibm.gradle.plugin.kubernetes.enhance.authprovider.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * refers to https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata
 * list only required fields
 */
public class OpenIdConfiguration {
    private String issuer;
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    @JsonProperty("userinfo_endpoint")
    private String userinfoEndpoint;
    @JsonProperty("jwks_uri")
    private String jwksUri;
    @JsonProperty("response_types_supported")
    private String responseTypesSupported;
    @JsonProperty("subject_types_supported")
    private String subjectTypesSupported;
    @JsonProperty("id_token_signing_alg_values_supported")
    private String idTokenSigningAlgValuesSupported;

}
