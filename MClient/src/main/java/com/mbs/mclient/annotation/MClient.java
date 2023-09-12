package com.mbs.mclient.annotation;

import com.mbs.mclient.core.MClientAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MClientAutoConfiguration.class)
public @interface MClient {
}
