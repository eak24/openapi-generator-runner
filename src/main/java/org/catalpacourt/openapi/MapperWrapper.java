package org.catalpacourt.openapi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MapperWrapper {

    private static final ObjectMapper jsonMapper = configure(new ObjectMapper());
    private static final ObjectMapper yamlMapper = configure(new ObjectMapper(new YAMLFactory()));

    private static ObjectMapper configure(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ProcessBuilder.class, new StdSerializer<ProcessBuilder>(ProcessBuilder.class) {
            @Override
            public void serialize(ProcessBuilder processBuilder, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
                jgen.writeStartObject();

                jgen.writeFieldName("command");
                jgen.writeArray(processBuilder.command().toArray(new String[]{}), 0, processBuilder.command().size());
                File directory = processBuilder.directory();
                if (directory != null) {
                    jgen.writeStringField("directory", processBuilder.directory().getAbsolutePath());
                }
                jgen.writeEndObject();
            }
        });
        mapper.registerModule(module);
        return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getMapper() {
        return jsonMapper;
    }

    public static ObjectMapper getMapper(String path) {
        String[] parts = path.split("\\.");
        String pathExtension = parts[parts.length - 1];
        if (Objects.equals(pathExtension, "yaml")) {
            return yamlMapper;
        }
        return jsonMapper;
    }
}
