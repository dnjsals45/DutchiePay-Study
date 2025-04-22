package dutchiepay.backend;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class kakaoPayTest {
    private WireMockServer wireMockServer;

    @Autowired
    @Qualifier("kakaoRestTemplate")
    private RestTemplate restTemplate;

    @BeforeAll
    void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        configureFor("localhost", 8089);
    }

    @AfterAll
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void testTimeoutScenario() {
        stubFor(post(urlEqualTo("/kakao-pay/approve"))
                .willReturn(aResponse()
                        .withFixedDelay(15000)
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\": \"APPROVED\"}")));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "SECRET_KEY test");

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        long startTime = System.currentTimeMillis();

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            restTemplate.postForEntity("http://localhost:8089/kakao-pay/approve", entity, String.class);
        });

        long elapsed = System.currentTimeMillis() - startTime;

        System.out.println("요청에 걸린 시간: " + elapsed + "ms");
        System.out.println("예외 클래스: " + thrown.getClass().getName());
        System.out.println("예외 메시지: " + thrown.getMessage());

        Throwable cause = thrown.getCause();
        while (cause != null) {
            System.out.println("원인: " + cause.getClass().getName() + " - " + cause.getMessage());
            cause = cause.getCause();
        }

        Assertions.assertTrue(thrown instanceof org.springframework.web.client.ResourceAccessException);
    }

    @Test
    void testConnectionTimeoutScenario() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "SECRET_KEY test");

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        long start = System.currentTimeMillis();

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            restTemplate.postForEntity("http://localhost:9999/kakao-pay/approve", entity, String.class);
        });

        long elapsed = System.currentTimeMillis() - start;

        System.out.println("연결 시도 시간: " + elapsed + "ms");
        System.out.println("예외 클래스: " + thrown.getClass().getName());
        System.out.println("예외 메시지: " + thrown.getMessage());

        Throwable cause = thrown.getCause();
        while (cause != null) {
            System.out.println("원인: " + cause.getClass().getName() + " - " + cause.getMessage());
            cause = cause.getCause();
        }

        Assertions.assertTrue(thrown instanceof org.springframework.web.client.ResourceAccessException);
    }
}
