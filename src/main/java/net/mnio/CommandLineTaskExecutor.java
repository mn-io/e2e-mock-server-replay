package net.mnio;

import net.mnio.services.CallOtherApiService;
import okhttp3.Response;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Profile("!test")
@Component
public class CommandLineTaskExecutor implements CommandLineRunner {

    private final static Logger LOG = getLogger(CommandLineTaskExecutor.class);

    @Autowired
    private CallOtherApiService taskService;

    @Override
    public void run(String... args) throws Exception {
        final Response response = taskService.doGetRequest();

        LOG.info("Response from '%s' with code '%d' and body: '%s'"
                .formatted(response.request().url(), response.code(), response.body().string()));
    }
}