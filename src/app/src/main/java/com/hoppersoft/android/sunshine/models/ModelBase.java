package com.hoppersoft.android.sunshine.models;

import java.util.UUID;

/**
 * Created by hopperadmin on 9/7/2014.
 */
public class ModelBase {
    private UUID _id;

    public ModelBase()
    {
        _id = UUID.randomUUID();
    }

    public ModelBase(UUID uuid)
    {
        _id = uuid;
    }

    public UUID getUuid()
    {
        return _id;
    }
    public void setUuid(UUID value)
    {
        _id = value;
    }
}
