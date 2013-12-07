package ca.ulaval.glo4003.persistence;

import ca.ulaval.glo4003.domain.Dao;
import ca.ulaval.glo4003.domain.Record;

import java.io.*;
import java.util.List;

public class FileBasedDaoPersistenceService implements DaoPersistenceService {

    private String profile;

    public FileBasedDaoPersistenceService(String profile) {
        this.profile = profile;
    }

    @Override
    public <T extends Dao> void persist(T dao) throws IOException {
        File objectPath = getDaoPersistencePath(dao);

        FileOutputStream fileStream = new FileOutputStream(objectPath);

        try (ObjectOutputStream serializer = new ObjectOutputStream(fileStream)) {
            serializer.writeObject(dao.list());
        }
    }

    @Override
    public <T extends Record, Y extends Dao> List<T> restore(Y dao) throws IOException, ClassNotFoundException {
        File objectPath = getDaoPersistencePath(dao);

        FileInputStream saveFile = new FileInputStream(objectPath);

        try (ObjectInputStream serializer = new ObjectInputStream(saveFile)) {
            @SuppressWarnings("unchecked")
            final List<T> entities = (List<T>) serializer.readObject();
            return entities;
        }
    }

    public <T extends Dao> File getDaoPersistencePath(T dao) {
        try {
            File baseDir = getOrCreateDir(new File(new File("").getAbsolutePath()), ca.ulaval.glo4003.persistence.ConstantsManager.PERSISTENCE_DIRECTORY);
            File profileDir = getOrCreateDir(baseDir, this.profile);

            return new File(profileDir, dao.getClass().getSimpleName() + ca.ulaval.glo4003.persistence.ConstantsManager.PERSISTENCE_FILE_EXTENSION);
        } catch (Exception e) {
            System.err.println(String.format("ERROR: Output path for DAO %s doesn't exist and could not be created." +
                    " Inner exception: %s", dao.getClass().getName(), e.getMessage()));

            return null;
        }
    }

    private File getOrCreateDir(File parent, String path) throws Exception {
        File baseDir = new File(parent, path);

        if (!baseDir.exists()) {
            if (!baseDir.mkdirs()) {
                throw new Exception("Directory " + baseDir.getPath() + " could not be created.");
            }
        }

        return baseDir;
    }
}
