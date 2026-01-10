package ru.ztrixdev.projects.passhavenapp.pHbeKt;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Base32;
import java.security.InvalidKeyException;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.spec.SecretKeySpec;

public class TOTP {
    private static final int DEFAULT_TIME_STEP = 30;
    private static final int DEFAULT_PASSWORD_LEN = 6;
    private static final String DEFAULT_HMAC_ALGORITHM = TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA1;

    private String algorithm;
    private int timeStep;
    private int passwordLength;

    public TOTP(String algorithm, int timeStep, int passwordLength) {
        setAlgorithm(algorithm);
        setTimeStep(timeStep);
        setPasswordLength(passwordLength);
    }

    public TOTP() {
        setAlgorithm(DEFAULT_HMAC_ALGORITHM);
        setTimeStep(DEFAULT_TIME_STEP);
        setPasswordLength(DEFAULT_PASSWORD_LEN);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        if (!TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA1.equals(algorithm)
                && !TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA256.equals(algorithm)
                && !TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA512.equals(algorithm)) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        }

        this.algorithm = algorithm;
    }

    private String guessAlgorithm(byte[] keyBytes) {
        int len = keyBytes.length;
        if (len >= 64) {
            return TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA512;
        } else if (len >= 32) {
            return TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA256;
        } else {
            return TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA1;
        }
    }

    public int getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(int timeStep) {
        if (timeStep <= 0) {
            throw new IllegalArgumentException("timeStep must be > 0");
        }
        this.timeStep = timeStep;
    }

    public int getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(int passwordLength) {
        if (passwordLength <= 0) {
            throw new IllegalArgumentException("passwordLength must be > 0");
        }
        this.passwordLength = passwordLength;
    }

    private TimeBasedOneTimePasswordGenerator getGenerator() {
        return new TimeBasedOneTimePasswordGenerator(
                Duration.ofSeconds(this.timeStep),
                this.passwordLength,
                this.algorithm
        );
    }

    private byte[] getKeyBytes(String key) {
        return new Base32().decode(key);
    }

    private Key getJavaKey(String key) {
        byte[] keyBytes = getKeyBytes(key);
        String algorithm = guessAlgorithm(keyBytes);
        setAlgorithm(algorithm);

        return new SecretKeySpec(keyBytes, algorithm);
    }

    private static final IllegalArgumentException NullKeyException = new IllegalArgumentException("Cannot compute a TOTP for a null key");

    public String compute(String key) throws InvalidKeyException {
        if (key == null) throw NullKeyException;

        final Key jKey = getJavaKey(key);
        final TimeBasedOneTimePasswordGenerator generator = getGenerator();

        long otp = generator.generateOneTimePassword(jKey, Instant.now());

        return String.format("%0" + passwordLength + "d", otp);
    }

    public String computeAt(String key, Instant instant) throws InvalidKeyException {
        if (key == null) throw NullKeyException;

        final Key jKey = getJavaKey(key);
        final TimeBasedOneTimePasswordGenerator generator = getGenerator();

        long otp = generator.generateOneTimePassword(jKey, instant);

        return String.format("%0" + passwordLength + "d", otp);
    }
}
