package go.users;

import com.google.cloud.storage.*;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * A class to manage file operations - based on code from HW2
 */
public class Persistence {
    private static final String BUCKET_NAME = "gousers-store";

    /**
     * Handles the loading of data into instances of classes that implement the Serialazable interface
     * @return an object instance of the corresponding class that was saved and which implements Serialazable
     * @throws IOException if a problem exists with accessing the file
     * @throws ClassNotFoundException if a problem exists with reading the constructed object
     */
    public static Serializable loadDataFromFile(String filename, PrintWriter printWriter) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = null;
        Serializable data = null;
        try {
            Storage storage = StorageOptions.getDefaultInstance().getService();
            Blob blob = storage.get(BlobId.of(BUCKET_NAME, filename));
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(blob.getContent());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            data = (Serializable) objectInputStream.readObject();
            printWriter.append("Filemanager: LoadData: " + filename + " successful: " + data + "<br/>");
        } catch (Exception e){
            printWriter.append("Filemanager: LoadData " + filename + " failed: " + e.getMessage() + "<br/>");
        } finally {
            objectInputStream.close();
        }
        return data;
    }

    /**
     * Handles the saving to files of data of instances of classes that implement the Serialazable interface
     * @param dataToSave the instance that implements Serialazble
     * @throws IOException if a problem exists with accessing the file
     */
    public static void saveDataToFile(Serializable dataToSave, String filename, PrintWriter printWriter) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        ObjectOutputStream objectOutputStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(dataToSave);
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            Blob blob =
                    storage.create(
                            BlobInfo.newBuilder(BUCKET_NAME, filename).build(),
                            inputStream);
            printWriter.append("Filemanager: SaveData " + filename + " successful: " + blob.getMediaLink() + "<br/>");
        } catch (Exception e){
            printWriter.append("Filemanager: SaveData " + filename + " failed: " + e.getMessage() + "<br/>");
        } finally {
            objectOutputStream.flush();
            objectOutputStream.close();
        }
    }
}
