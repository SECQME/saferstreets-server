package com.secqme.crimedata.domain.model;

import com.secqme.crimedata.domain.serializer.GeometryDeserializer;
import com.secqme.crimedata.domain.serializer.GeometrySerializer;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.geolatte.geom.Point;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import com.secqme.crimedata.domain.converter.PostgisConverter;

/**
 * User: James Khoo
 * Date: 8/13/14
 * Time: 5:49 PM
 */
@Entity
@Table(name = "crime_data")
@NamedQueries({
        @NamedQuery(name=CrimeDataVO.QUERY_FIND_LATEST_CRIME_BY_CITY_WITH_START_DATE,
                query = "SELECT o " +
                        "FROM CrimeDataVO o " +
                        "WHERE o.occurredAt > :startDate " +
                        "AND o.city.name = :city " +
                        "ORDER BY o.occurredAt DESC"
        ),
        @NamedQuery(name=CrimeDataVO.QUERY_FIND_LATEST_CRIME_BY_CITY_WITH_START_DATE_AND_END_DATE,
                query = "SELECT o " +
                        "FROM CrimeDataVO o " +
                        "WHERE o.occurredAt > :startDate " +
                        "AND o.occurredAt <= :endDate " +
                        "AND o.city.name = :city "  +
                        "ORDER BY o.occurredAt DESC"
        ),
        @NamedQuery(name=CrimeDataVO.QUERY_FIND_LATEST_CRIME_BY_CRIME_WEIGHT,
                query = "SELECT o " +
                        "FROM CrimeDataVO o "+
                        "WHERE o.crimeWeight IS NULL"
        ),
        @NamedQuery(name=CrimeDataVO.QUERY_FIND_ALL_BY_BATCH,
                query = "SELECT o "+
                        "FROM CrimeDataVO o " +
                        "WHERE o.id BETWEEN :startId AND :endId"
        ),
        @NamedQuery(name=CrimeDataVO.QUERY_FIND_ALL,
                query = "SELECT o "+
                        "FROM CrimeDataVO o"
        )
})
public class CrimeDataVO implements Serializable {
    public static final String QUERY_FIND_LATEST_CRIME_BY_CITY_WITH_START_DATE = "crimeDataVO.findLatestCrimeByCityWithStartDate";
    public static final String QUERY_FIND_LATEST_CRIME_BY_CITY_WITH_START_DATE_AND_END_DATE = "crimeDataVO.findLatestCrimeByCityWithStartDateAndEndDate";
    public static final String QUERY_FIND_LATEST_CRIME_BY_CRIME_WEIGHT = "crimeDataVO.findLatestCrimeByCrimeWeight";
    public static final String QUERY_FIND_ALL_BY_BATCH = "crimeDataVO.findAllByBatch";
    public static final String QUERY_FIND_ALL = "crimeDataVO.findAll";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "crime_data_id_seq")
    @SequenceGenerator(
            name = "crime_data_id_seq",
            sequenceName = "crime_data_id_seq",
            allocationSize = 1)
    @Column(name = "id", updatable=false)
    private Long id;

    @Column(name = "crime_case_id")
    private String crimeCaseId;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityInfo city;

    @ManyToOne
    @JoinColumn(name="crime_type_id")
    private CrimeTypeVO crimeTypeVO;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "occurred_at")
    private Date occurredAt;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "note")
    private String note;

    @Column(name = "location")
    @Convert(converter = PostgisConverter.class)
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Point location;

    @Column(name = "accuracy")
    private Double accuracy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reported_at")
    private Date reportedAt;

    @Column(name = "ucr")
    private String ucr;

    @Column(name = "domestic")
    private Boolean domestic;

    @Column(name = "arrested")
    private Boolean arrested;

    @Column(name = "crime_weight")
    private Double crimeWeight;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name="crime_day_time")
    private CrimeDayTime crimeDayTime;

    @Column(name = "source")
    private String source;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "crime_picture_url")
    private String crimePictureURL;

    @Column(name = "crime_video_url")
    private String crimeVideoURL;

    @Column(name = "address")
    private String address;

    @Column(name = "beat")
    private String beat;

    @Column(name = "block")
    private String block;

    @Column(name = "ward")
    private String ward;

    @Column(name = "community_area")
    private String communityArea;

    @Column(name = "district")
    private String district;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "location_description")
    private String locationDescription;

    @Column(name = "description")
    private String description;

    public CrimeDataVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrimeCaseID() {
        return crimeCaseId;
    }

    public void setCrimeCaseID(String crimeCaseId) {
        this.crimeCaseId = crimeCaseId;
    }

    public CityInfo getCity() {
        return city;
    }

    public void setCity(CityInfo city) {
        this.city = city;
    }

    public CrimeTypeVO getCrimeTypeVO() {
        return crimeTypeVO;
    }

    public void setCrimeTypeVO(CrimeTypeVO crimeTypeVO) {
        this.crimeTypeVO = crimeTypeVO;
    }

    public Date getCrimeDate() {
        return occurredAt;
    }

    public void setCrimeDate(Date occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @JsonIgnore
    public Double getLatitude() {
        return location.getY();
    }

    @JsonIgnore
    public Double getLongitude() {
        return location.getX();
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Date getReportDate() {
        return reportedAt;
    }

    public void setReportDate(Date reportedAt) {
        this.reportedAt = reportedAt;
    }

    public String getUcr() {
        return ucr;
    }

    public void setUcr(String ucr) {
        this.ucr = ucr;
    }

    public Boolean getDomestic() {
        return domestic;
    }

    public void setDomestic(Boolean domestic) {
        this.domestic = domestic;
    }

    public Boolean getArrested() {
        return arrested;
    }

    public void setArrested(Boolean arrested) {
        this.arrested = arrested;
    }

    public Double getCrimeWeight() {
        return crimeWeight;
    }

    public void setCrimeWeight(Double crimeWeight) {
        this.crimeWeight = crimeWeight;
    }

    public CrimeDayTime getCrimeDayTime() {
        return crimeDayTime;
    }

    public void setCrimeDayTime(CrimeDayTime crimeDayTime) {
        this.crimeDayTime = crimeDayTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getCrimePictureURL() {
        return crimePictureURL;
    }

    public void setCrimePictureURL(String crimePictureURL) {
        this.crimePictureURL = crimePictureURL;
    }

    public String getCrimeVideoURL() {
        return crimeVideoURL;
    }

    public void setCrimeVideoURL(String crimeVideoURL) {
        this.crimeVideoURL = crimeVideoURL;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBeat() {
        return beat;
    }

    public void setBeat(String beat) {
        this.beat = beat;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getCommunityArea() {
        return communityArea;
    }

    public void setCommunityArea(String communityArea) {
        this.communityArea = communityArea;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
