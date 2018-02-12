package org.mskcc.igo.pi.cmopatientconverter.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Configuration
public class AppConfig {
    @Value("${crdb.username}")
    private String crdbUsername;

    @Value("${crdb.password}")
    private String crdbPassword;

    @Bean
    @Qualifier("crdbRestTemplate")
    public RestTemplate crdbRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        addBasicAuth(restTemplate, crdbUsername, crdbPassword);

        return restTemplate;
    }

    private void addBasicAuth(RestTemplate restTemplate, String username, String password) {
        List<ClientHttpRequestInterceptor> interceptors = Collections.singletonList(new BasicAuthorizationInterceptor
                (username, password));
        restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(),
                interceptors));
    }
}
