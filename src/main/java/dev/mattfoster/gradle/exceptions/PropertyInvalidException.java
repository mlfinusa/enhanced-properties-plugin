package dev.mattfoster.gradle.exceptions;

import org.gradle.api.GradleException;

public class PropertyInvalidException extends GradleException {
    public PropertyInvalidException(String format) {
        super(format);
    }
}
