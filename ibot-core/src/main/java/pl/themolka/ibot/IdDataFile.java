package pl.themolka.ibot;

import org.bson.types.ObjectId;
import pl.themolka.ibot.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IdDataFile {
    public static final String FILENAME = "uid.dat";

    private final Logger logger;
    private final File file;

    public IdDataFile(Logger logger, File directory) {
        this.logger = logger;
        this.file = new File(directory, FILENAME);
    }

    public File getFile() {
        return this.file;
    }

    public ObjectId load() {
        try {
            this.logger.info("Loading the " + this.getFile().getPath() + " file...");

            return this.read();
        } catch (ClassNotFoundException | IOException ex) {
            this.logger.error("Could not load " + this.getFile().getPath() + ": " + ex.getMessage(), ex);
        }

        return null;
    }

    public ObjectId read() throws ClassNotFoundException, IOException {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(this.getFile()))) {
            Object object = input.readObject();

            if (object instanceof ObjectId) {
                return (ObjectId) object;
            }
        }

        return null;
    }

    public boolean save(ObjectId objectId) {
        try {
            this.logger.info("Saving the " + this.getFile().getPath() + " file...");

            this.write(objectId);
            return true;
        } catch (IOException ex) {
            this.logger.error("Could not save " + this.getFile().getPath() + ": " + ex.getMessage(), ex);
        }

        return false;
    }

    public void write(ObjectId objectId) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(this.getFile()))) {
            output.writeObject(objectId);
            output.flush();
        }
    }
}
