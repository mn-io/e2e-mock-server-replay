package net.mnio.services;

import net.mnio.App;
import net.mnio.MockServerBuilder;
import okhttp3.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@SpringBootTest(
        classes = App.class,
        properties = {"spring.profiles.active=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class CallOtherApiServiceTest {

    private static final String RESPONSE_BODY_EXPECTED = "[{\"id\":3626,\"name\":\"Digambara Ahuja\",\"email\":\"digambara_ahuja@monahan.com\",\"gender\":\"male\",\"status\":\"inactive\"},{\"id\":3625,\"name\":\"Smriti Sharma Esq.\",\"email\":\"smriti_sharma_esq@wehner-homenick.com\",\"gender\":\"female\",\"status\":\"inactive\"},{\"id\":3624,\"name\":\"Vedanga Ganaka PhD\",\"email\":\"phd_ganaka_vedanga@medhurst.name\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":3623,\"name\":\"Durga Somayaji\",\"email\":\"durga_somayaji@rippin.io\",\"gender\":\"female\",\"status\":\"active\"},{\"id\":3622,\"name\":\"Chandraprabha Varrier\",\"email\":\"varrier_chandraprabha@veum-bosco.org\",\"gender\":\"female\",\"status\":\"inactive\"},{\"id\":3621,\"name\":\"Kumuda Dutta\",\"email\":\"kumuda_dutta@cassin.org\",\"gender\":\"female\",\"status\":\"inactive\"},{\"id\":3620,\"name\":\"Vaishvi Patil\",\"email\":\"patil_vaishvi@lubowitz.biz\",\"gender\":\"male\",\"status\":\"inactive\"},{\"id\":3619,\"name\":\"Jay Gupta Jr.\",\"email\":\"jay_jr_gupta@upton.info\",\"gender\":\"male\",\"status\":\"inactive\"},{\"id\":3618,\"name\":\"Vaijayanti Kapoor\",\"email\":\"kapoor_vaijayanti@corkery-dietrich.name\",\"gender\":\"female\",\"status\":\"active\"},{\"id\":3617,\"name\":\"Rep. Devani Deshpande\",\"email\":\"rep_deshpande_devani@parker.name\",\"gender\":\"female\",\"status\":\"inactive\"},{\"id\":3616,\"name\":\"Girija Saini\",\"email\":\"girija_saini@schulist-ziemann.net\",\"gender\":\"male\",\"status\":\"inactive\"},{\"id\":3615,\"name\":\"Narinder Kocchar\",\"email\":\"kocchar_narinder@gibson.com\",\"gender\":\"female\",\"status\":\"inactive\"},{\"id\":3614,\"name\":\"Sen. Dhanpati Nair\",\"email\":\"nair_dhanpati_sen@gerlach-powlowski.biz\",\"gender\":\"female\",\"status\":\"inactive\"},{\"id\":3613,\"name\":\"Aamod Naik IV\",\"email\":\"aamod_naik_iv@bednar-wilkinson.biz\",\"gender\":\"female\",\"status\":\"inactive\"},{\"id\":3611,\"name\":\"Shobhana Tandon\",\"email\":\"tandon_shobhana@stamm.info\",\"gender\":\"female\",\"status\":\"inactive\"},{\"id\":3610,\"name\":\"Mangalya Pilla\",\"email\":\"mangalya_pilla@kulas-lesch.name\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":3609,\"name\":\"Dayaamay Saini\",\"email\":\"saini_dayaamay@rau.info\",\"gender\":\"male\",\"status\":\"inactive\"},{\"id\":3608,\"name\":\"Msgr. Jagadish Deshpande\",\"email\":\"deshpande_jagadish_msgr@strosin.net\",\"gender\":\"female\",\"status\":\"active\"},{\"id\":3607,\"name\":\"Kashyap Devar\",\"email\":\"devar_kashyap@kub.net\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":3606,\"name\":\"Rev. Anilabh Guneta\",\"email\":\"anilabh_rev_guneta@mertz.info\",\"gender\":\"female\",\"status\":\"inactive\"}]";

    @Value("${test.mockServer.port}")
    private int mockServerPort;

    @Value("${test.mockServer.dir}")
    private String requestResponseLogDir;

    @Value("${app.restApiUrl}")
    private String restApiUrl;

    private ClientAndServer mockServer;

    @Autowired
    private CallOtherApiService callOtherApiService;

    @Test
    void test() throws IOException, URISyntaxException {
        mockServer = MockServerBuilder.start(mockServerPort, requestResponseLogDir);
        final Response response = callOtherApiService.doGetRequest();

        assertTrue(restApiUrl.startsWith("http://localhost"), "http request should against localhost");
        assertEquals(restApiUrl, response.request().url().toString(), "http request should against our mock server");

        assertEquals(200, response.code(), "http response code should be 200");
        assertEquals(RESPONSE_BODY_EXPECTED, response.body().string(), "http response body should match");
    }

    @AfterEach
    public void afterEach() {
        if (mockServer != null) {
            mockServer.stop();
            mockServer = null;
        }
    }
}