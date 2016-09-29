package com.secqme.crimedata.domain.serializer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;

import java.io.IOException;

/**
 * Created by edward on 02/11/2015.
 */
public class GeometrySerializer extends JsonSerializer<Geometry> {

    @Override
    public void serialize(Geometry t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        String wktFormatted = Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(t);
        jsonGenerator.writeString(wktFormatted);
    }
}
