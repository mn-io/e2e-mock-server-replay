package net.mnio.services;

import net.mnio.apiclient.RequestResponseLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class CallOtherApiService {
    private final static Logger LOG = getLogger(CallOtherApiService.class);

    @Value("${test.mockServer.dir}")
    private String requestResponseLogDir;

    @Value("${app.restApiUrl}")
    private String restApiUrl;

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    // this is called via test and it's request is answered by our mock server
    public Response doGetRequest() throws IOException {
        LOG.info("Performing GET request to %s - logging to directory '%s'".formatted(restApiUrl, requestResponseLogDir));

        final Request request = new Request.Builder()
                .get()
                .url(restApiUrl)
                .build();

        final OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        if (!Objects.equals(activeProfile, "test")) {
            // this creates some serialized request response data within mockServerReplays/
            builder.addInterceptor(new RequestResponseLogger(requestResponseLogDir));
        }

        final OkHttpClient client = builder.build();
        final Response response = client
                .newCall(request)
                .execute();
        return response;
    }
}