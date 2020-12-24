package com.ibm.gradle.plugin.kubernetes.enhance.authprovider;

import com.ibm.gradle.plugin.kubernetes.enhance.authprovider.api.OpenIdConnectDiscoveryApi;
import com.ibm.gradle.plugin.kubernetes.enhance.authprovider.api.OpenIdTokenApi;
import com.ibm.gradle.plugin.kubernetes.enhance.authprovider.api.entity.OpenIdConfiguration;
import com.ibm.gradle.plugin.kubernetes.enhance.authprovider.entity.OidcAuthProviderConfig;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.fabric8.kubernetes.client.OAuthTokenProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OidcOauthTokenProvider implements OAuthTokenProvider {

    private OidcAuthProviderConfig oidcAuthProviderConfig;

    @Override
    public String getToken() {
        try {
            if (oidcAuthProviderConfig.getIdToken() != null && !checkTokenExpired(oidcAuthProviderConfig.getIdToken())){
                return oidcAuthProviderConfig.getIdToken();
            }else {
                String openIdConnectDiscoveryEndpoint;
                if (oidcAuthProviderConfig.getIdpIssuerUrl().endsWith("/")){
                    openIdConnectDiscoveryEndpoint = oidcAuthProviderConfig.getIdpIssuerUrl().substring(0, oidcAuthProviderConfig.getIdpIssuerUrl().length()-1) + "/.well-known/openid-configuration";
                }else {
                    openIdConnectDiscoveryEndpoint = oidcAuthProviderConfig.getIdpIssuerUrl() + "/.well-known/openid-configuration";
                }
                OpenIdConfiguration openIdConfiguration = OpenIdConnectDiscoveryApi.Builder.build(openIdConnectDiscoveryEndpoint).getOpenIdConfiguration();
                return OpenIdTokenApi.Builder.build(openIdConfiguration.getTokenEndpoint()).refreshToken(oidcAuthProviderConfig.getRefreshToken(), oidcAuthProviderConfig.getClientId(), oidcAuthProviderConfig.getClientSecret()).getIdToken();
            }
        } catch (MalformedURLException | ParseException | JOSEException | BadJOSEException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkTokenExpired(String idToken) throws MalformedURLException, ParseException, JOSEException, BadJOSEException {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL("https://iam-id-1.au-syd.bluemix.net/identity/keys"), new DefaultResourceRetriever(5000, 5000));
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier());
        JWTClaimsSet claimsSet = jwtProcessor.process(idToken, null);
        Date currentDate = new Date();
        if (currentDate.after(claimsSet.getExpirationTime())){
            return true;
        }
        return false;
    }

}
