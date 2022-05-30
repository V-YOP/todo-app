package me.yuuki.todoapp.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class StringMultiValueMapDeserializer extends JsonDeserializer<MultiValueMap<String, String>> {
    @Override
    public MultiValueMap<String, String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        Map<String, List<String>> map = deserializationContext.readValue(jsonParser, Map.class);
        return new MultiValueMapAdapter<>(map);
    }
}
