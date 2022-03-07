package org.netcracker.eventteammatessearch.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class JacksonSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static boolean isInit = false;

    private JacksonSerializer() {

    }

    private static void init() {
        if (isInit == false) {
            objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY);
            objectMapper.disable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setDateFormat(new ISO8601DateFormat());


            isInit = true;
        }
    }
}