package pl.lodz.p.liceum.matura.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.liceum.matura.BaseIT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
class SimpleFileLockTest extends BaseIT {
    private static final String TEST_FOLDER = "test_folder";

//    @BeforeEach
//    public void setUp() throws IOException {
//        Path testFolderPath = Paths.get(TEST_FOLDER);
//        if (!Files.exists(testFolderPath)) {
//            Files.createDirectory(testFolderPath);
//        }
//    }
//
//    @AfterEach
//    public void tearDown() throws IOException {
//        Path testFolderPath = Paths.get(TEST_FOLDER);
//        if (Files.exists(testFolderPath)) {
//            Files.walk(testFolderPath)
//                    .map(Path::toFile)
//                    .forEach(File::delete);
//        }
//    }
//
//    @Test
//    public void testLockFileCreation() throws Exception {
//        SimpleFileLock fileLock = new SimpleFileLock(TEST_FOLDER, 60);
//
//        Path lockFilePath = Paths.get(TEST_FOLDER, "lock.txt");
//        assertTrue(Files.exists(lockFilePath), "Lock file should be created.");
//
//        String content = Files.readString(lockFilePath);
//        String[] lines = content.split("\n");
//        assertEquals(1, lines.length, "Lock file should contain one line.");
//
//        assertDoesNotThrow(() -> LocalDateTime.parse(lines[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME), "Line should be a valid timestamp.");
//    }
//
//    @Test
//    public void testFolderDoesNotExist() {
//        String invalidFolder = "non_existent_folder";
//        Exception exception = assertThrows(FolderDoesNotExistException.class, () -> new SimpleFileLock(invalidFolder, 60));
//    }
//
//    @Test
//    public void testFileLockAlreadyExists() throws Exception {
//        SimpleFileLock fileLock1 = new SimpleFileLock(TEST_FOLDER, 60);
//        Exception exception = assertThrows(FileLockAlreadyExistsException.class, () -> new SimpleFileLock(TEST_FOLDER, 60));
//    }
//
//    @Test
//    public void testLockFileExpiration() throws Exception {
//        SimpleFileLock fileLock1 = new SimpleFileLock(TEST_FOLDER, 1); // Set short timeout
//        Thread.sleep(2000); // Wait for lock to expire
//
//        SimpleFileLock fileLock2 = assertDoesNotThrow(() -> new SimpleFileLock(TEST_FOLDER, 60), "New lock should be created after expiration.");
//        assertNotNull(fileLock2, "FileLock should not be null.");
//    }
//
//    @Test
//    public void testReleaseLock() throws Exception {
//        SimpleFileLock fileLock = new SimpleFileLock(TEST_FOLDER, 60);
//        fileLock.releaseLock();
//
//        Path lockFilePath = Paths.get(TEST_FOLDER, "lock.txt");
//        assertFalse(Files.exists(lockFilePath), "Lock file should be deleted after release.");
//    }
//
//    @Test
//    public void testForceReleaseLock() throws Exception {
//        SimpleFileLock fileLock = new SimpleFileLock(TEST_FOLDER, 1); // Set short timeout
//        Thread.sleep(2000); // Wait for lock to expire
//
//        assertDoesNotThrow(() -> SimpleFileLock.forceReleaseLock(TEST_FOLDER), "Expired lock should be force released.");
//
//        Path lockFilePath = Paths.get(TEST_FOLDER, "lock.txt");
//        assertFalse(Files.exists(lockFilePath), "Lock file should be deleted after force release.");
//    }
//
//    @Test
//    public void testInvalidLockFileFormat() throws Exception {
//        Path lockFilePath = Paths.get(TEST_FOLDER, "lock.txt");
//        Files.writeString(lockFilePath, "Invalid Data\n");
//
//        // Upewnij się, że konstruktora nie wyrzuca wyjątku
//        assertDoesNotThrow(() -> new SimpleFileLock(TEST_FOLDER, 60));
//
//        // Sprawdź, czy plik został nadpisany
//        String content = Files.readString(lockFilePath);
//        String[] lines = content.split("\\n");
//
//        // Nowy plik powinien zawierać jedną linię - poprawny timestamp
//        assertEquals(1, lines.length, "Lock file should contain one line.");
//        assertDoesNotThrow(() -> LocalDateTime.parse(lines[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
//                "Line should be a valid timestamp.");
//    }


}