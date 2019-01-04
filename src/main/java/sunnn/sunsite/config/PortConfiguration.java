package sunnn.sunsite.config;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import sunnn.sunsite.util.SunSiteProperties;

/**
 * https://docs.spring.io/spring-boot/docs/2.0.7.RELEASE/reference/htmlsingle/#boot-features-programmatic-embedded-container-customization
 */
@Component
@DependsOn("sunSiteProperties")
//@Order
public class PortConfiguration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.setPort(SunSiteProperties.port);
    }
}
