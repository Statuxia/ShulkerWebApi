package me.statuxia.shulkerapi.model;

import org.joda.time.DateTime;

public interface DisableAware {

    Boolean isDisabled();

    void setDisabled(Boolean disabled);

    DateTime getDisabledTime();

    void setDisabledTime(DateTime dateTime);
}
