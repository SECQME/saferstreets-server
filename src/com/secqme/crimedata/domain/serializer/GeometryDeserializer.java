package com.secqme.crimedata.domain.serializer;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;

import java.io.IOException;

/**
 * Created by edward on 02/11/2015.
 */
public class GeometryDeserializer extends JsonDeserializer<Geometry> {

    @Override
    public Geometry deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        String wktFormatted = oc.readValue(jsonParser, String.class);

        return Wkt.newDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(wktFormatted);
    }
}
