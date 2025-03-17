package pl.lodz.p.liceum.matura.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileLock {
    private final Path folderPath;
    private final Path lockFilePath;
    private final UUID ownerUuid;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileLock(String folderPathStr, long timeoutInSeconds) throws Exception {
        this.folderPath = Paths.get(folderPathStr);
        this.lockFilePath = this.folderPath.resolve("lock.txt");
        this.ownerUuid = UUID.randomUUID();

        if (!Files.exists(this.folderPath) || !Files.isDirectory(this.folderPath)) {
            throw new FolderDoesNotExistException();
        }

        if (Files.exists(this.lockFilePath)) {
            String content = Files.readString(this.lockFilePath);
            String[] lines = content.split("\n");
            if (lines.length < 2) {
                throw new IllegalStateException("Invalid lock file format.");
            }

            UUID existingUuid = UUID.fromString(lines[0]);
            LocalDateTime expirationTime = LocalDateTime.parse(lines[1], formatter);

            if (expirationTime.isAfter(LocalDateTime.now())) {
                throw new FileLockAlreadyExistsException();
            } else {
                // Lock expired, recreate
                createLockFile(timeoutInSeconds);
            }
        } else {
            createLockFile(timeoutInSeconds);
        }
    }

    private void createLockFile(long timeoutInSeconds) throws IOException {
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(timeoutInSeconds);
        try (FileWriter writer = new FileWriter(lockFilePath.toFile())) {
            writer.write(ownerUuid.toString() + "\n");
            writer.write(expirationTime.format(formatter) + "\n");
        }
    }

    public void releaseLock() throws Exception {
        if (!Files.exists(this.lockFilePath)) {
            throw new IllegalStateException("No lock file to release.");
        }

        String content = Files.readString(this.lockFilePath);
        String[] lines = content.split("\n");
        if (lines.length < 2) {
            throw new IllegalStateException("Invalid lock file format.");
        }

        UUID existingUuid = UUID.fromString(lines[0]);
        if (!existingUuid.equals(this.ownerUuid)) {
            throw new IllegalStateException("Only the lock owner can release the lock.");
        }

        Files.delete(this.lockFilePath);
    }

    public static void forceReleaseLock(String folderPathStr) throws IOException {
        Path lockFilePath = Paths.get(folderPathStr, "lock.txt");
        if (!Files.exists(lockFilePath)) {
            throw new IllegalStateException("No lock file to force release.");
        }

        String content = Files.readString(lockFilePath);
        String[] lines = content.split("\n");
        if (lines.length < 2) {
            throw new IllegalStateException("Invalid lock file format.");
        }

        LocalDateTime expirationTime = LocalDateTime.parse(lines[1], formatter);
        if (expirationTime.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Lock is still valid and cannot be force released.");
        }

        Files.delete(lockFilePath);
    }

}
