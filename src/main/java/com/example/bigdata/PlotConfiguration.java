package com.example.bigdata;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.function.Function;

@Configuration
public class PlotConfiguration {

    @Value(value = "classpath:plot.R")
    private Resource rSource;

    @Bean
    public Context getGraalVMContext() {
        return Context.newBuilder().allowAllAccess(true).build();
    }

    @Bean
    public Function<DataHolder, String> plotFunction(Context ctx) throws IOException {
        Source source = Source.newBuilder("R", rSource.getURL()).build();
        return ctx.eval(source).as(Function.class);
    }
}
