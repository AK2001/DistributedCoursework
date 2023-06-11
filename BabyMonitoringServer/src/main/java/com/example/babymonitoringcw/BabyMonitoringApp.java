package com.example.babymonitoringcw;

import com.example.babymonitoringcw.Cors.CorsFilter;
import com.example.babymonitoringcw.resources.FeedingRecordsResource;
import com.example.babymonitoringcw.security.AuthenticationFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class BabyMonitoringApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CorsFilter.class);
        classes.add(FeedingRecordsResource.class);
        classes.add(AuthenticationFilter.class);
        return classes;
    }
}