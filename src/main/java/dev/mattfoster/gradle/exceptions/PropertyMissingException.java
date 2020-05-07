package dev.mattfoster.gradle.exceptions;

import org.gradle.api.GradleException;

public class PropertyMissingException extends GradleException {


    public PropertyMissingException(String format) {
        super(format);
    }
}
