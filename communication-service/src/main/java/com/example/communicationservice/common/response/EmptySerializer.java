package com.example.communicationservice.common.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class EmptySerializer extends StdSerializer<Empty> {

    public EmptySerializer() {
        super(Empty.class);
    }

    @Override
    public void serialize(
        Empty value,
        JsonGenerator generator,
        SerializerProvider provider
    ) throws IOException {
        generator.writeStartObject(); // {
        generator.writeEndObject(); // }
    }

}
