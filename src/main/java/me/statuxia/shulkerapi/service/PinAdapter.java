package me.statuxia.shulkerapi.service;

public interface PinAdapter {

    String encodePin(String rawPin);

    boolean pinMatches(String rawPin, String storedPin);
}
