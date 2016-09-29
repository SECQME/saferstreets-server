package com.secqme.crimedata.domain.converter;

import java.sql.SQLException;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.geolatte.geom.ByteBuffer;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkb;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.codec.Wkt.Dialect;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;

/**
 * This are taken from https://github.com/realityforge/geolatte-geom-jpa/blob/master/src/main/java/org/realityforge/jeo/geolatte/jpa/PostgisConverter.java.
 * When this code was copied, the original code was only support PostgreSQL 9.1.
 * We use PostgreSQL 9.4 and there was a breaking changes.
 *
 * Created by edward on 02/11/2015.
 */
@Converter
public class PostgisConverter<T extends Geometry>
        implements AttributeConverter<T, Object> {

    @Override
    @SuppressWarnings("unchecked")
    public T convertToEntityAttribute(final Object dbData) {
        if (null == dbData) {
            return null;
        } else if (dbData instanceof PGgeometry) {
            final org.postgis.Geometry geometry = ((PGgeometry) dbData).getGeometry();
            return (T) Wkt.newDecoder(Dialect.POSTGIS_EWKT_1).decode(geometry.toString());
        } else if (dbData instanceof PGobject || dbData instanceof String) {
            /*
            In some circumstances the data will come back in WKB format (i.e. When using the Driver directly)
            and sometimes it will be returned in WKT format (i.e. In GlassFish when using the DataSource) and
            it is unclear what is causing the variance so support both scenarios.
            */
            final String wk = dbData.toString();
            final char ch = wk.charAt(0);
            if ('0' == ch) {
                // Guess that it is in WKB format
                return (T) Wkb.newDecoder(Wkb.Dialect.POSTGIS_EWKB_1).decode(ByteBuffer.from(wk));
            } else {
                // Assume a WKT format
                return (T) Wkt.newDecoder(Dialect.POSTGIS_EWKT_1).decode(wk);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Object convertToDatabaseColumn(final T attribute) {
        if (null == attribute) {
            final PGobject pgObject = new PGobject();
            pgObject.setType("geometry");
            return pgObject;
        }

        final String wkt = Wkt.newEncoder(Dialect.POSTGIS_EWKT_1).encode(attribute);
        try {
            return new PGgeometry(wkt);
        } catch (final SQLException se) {
            throw new IllegalStateException("Failed converting geometry", se);
        }
    }
}
