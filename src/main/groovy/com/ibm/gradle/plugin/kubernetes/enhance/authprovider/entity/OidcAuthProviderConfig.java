package com.ibm.gradle.plugin.kubernetes.enhance.authprovider.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OidcAuthProviderConfig {
    @JsonProperty("client-id")
    private String clientId;
    @JsonProperty("client-secret")
    private String clientSecret;
    @JsonProperty("id-token")
    private String idToken;
    @JsonProperty("idp-certificate-authority")
    private String idpCertificateAuthority;
    @JsonProperty("idp-issuer-url")
    private String idpIssuerUrl;
    @JsonProperty("refresh-token")
    private String refreshToken;
}
