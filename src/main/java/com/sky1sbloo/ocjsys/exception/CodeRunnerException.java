package com.sky1sbloo.ocjsys.exception;

import lombok.Getter;

import java.io.IOException;

public class CodeRunnerException extends RuntimeException {
    public enum Type {
        IO_EXCEPTION("IO Exception"),
        INTERRUPTED("Process interrupted"),
        UNSUPPORTED_LANGUAGE("Unsupported language"),
        DOCKER_ERROR("Docker might not be running"),
        EXECUTION_FAILED("Code execution failed"),
        EXECUTION_TIMED_OUT("Code execution timed out"),
        UNKNOWN("Unknown");

        public final String label;
        Type(String label) {
            this.label = label;
        }
    }
    @Getter
    private final Type type;

    public CodeRunnerException(String message) {
        super(message);
        type = Type.UNKNOWN;
    }

    public CodeRunnerException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public CodeRunnerException(Type type) {
        super(type.label);
        this.type = type;
    }

    public CodeRunnerException(InterruptedException ex) {
        super(ex);
        type = Type.INTERRUPTED;
    }

    public CodeRunnerException(IOException ex) {
        super(ex);
        type = Type.IO_EXCEPTION;
    }

    public CodeRunnerException(String message, Throwable cause) {
        super(message, cause);
        type = Type.UNKNOWN;
    }
}
