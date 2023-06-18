package net.jqwik.micronaut;

import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest(propertySources = "myprops.properties")
class PropertySourceTest {
    @io.micronaut.context.annotation.Property(name = "foo.bar")
    private String val;

    @Property(tries = 1)
    void testPropertySource() {
        assertThat(val).isEqualTo("foo");
    }
}