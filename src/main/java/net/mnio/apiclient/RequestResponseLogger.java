package net.mnio.apiclient;

import net.mnio.apiclient.dto.RequestResponseData;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import static net.mnio.Factory.YAML_PARSER;
import static org.slf4j.LoggerFactory.getLogger;

public class RequestResponseLogger implements Interceptor {

    private final static Logger LOG = getLogger(RequestResponseLogger.class);

    private final static long START_TIME = new Date().getTime();

    private final String dir;

    public RequestResponseLogger(final String dir) {
        this.dir = dir;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        final long diff = new Date().getTime() - START_TIME;
        final File logFile = new File("%s/request-response-%d-%06d.log".formatted(dir, START_TIME, diff));

        final Request request = chain.request();
        final Response response = chain.proceed(request);

        final RequestResponseData requestResponseData = new RequestResponseData(response);
        final ResponseBody newResponseBody = createNewResponseBodyIfNeeded(response, requestResponseData.getResponseBody());

        YAML_PARSER.writeValue(logFile, requestResponseData);
        LOG.info("Request and response logged to '%s'".formatted(logFile.getAbsolutePath()));

        final Response newResponse = buildNewResponse(request, response, newResponseBody);
        return newResponse;
    }

    @Nullable
    private ResponseBody createNewResponseBodyIfNeeded(final Response response, final String responseBodyString) {
        if (responseBodyString == null) {
            return null;
        }

        final ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return null;
        }

        final Buffer buffer = new Buffer();
        buffer.writeString(responseBodyString, Charset.defaultCharset());

        return new ResponseBody() {

            @Nullable
            @Override
            public MediaType contentType() {
                return responseBody.contentType();
            }

            @Override
            public long contentLength() {
                return responseBody.contentLength();
            }

            @NotNull
            @Override
            public BufferedSource source() {
                return buffer;
            }
        };
    }

    @NotNull
    private Response buildNewResponse(final Request request, final Response response, final ResponseBody newResponseBody) {
        final Response newResponse = new Response.Builder()
                .request(request)
                .protocol(response.protocol())
                .message(response.message())
                .code(response.code())
                .handshake(response.handshake())
                .body(newResponseBody)
                .networkResponse(response.networkResponse())
                .cacheResponse(response.cacheResponse())
                .priorResponse(response.priorResponse())
                .sentRequestAtMillis(response.sentRequestAtMillis())
                .receivedResponseAtMillis(response.receivedResponseAtMillis())
                .build();
        return newResponse;
    }
}

