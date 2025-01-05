package pl.lodz.p.liceum.matura.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.lodz.p.liceum.matura.BaseIT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class FileLockTest extends BaseIT {
    private static final String TEST_FOLDER = "test_folder";

    @BeforeEach
    public void setUp() throws IOException {
        Path testFolderPath = Paths.get(TEST_FOLDER);
        if (!Files.exists(testFolderPath)) {
            Files.createDirectory(testFolderPath);
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        Path testFolderPath = Paths.get(TEST_FOLDER);
        if (Files.exists(testFolderPath)) {
            Files.walk(testFolderPath)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void testLockFileCreation() throws Exception {
        FileLock fileLock = new FileLock(TEST_FOLDER, 60);

        Path lockFilePath = Paths.get(TEST_FOLDER, "lock.txt");
        assertTrue(Files.exists(lockFilePath), "Lock file should be created.");

        String content = Files.readString(lockFilePath);
        String[] lines = content.split("\n");
        assertEquals(2, lines.length, "Lock file should contain two lines.");

        assertDoesNotThrow(() -> UUID.fromString(lines[0]), "First line should be a valid UUID.");
        assertDoesNotThrow(() -> LocalDateTime.parse(lines[1]), "Second line should be a valid timestamp.");
    }

    @Test
    public void testFolderDoesNotExist() {
        String invalidFolder = "non_existent_folder";
        Exception exception = assertThrows(FolderDoesNotExistException.class, () -> new FileLock(invalidFolder, 60));
    }

    @Test
    public void testFileLockAlreadyExists() throws Exception {
        FileLock fileLock1 = new FileLock(TEST_FOLDER, 60);
        Exception exception = assertThrows(FileLockAlreadyExistsException.class, () -> new FileLock(TEST_FOLDER, 60));
    }

    @Test
    public void testLockFileExpiration() throws Exception {
        FileLock fileLock1 = new FileLock(TEST_FOLDER, 1); // Set short timeout
        Thread.sleep(2000); // Wait for lock to expire

        FileLock fileLock2 = assertDoesNotThrow(() -> new FileLock(TEST_FOLDER, 60), "New lock should be created after expiration.");
        assertNotNull(fileLock2, "FileLock should not be null.");
    }

    @Test
    public void testReleaseLock() throws Exception {
        FileLock fileLock = new FileLock(TEST_FOLDER, 60);
        fileLock.releaseLock();

        Path lockFilePath = Paths.get(TEST_FOLDER, "lock.txt");
        assertFalse(Files.exists(lockFilePath), "Lock file should be deleted after release.");
    }

    @Test
    public void testForceReleaseLock() throws Exception {
        FileLock fileLock = new FileLock(TEST_FOLDER, 1); // Set short timeout
        Thread.sleep(2000); // Wait for lock to expire

        assertDoesNotThrow(() -> FileLock.forceReleaseLock(TEST_FOLDER), "Expired lock should be force released.");

        Path lockFilePath = Paths.get(TEST_FOLDER, "lock.txt");
        assertFalse(Files.exists(lockFilePath), "Lock file should be deleted after force release.");
    }

    @Test
    public void testInvalidLockFileFormat() throws Exception {
        Path lockFilePath = Paths.get(TEST_FOLDER, "lock.txt");
        Files.writeString(lockFilePath, "Invalid Data\n");

        Exception exception = assertThrows(IllegalStateException.class, () -> new FileLock(TEST_FOLDER, 60));
        assertEquals("Invalid lock file format.", exception.getMessage());
    }

}