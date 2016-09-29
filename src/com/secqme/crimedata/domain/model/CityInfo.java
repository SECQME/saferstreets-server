package com.secqme.crimedata.domain.model;

import com.secqme.crimedata.domain.converter.PostgisConverter;
import com.secqme.crimedata.domain.serializer.GeometryDeserializer;
import com.secqme.crimedata.domain.serializer.GeometrySerializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: James Khoo
 * Date: 10/9/14
 * Time: 4:42 PM
 */
@Entity
@Table(name = "cities")
@NamedQueries({
        @NamedQuery(name=CityInfo.QUERY_FIND_ALL,
                query = "SELECT o " +
                        "FROM CityInfo o"
        ),
        @NamedQuery(name=CityInfo.QUERY_FIND_BY_CITY,
                query = "SELECT o " +
                        "FROM CityInfo o " +
                        "WHERE o.name = :city"
        ),
        @NamedQuery(name=CityInfo.QUERY_FIND_BY_COUNTRY_STATE_CITY,
                query = "SELECT o " +
                        "FROM CityInfo o " +
                        "WHERE o.country = :country AND o.state = :state AND o.name = :city"
        ),
        @NamedQuery(name=CityInfo.QUERY_FIND_BY_LOCATION,
                query = "SELECT o " +
                        "FROM CityInfo  o " +
                        "WHERE FUNCTION('ST_CoveredBy', FUNCTION('ST_SetSRID', FUNCTION('ST_MakePoint', :longitude, :latitude), 4326), o.area)"
        )
})
public class CityInfo implements Serializable {

    public static final String QUERY_FIND_ALL = "cityInfo.findAll";
    public static final String QUERY_FIND_BY_CITY = "cityInfo.findByCity";
    public static final String QUERY_FIND_BY_COUNTRY_STATE_CITY = "cityInfo.findByCountryStateCity";
    public static final String QUERY_FIND_BY_LOCATION = "cityInfo.findByLocation";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cities_id_seq")
    @SequenceGenerator(
            name = "cities_id_seq",
            sequenceName = "cities_id_seq",
            allocationSize = 1)
    @Column(name = "id", updatable=false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "city_time_zone")
    private String cityTimeZone;

    @Column(name = "crime_day_time_report")
    private boolean crimeDayTimeReport;

    @Column(name = "neighbour")
    private String neighbour;

    @Column(name = "south_west")
    @Convert(converter = PostgisConverter.class)
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Point southWest;

    @Column(name = "north_east")
    @Convert(converter = PostgisConverter.class)
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Point northEast;

    @Column(name = "area")
    @Convert(converter = PostgisConverter.class)
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry area;

    public CityInfo() {
        // Empty Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String city) {
        this.name = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @JsonIgnore
    public Double getLowerLeftLat() {
        return southWest.getY();
    }

    @JsonIgnore
    public Double getLowerLeftLng() {
        return southWest.getX();
    }

    @JsonIgnore
    public Double getUpperRightLat() {
        return northEast.getY();
    }

    @JsonIgnore
    public Double getUpperRightLng() {
        return northEast.getX();
    }

    public String getNeighbour() {
        return neighbour;
    }

    public void setNeighbour(String neighbour) {
        this.neighbour = neighbour;
    }

    public String getCityTimeZone() {
        return cityTimeZone;
    }

    public void setCityTimeZone(String cityTimeZone) {
        this.cityTimeZone = cityTimeZone;
    }

    public boolean isCrimeDayTimeReport() {
        return crimeDayTimeReport;
    }

    public void setCrimeDayTimeReport(boolean crimeDayTimeReport) {
        this.crimeDayTimeReport = crimeDayTimeReport;
    }

    public Point getSouthWest() {
        return southWest;
    }

    public void setSouthWest(Point southWest) {
        this.southWest = southWest;
    }

    public Point getNorthEast() {
        return northEast;
    }

    public void setNorthEast(Point northEast) {
        this.northEast = northEast;
    }

    public Geometry getArea() {
        return area;
    }

    public void setArea(Geometry boundary) {
        this.area = boundary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CityInfo cityInfo = (CityInfo) o;

        if (crimeDayTimeReport != cityInfo.crimeDayTimeReport) return false;
        if (id != null ? !id.equals(cityInfo.id) : cityInfo.id != null) return false;
        if (!name.equals(cityInfo.name)) return false;
        if (!state.equals(cityInfo.state)) return false;
        if (!country.equals(cityInfo.country)) return false;
        if (cityTimeZone != null ? !cityTimeZone.equals(cityInfo.cityTimeZone) : cityInfo.cityTimeZone != null)
            return false;
        if (neighbour != null ? !neighbour.equals(cityInfo.neighbour) : cityInfo.neighbour != null) return false;
        if (!southWest.equals(cityInfo.southWest)) return false;
        if (!northEast.equals(cityInfo.northEast)) return false;
        return area.equals(cityInfo.area);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + country.hashCode();
        result = 31 * result + (cityTimeZone != null ? cityTimeZone.hashCode() : 0);
        result = 31 * result + (crimeDayTimeReport ? 1 : 0);
        result = 31 * result + (neighbour != null ? neighbour.hashCode() : 0);
        result = 31 * result + southWest.hashCode();
        result = 31 * result + northEast.hashCode();
        result = 31 * result + area.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CityInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
