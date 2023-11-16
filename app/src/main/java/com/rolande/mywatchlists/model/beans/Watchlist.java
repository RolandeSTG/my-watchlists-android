package com.rolande.mywatchlists.model.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
/**
 * A class/bean to define a watchlist.  In its simplest form, a watchlist is really just a
 * name associated with a list of security symbols...
 *
 * @author Rolande
 */

public class Watchlist implements Serializable {
    @SerializedName("_id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("date_created")
    private Date dateCreated;

    @SerializedName("number_of_securities")
    private int numberOfSecurities;

    public Watchlist() {
    }

    public Watchlist(Long id, String name, Date dateCreated, int numberOfSecurities) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.numberOfSecurities = numberOfSecurities;
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

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getNumberOfSecurities() {
        return numberOfSecurities;
    }

    public void setNumberOfSecurities(int numberOfSecurities) {
        this.numberOfSecurities = numberOfSecurities;
    }

    @Override
    public String toString() {
        return "Watchlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateCreated=" + dateCreated +
                ", numberOfSecurities=" + numberOfSecurities +
                '}';
    }
}

