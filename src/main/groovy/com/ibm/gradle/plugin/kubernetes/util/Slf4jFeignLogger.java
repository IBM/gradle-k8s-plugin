package com.ibm.gradle.plugin.kubernetes.util;

import feign.Request;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Slf4jFeignLogger extends feign.Logger {

    private final Logger logger;

    public Slf4jFeignLogger() {
        this(feign.Logger.class);
    }

    public Slf4jFeignLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public Slf4jFeignLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    public Slf4jFeignLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (logger.isDebugEnabled()) {
            super.logRequest(configKey, logLevel, request);
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey,
                                              Level logLevel,
                                              Response response,
                                              long elapsedTime)
            throws IOException {
        if (logger.isDebugEnabled()) {
            return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
        }
        return response;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        // Not using SLF4J's support for parameterized messages (even though it would be more efficient)
        // because it would
        // require the incoming message formats to be SLF4J-specific.
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(methodTag(configKey) + format, args));
        }
    }

}
