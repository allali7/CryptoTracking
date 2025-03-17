/**
 * HuaiHao Mo
 */
package com.dummynode.cryptotrackingbackend.service.interceptor;

import com.dummynode.cryptotrackingbackend.tool.ConstantKt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class ApiInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add("X-CMC_PRO_API_KEY", ConstantKt.X_CMC_PRO_API_KEY);
        return execution.execute(request, body);
    }
}
