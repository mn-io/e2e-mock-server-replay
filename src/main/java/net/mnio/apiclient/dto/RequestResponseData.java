package net.mnio.apiclient.dto;

import kotlin.Pair;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;
import java.util.HashMap;

public class RequestResponseData {

    private String requestUrl = "";
    private String requestMethod = "";
    private HashMap<String, String> requestHeaders = new HashMap<>();
    private String requestBody = "";

    private int responseCode = 0;
    private HashMap<String, String> responseHeaders = new HashMap<>();
    private String responseBody = "";

    public RequestResponseData() {
    }

    public RequestResponseData(final Response response) throws IOException {
        final Request request = response.request();
        requestUrl = request.url().toString();
        requestMethod = request.method();
        for (Pair<? extends String, ? extends String> header : request.headers()) {
            requestHeaders.put(header.getFirst(), header.getSecond());
        }
        requestBody = bodyToString(request);

        responseCode = response.code();
        for (Pair<? extends String, ? extends String> header : response.headers()) {
            responseHeaders.put(header.getFirst(), header.getSecond());
        }
        responseBody = response.body() == null ? null : response.body().string();
    }

    private static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            final RequestBody body = copy.body();
            if (body == null) {
                return "";
            }
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "could not be read";
        }
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public HashMap<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public HashMap<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
