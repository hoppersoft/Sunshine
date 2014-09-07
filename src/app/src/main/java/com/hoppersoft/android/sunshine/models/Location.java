package com.hoppersoft.android.sunshine.models;

import java.util.UUID;

/**
 * Created by hopperadmin on 9/7/2014.
 */
public class Location extends ModelBase {
    private String name;
    private String setting;
    private float longitude;
    private float latitude;

    private Location()
    {
        super();
    }

    private Location(UUID id, String name, String setting, Float longitude, Float latitude)
    {
        super();
        this.name = name;
        this.setting = setting;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static Location Create()
    {
        return new Location();
    }

    public static Location Create(UUID id, String name, String setting, Float longitude, Float latitude)
    {
        return new Location(id, name, setting, longitude, latitude);
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != Location.class) return false;
        Location other = (Location)o;
        return other.getUuid() == getUuid() &&
                other.getName() == getName() &&
                other.getSetting() == getSetting() &&
                other.getLatitude() == getLatitude() &&
                other.getLongitude() == getLongitude();
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }
}
