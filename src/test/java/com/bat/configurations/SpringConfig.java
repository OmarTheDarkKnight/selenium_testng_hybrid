package com.bat.configurations;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(basePackages = "com.bat")
@PropertySources({
        @PropertySource(value = "classpath:browser_config.properties"),
        @PropertySource(value = "classpath:selenium.properties")
})
public class SpringConfig {
}
