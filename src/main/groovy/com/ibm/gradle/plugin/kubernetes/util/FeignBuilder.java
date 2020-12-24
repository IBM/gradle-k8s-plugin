package com.ibm.gradle.plugin.kubernetes.util;

import feign.Feign;
import feign.Logger;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public class FeignBuilder {

    private static Feign.Builder build(){
        return Feign.builder();
    }

    public static Feign.Builder buildJson(){
        return  build()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
    }

    public static Feign.Builder buildJson(org.slf4j.Logger logger){
        return buildJson()
                .logger(new Slf4jFeignLogger(logger))
                .logLevel(Logger.Level.FULL);
    }

    public static Feign.Builder buildForm(){
        return build()
                .encoder(new FormEncoder())
                .decoder(new JacksonDecoder());
    }

    public static Feign.Builder buildForm(org.slf4j.Logger logger){
        return buildForm()
                .logger(new Slf4jFeignLogger(logger))
                .logLevel(Logger.Level.FULL);
    }

    public static Feign.Builder buildDownload(){
        return build()
                .encoder(new JacksonEncoder());
    }

    public static Feign.Builder buildDownload(org.slf4j.Logger logger){
        return buildDownload()
                .logger(new Slf4jFeignLogger(logger))
                .logLevel(Logger.Level.FULL);
    }

}
