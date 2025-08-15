package me.statuxia.shulkerapi.model;

import org.joda.time.DateTime;

public interface DisableAware {

    boolean isDisabled();

    void setDisabled(boolean disabled);

    DateTime getDisabledTime();

    void setDisabledTime(DateTime dateTime);
}
