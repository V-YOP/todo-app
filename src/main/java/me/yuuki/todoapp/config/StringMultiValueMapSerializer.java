package me.yuuki.todoapp.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("all")
public class StringMultiValueMapSerializer extends JsonSerializer<MultiValueMap<String, String>> {
    @Override
    public void serialize(MultiValueMap<String, String> multiValueMap, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            String key = entry.getKey();
            List<String> values =
                    Optional.ofNullable(entry.getValue())
                            .orElse(Collections.emptyList());

            jsonGenerator.writeArrayFieldStart(key);
            for (String value : values) {
                jsonGenerator.writeString(value);
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
    }
}
