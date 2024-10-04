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
@Profile("prod") // Wykonuje się tylko w profilu produkcyjnym
@RequiredArgsConstructor
public class CommandInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("Starting Command Initializer...");

        executeStartupCommand("apt-get update");
        executeStartupCommand("apt install docker.io -y");
        executeStartupCommand("apt install docker-compose -y");
        executeStartupCommand("apt install python3-setuptools -y");

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
            // Uruchomienie komendy w systemie operacyjnym
            Process process = Runtime.getRuntime().exec(command);

            // Pobranie wyniku z wejścia standardowego
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            // Pobranie wyniku z wyjścia standardowego błędów (jeśli istnieje)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                result.append("ERROR: ").append(line).append("\n");
            }

            // Zakończenie procesu
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            return "Command execution failed: " + e.getMessage();
        }

        return result.toString();
    }
}
