package com.sky1sbloo.ocjsys.runner;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.submission.CodeSubmission;
import com.sky1sbloo.ocjsys.exception.CodeRunnerException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CodeRunner {
    public String runCode(CodeSubmission submission) throws CodeRunnerException {
        return runCode(submission.getCode(), submission.getLanguage());
    }

    public String runCode(String code, CodeLanguage language)
            throws CodeRunnerException {
        checkDockerRunning();
        if (language != CodeLanguage.PYTHON) {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
        try {
            String submissionId = UUID.randomUUID().toString();
            Path submissionDir = Paths.get("/tmp/code_submission/" + submissionId);
            Files.createDirectories(submissionDir);
            Path codeFile = submissionDir.resolve("code_submission.py");
            Files.writeString(codeFile, code);
            Process process = getProcess(submissionDir);
            StringBuilder output = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                output.append("Execution timed out\n");
            }

            deleteDirectory(submissionDir);

            return output.toString();
        } catch (IOException e) {
            throw new CodeRunnerException(e);
        } catch (InterruptedException e) {
            throw new CodeRunnerException(e);
        }
    }

    private void checkDockerRunning() throws CodeRunnerException {
        try {
            Process process = new ProcessBuilder("docker", "info")
                    .redirectErrorStream(true)
                    .start();
            boolean finished = process.waitFor(3, TimeUnit.SECONDS);
            if (!finished || process.exitValue() != 0) {
                throw new CodeRunnerException(CodeRunnerException.Type.DOCKER_ERROR);
            }
        } catch (IOException e) {
            throw new CodeRunnerException(e);
        } catch (InterruptedException e) {
            throw new CodeRunnerException(e);
        }
    }

    private static @NonNull Process getProcess(Path submissionDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "docker",
                "run",
                "--rm",
                "--memory=128m",
                "--cpus=0.5",
                "--pids-limit=64",
                "--security-opt=no-new-privileges",
                "--cap-drop=ALL",
                "-v", submissionDir.toAbsolutePath() + ":/code",
                "python:3.14.3-slim-trixie",
                "python", "/code/code_submission.py"
        );
        pb.redirectErrorStream(true);
        return pb.start();
    }

    private void deleteDirectory(Path path) throws IOException {
        try (var paths = Files.walk(path)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }
}
