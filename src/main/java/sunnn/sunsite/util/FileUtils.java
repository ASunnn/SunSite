package sunnn.sunsite.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static boolean createPath(String path) {
        return createPath(new File(path));
    }

    public static boolean createPath(File pathFile) {
        if (pathFile.exists())
            return true;
        return pathFile.mkdirs();
    }

    public static File storeFile(MultipartFile file, String path) throws IOException {
        if (!createPath(path))
            throw new IOException("Cannot Create Path");

        File f = new File(path + file.getOriginalFilename());
        file.transferTo(f);
        return f;
    }

    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    public static boolean deleteFile(File file) {
        return file.isFile() && file.delete();
    }

    public static boolean deletePath(String path) {
        return deletePath(new File(path));
    }

    public static boolean deletePath(File file) {
        if (!file.isDirectory())
            return false;

        File[] files = file.listFiles();
        if (files == null)
            return false;
        if (files.length > 0)
            return true;

        return file.delete();
    }

    public static boolean deletePathForce(String path) {
        return deletePathForce(new File(path));
    }

    public static boolean deletePathForce(File file) {
        if (!file.isDirectory())
            return false;

        File[] files = file.listFiles();
        if (files == null)
            return false;

        boolean flag = true;
        for (File f : files)
            flag = deletePathForce(f);

        return file.delete() && flag;
    }

    public static boolean rename(File srcFile, String newName) {
        return srcFile.renameTo(new File(newName));
    }

    public static void copy(File srcFile, String destPath) throws IOException {
        try (FileChannel inputChannel =
                     new FileInputStream(srcFile).getChannel();
             FileChannel outputChannel =
                     new FileOutputStream(new File(destPath)).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

}
