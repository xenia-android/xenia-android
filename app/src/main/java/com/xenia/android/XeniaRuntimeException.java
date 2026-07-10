package com.xenia.android;

/**
 * Base class for all unchecked exceptions thrown by Xenia components.
 * Mirrors jp.xenia.XeniaRuntimeException from the original upstream.
 */
public class XeniaRuntimeException extends RuntimeException {

    public XeniaRuntimeException() {
        super();
    }

    public XeniaRuntimeException(final String message) {
        super(message);
    }

    public XeniaRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public XeniaRuntimeException(final Throwable cause) {
        super(cause);
    }
}
