package net.jqwik.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.math.MathService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@JqwikMicronautTest
@Requires(property = "mockito.test.enabled", defaultValue = StringUtils.FALSE, value = StringUtils.TRUE)
class MathCollaboratorBaseTest extends MathBaseTest {
    @Inject
    private MathService mathService;

    @Inject
    @Client("/")
    private HttpClient client;

    @Property(tries = 1)
    void successOnGettingSquareNumber() {
        // given
        when(mathService.compute(10))
                .then(invocation -> Long.valueOf(Math.round(Math.pow(2, 2))).intValue());

        // when
        final Integer result = client.toBlocking()
                .retrieve(HttpRequest.GET("/math/compute/10"), Integer.class);

        // then
        assertThat(result).isEqualTo(4);
    }
}