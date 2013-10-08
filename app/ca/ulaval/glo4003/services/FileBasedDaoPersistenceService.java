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

        System.out.println(objectPath.toPath());

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
        File baseDir = getOrCreateDir(new File(new File("").getAbsolutePath()), "data");
        File profileDir = getOrCreateDir(baseDir, this.profile);
        return getOrCreateDir(profileDir, dao.getClass().getSimpleName() + ".ser");
    }

    private File getOrCreateDir(File parent, String path) {
        File baseDir = new File(parent, path);

        if (baseDir.exists()) {
            baseDir.mkdir();
        }

        return baseDir;
    }
}
