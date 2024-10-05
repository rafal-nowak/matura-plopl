package pl.lodz.p.liceum.matura.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Log
@Component
@Profile({"dev", "prod"}) // Wykonuje się zarówno w profilach 'dev' jak i 'prod'
@RequiredArgsConstructor
public class CommandInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("Starting Command Initializer...");

//        log.info(executeStartupCommand("dockerd &"));
        log.info(executeStartupCommand("dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock &"));

        log.info("Docker Version: " + executeStartupCommand("docker --version"));
        log.info("Docker Compose Version: " + executeStartupCommand("docker-compose --version"));
    }

    private String executeStartupCommand(String command) {
        log.info("Executing command: " + command);
        return executeCommand(command);
    }

    private String executeCommand(@RequestParam String command) {
        StringBuilder result = new StringBuilder();
        try {
            log.info("Executing command using ProcessBuilder: " + command);
            // Użycie ProcessBuilder zamiast exec
            ProcessBuilder builder = new ProcessBuilder("sh", "-c", command);
            builder.redirectErrorStream(true);  // Przekierowanie strumienia błędów do standardowego wyjścia
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            return "Command execution failed: " + e.getMessage();
        }
        return result.toString();
    }

}
