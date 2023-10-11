package com.mbs.mclient.auto;


import com.mbs.mclient.annotation.Loggable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.mbs.mclient.aspect")
@ConditionalOnClass(Loggable.class)
public class LoggableAutoConfiguration {

    // ...
}