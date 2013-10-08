package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.dataaccessobjects.DataAccessObject;
import ca.ulaval.glo4003.models.Record;

import java.io.*;
import java.util.List;

public class FileBasedDaoPersistenceService implements DaoPersistenceService {

    private String profile;

    public FileBasedDaoPersistenceService(String profile) {
        this.profile = profile;
    }

    @Override
    public <T extends DataAccessObject> void persist(T dao) throws IOException {
        File objectPath = getOutputPathForObject(dao);

        FileOutputStream fileStream = new FileOutputStream(objectPath);
        ObjectOutputStream oos = new ObjectOutputStream(fileStream);

        oos.writeObject(dao.list());
    }

    @Override
    public <T extends Record, Y extends DataAccessObject> List<T> restore(Y dao) throws IOException, ClassNotFoundException {
        File objectPath = getOutputPathForObject(dao);

        FileInputStream saveFile = new FileInputStream(objectPath);
        ObjectInputStream restore = new ObjectInputStream(saveFile);

        return (List<T>) restore.readObject();
    }

    private <T> File getOutputPathForObject(T dao) {
        try {
            File baseDir = getOrCreateDir(new File(new File("").getAbsolutePath()), "data");
            File profileDir = getOrCreateDir(baseDir, this.profile);
            return new File(profileDir, dao.getClass().getSimpleName() + ".ser");
        } catch (Exception e) {
            System.err.println(String.format("ERROR: Output path for DAO %s doesn't exist and could not be created." +
                    " Inner exception: %s", dao.getClass().getName(), e.getMessage()));

            return null;
        }
    }

    private File getOrCreateDir(File parent, String path) throws Exception {
        File baseDir = new File(parent, path);

        if (!baseDir.exists()) {
            if(!baseDir.mkdirs()) {
                throw new Exception("Directory " + baseDir.getPath() + " could not be created.");
            }
        }

        return baseDir;
    }
}
