package com.secqme.crimedata.domain.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Edmund on 3/30/15.
 */
@Entity
@Table(name = "crime_types")
@NamedQueries({
        @NamedQuery(name=CrimeTypeVO.QUERY_FIND_ALL,
                query = "SELECT o " +
                        "FROM CrimeTypeVO o ")
})
public class CrimeTypeVO implements Serializable {

    public static final String QUERY_FIND_ALL = "crimeTypeVO.findAll";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "crime_type_id_seq")
    @SequenceGenerator(
            name = "crime_type_id_seq",
            sequenceName = "crime_type_id_seq",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "crime_weight")
    private Double crimeWeight;

    @Column(name = "description")
    private String description;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "violent")
    private boolean violent;

    @ManyToOne
    @JoinColumn(name = "subtype_of")
    private CrimeTypeVO subtypeOf;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public CrimeTypeVO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String crimeType) {
        this.name = crimeType;
    }

    public Double getCrimeWeight() {
        return crimeWeight;
    }

    public void setCrimeWeight(Double crimeWeight) {
        this.crimeWeight = crimeWeight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isViolent() {
        return violent;
    }

    public void setViolent(boolean violent) {
        this.violent = violent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CrimeTypeVO getSubtypeOf() {
        return subtypeOf;
    }

    public void setSubtypeOf(CrimeTypeVO subtypeOf) {
        this.subtypeOf = subtypeOf;
    }
}
