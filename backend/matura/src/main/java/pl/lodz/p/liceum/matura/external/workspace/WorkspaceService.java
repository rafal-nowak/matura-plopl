package pl.lodz.p.liceum.matura.external.workspace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.springframework.beans.factory.annotation.Value;
import pl.lodz.p.liceum.matura.domain.workspace.*;
import pl.lodz.p.liceum.matura.utils.SimpleFileLock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class WorkspaceService implements Workspace {

    @Value("${volume.path}")
    private String baseWorkspace;

    @Override
    public String createWorkspace(String sourceRepositoryUrl) {
        String absoluteDestinationPath = crateDirectoryPath();
        cloneRepository(sourceRepositoryUrl, absoluteDestinationPath);

        try {
            SimpleFileLock lock = new SimpleFileLock(absoluteDestinationPath, 60);
            DownloadSio2Jail(absoluteDestinationPath + "/sio2jail");
            lock.releaseLock();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to lock the directory", e);
        }
        return absoluteDestinationPath;
    }

    private boolean deleteFile(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteFile(f);
            }
        }
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public void deleteWorkspace(String rootPathUrl) {
        if (!deleteFile(new File(rootPathUrl)))
            throw new RepositoryWasNotFoundException();
    }

    @Override
    public Map<String, Object> readTaskDefinitionFile(final String rootPathUrl) {

        String fullPath = java.nio.file.Paths.get(rootPathUrl, "task_definition.yml").toString();

        Map<String, Object> data;
        try {
            // Create ObjectMapper and YAMLFactory
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            // Read YAML file
            data = mapper.readValue(new File(fullPath), Map.class);
        } catch (IOException e) {
            throw new FileWasNotFoundException();
        }

        return data;
    }

    @Override
    public void writeFile(String rootPathUrl, String path, byte[] bytes) {
        String fullPath = java.nio.file.Paths.get(rootPathUrl, path).toString();

        try {
            Files.write(Paths.get(fullPath), bytes);
        } catch (IOException e) {
            throw new FileWasNotFoundException();
        }
    }

    @Override
    public byte[] readFile(String rootPathUrl, String path) {
        String fullPath = java.nio.file.Paths.get(rootPathUrl, path).toString();

        try {
            return Files.readAllBytes(Paths.get(fullPath));
        } catch (IOException e) {
            throw new FileWasNotFoundException();
        }
    }

    @Override
    public void commitChanges(String rootPathUrl) {
        CommitCommand commit;
        try (Git git = Git.open(new File(rootPathUrl))) {

            AddCommand add = git.add();
            add.addFilepattern(".").call();

            commit = git.commit();
            commit.setMessage("update commit").call();
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            throw new RepositoryWasNotFoundException();
        }
    }

    private void unlinkRemotes(Git git) {
        Set<String> remoteNames = git.getRepository().getRemoteNames();
        remoteNames.forEach(g -> {
            try {
                git.remoteRemove().setRemoteName(g).call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        });
    }

    private String crateDirectoryPath() {
        Path generatedDirectoryPath = java.nio.file.Paths.get(baseWorkspace, UUID.randomUUID().toString());
        return generatedDirectoryPath.toAbsolutePath().toString();
    }

    private void cloneRepository(String sourceRepositoryUrl, String destinationPath) {
        File repo = new File(destinationPath);
        Git git;
        try {
            git = Git.cloneRepository()
                    .setURI(sourceRepositoryUrl)
                    .setDirectory(repo)
                    .call();
        } catch (JGitInternalException | GitAPIException e) {
            throw new RepositoryAlreadyResidesInDestinationFolderException();
        }
        unlinkRemotes(git);
        git.close();
    }

    private void DownloadSio2Jail(String destinationPath) {
        try (FileOutputStream fos = new FileOutputStream(destinationPath)) {
            String sio2jailDownloadUrl = "https://github.com/sio2project/sio2jail/releases/download/v1.5.0/sio2jail";
            URL url = new URL(sio2jailDownloadUrl);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            throw new Sio2JailCouldNotBeDownloadedException();
        }
    }
}
