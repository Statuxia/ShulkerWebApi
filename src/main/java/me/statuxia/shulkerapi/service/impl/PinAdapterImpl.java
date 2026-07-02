package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.service.PinAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PinAdapterImpl implements PinAdapter {

    private static final String ENCRYPTED_MODE = "encrypted";

    @Value("${pin.security.mode:plain}")
    private String pinSecurityMode;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public PinAdapterImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encodePin(String rawPin) {
        return isEncrypted() ? passwordEncoder.encode(rawPin) : rawPin;
    }

    @Override
    public boolean pinMatches(String rawPin, String storedPin) {
        return isEncrypted() ? passwordEncoder.matches(rawPin, storedPin) : storedPin.equals(rawPin);
    }

    private boolean isEncrypted() {
        return ENCRYPTED_MODE.equalsIgnoreCase(pinSecurityMode);
    }
}
