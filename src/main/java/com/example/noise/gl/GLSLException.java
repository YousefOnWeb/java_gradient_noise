package com.example.noise.gl;

public class GLSLException extends RuntimeException {
    public GLSLException(String message) {
        super(message);
    }

    public GLSLException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public GLSLException(String message, String infoLog) {
        super(message + (infoLog != null && !infoLog.isEmpty() ? ":\n" + infoLog : ""));
    }
}