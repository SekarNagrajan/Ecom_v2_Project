package com.solverminds.booking.validation;

import javax.validation.ElementKind;
import javax.validation.Path;

/**
 * Converts a Bean Validation {@link Path} to dot/bracket notation (e.g. {@code containers[0].equipmentSize}),
 * suitable for JSON Pointer-style error locations.
 */
public final class BeanValidationPathFormatter {

    private BeanValidationPathFormatter() {
    }

    /**
     * Builds a property path string from a constraint violation path (e.g. for {@code #/} + path in V2 responses).
     */
    public static String toDotPath(Path path) {
        if (path == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Path.Node node : path) {
            ElementKind kind = node.getKind();
            if (kind == ElementKind.PROPERTY) {
                if (sb.length() > 0) {
                    sb.append('.');
                }
                sb.append(node.getName());
            } else if (kind == ElementKind.CONTAINER_ELEMENT) {
                Object index = node.getIndex();
                if (index != null) {
                    sb.append('[').append(index).append(']');
                }
            }
        }
        return sb.toString();
    }
}
