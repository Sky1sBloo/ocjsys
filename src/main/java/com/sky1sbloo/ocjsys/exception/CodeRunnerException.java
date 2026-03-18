package com.sky1sbloo.ocjsys.exception;

import com.sky1sbloo.ocjsys.runner.CodeRunner;
import lombok.Getter;

import java.io.IOException;

public class CodeRunnerException extends RuntimeException {
    public enum Type {
        IO_EXCEPTION,
        INTERRUPTED,
        UNSUPPORTED_LANGUAGE,
        DOCKER_ERROR,
        UNKNOWN
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
