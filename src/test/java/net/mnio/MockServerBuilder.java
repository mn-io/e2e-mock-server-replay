package net.mnio;

import net.mnio.apiclient.dto.RequestResponseData;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.mnio.Factory.YAML_PARSER;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.slf4j.LoggerFactory.getLogger;

public class MockServerBuilder {

    private final static Logger LOG = getLogger(MockServerBuilder.class);

    public static ClientAndServer start(final int port, final String replayDir) throws IOException, URISyntaxException {
        LOG.info("Starting mock server listening on http://localhost: " + port);
        final ClientAndServer clientAndServer = startClientAndServer(port);

        final File root = new File(replayDir);
        LOG.info("Reading all files in dir for replay: %s".formatted(root.getAbsolutePath()));
        final Iterator<File> fileIterator = FileUtils.iterateFiles(root, new String[]{"log"}, false);

        while (fileIterator.hasNext()) {
            File next = fileIterator.next();
            LOG.info("Read file for replay: %s".formatted(next.getPath()));
            final RequestResponseData requestResponseData = YAML_PARSER.readValue(next, RequestResponseData.class);
            attachMockServerClient(port, requestResponseData);
        }

        return clientAndServer;
    }

    private static void attachMockServerClient(final int port, final RequestResponseData data) throws URISyntaxException, MalformedURLException {
        final String urlPathOnly = new URL(data.getRequestUrl()).getPath();
        final HttpRequest httpRequest = request()
                .withMethod(data.getRequestMethod())
                .withPath(urlPathOnly)
//                headers don't need to match - e.g. data would never match and should be excluded
//                .withHeaders(createList(data.getRequestHeaders()))
                .withBody(data.getRequestBody());

        List<NameValuePair> params = URLEncodedUtils.parse(new URI(data.getRequestUrl()), StandardCharsets.UTF_8);
        for (NameValuePair param : params) {
            httpRequest.withQueryStringParameter(Parameter.param(param.getName(), param.getValue()));
        }

        final int responseCode = data.getResponseCode();
        final HttpResponse httpResponse = response()
                .withStatusCode(responseCode)
//                headers don't need to match - e.g. data would never match and should be excluded
//                .withHeaders(createList(data.getResponseHeaders()))
                .withBody(data.getResponseBody());

        new MockServerClient("localhost", port)
                .when(httpRequest, Times.once())
                .respond(httpResponse);
    }

    private static List<Header> createList(final Map<String, String> headers) {
        final ArrayList<Header> list = new ArrayList<>();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            final Header h = new Header(header.getKey(), header.getValue());
            list.add(h);
        }
        return list;
    }
}
