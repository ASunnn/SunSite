package sunnn.sunsite.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtils {

    /**
     * 将MultipartFile存储为文件
     *
     * @param file MultipartFile文件
     * @param path 要存储的路径
     * @return 保存之后的文件
     * @throws IOException 转换时发生错误
     */
    public static File storeFile(MultipartFile file, String path) throws IOException {
        if (!createPath(path))
            throw new IOException("Cannot Create Path");

        File f = new File(path + file.getOriginalFilename());
        file.transferTo(f);
        return f;
    }

    /**
     * 创建一个文件夹
     *
     * @param path 文件夹路径
     * @return 当且仅当创建了文件夹时返回true
     */
    public static boolean createPath(String path) {
        return createPath(new File(path));
    }

    /**
     * 创建一个文件夹
     *
     * @param pathFile 文件夹表示
     * @return 当且仅当创建了文件夹时返回true
     */
    public static boolean createPath(File pathFile) {
        if (pathFile.isFile())
            return false;
        if (pathFile.exists())
            return true;
        return pathFile.mkdirs();
    }

    /**
     * 删除一个文件
     *
     * @param path 文件路径
     * @return 当且仅当文件被删除时返回true
     */
    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    /**
     * 删除一个文件
     *
     * @param file 文件表示
     * @return 当且仅当文件被删除时返回true
     */
    public static boolean deleteFile(File file) {
        return file.isFile() && file.delete();
    }

    /**
     * 删除一个文件夹
     *
     * @param path 文件夹路径
     * @return 当且仅当文件夹为空且被删除时返回true
     */
    public static boolean deletePath(String path) throws IOException {
        return deletePath(new File(path));
    }

    /**
     * 删除一个文件夹
     *
     * @param file 文件夹路径表示
     * @return 当且仅当文件夹为空且被删除时返回true
     * @throws IOException 传入的为文件或者无法列出文件夹下的子文件
     */
    public static boolean deletePath(File file) throws IOException {
        File[] files = getListFiles(file);

        if (files.length > 0)
            return false;

        return file.delete();
    }

    /**
     * 强制删除一个文件夹
     *
     * @param path 文件夹路径
     * @return 当且仅当文件夹全部被删除时返回true
     * @throws IOException 传入的为文件或者无法列出文件夹下的子文件
     */
    public static boolean deletePathForce(String path) throws IOException {
        return deletePathForce(new File(path));
    }

    /**
     * 强制删除一个文件夹
     *
     * @param file 文件夹路径表示
     * @return 当且仅当文件夹全部被删除时返回true
     * @throws IOException 传入的为文件或者无法列出文件夹下的子文件
     */
    public static boolean deletePathForce(File file) throws IOException {
        boolean flag = true;

        for (File f : getListFiles(file)) {
            flag = f.isDirectory() ? deletePathForce(f) : deleteFile(f);
        }

        return file.delete() && flag;
    }

    /**
     * 对一个文件或文件夹进行重命名
     *
     * @param srcFile 原文件
     * @param newName 新的文件名
     * @return 当且仅当重命名成功时返回true
     */
    public static boolean rename(File srcFile, String newName) throws IOException {
        File newFile = new File(newName);
        if (newFile.exists())
            throw new IOException("NewFile Is Exists");

        return srcFile.renameTo(new File(newName));
    }

    /**
     * 对一个文件夹进行复制
     *
     * @param srcFile  源文件夹
     * @param destPath 目标路径
     * @throws IOException 源文件或目标路径不是文件夹，或者无法列出某个文件夹的子文件
     */
    public static void copyPath(File srcFile, String destPath) throws IOException {
        if (srcFile.isFile())
            throw new IOException("SrcFile Cannot Be File");
        doCopyPath(srcFile, destPath);
    }

    /**
     * 对一个文件进行复制
     *
     * @param srcFile  源文件
     * @param destPath 复制的目标路径
     * @throws IOException 发生了意外错误
     */
    public static void copyFile(File srcFile, String destPath) throws IOException {
        if (srcFile.isDirectory())
            throw new IOException("SrcFile Cannot Be Directory");

        File destFile = new File(destPath);
        if (destFile.exists())
            throw new IOException("DestFile Is Exists");

        try (FileChannel inputChannel =
                     new FileInputStream(srcFile).getChannel();
             FileChannel outputChannel =
                     new FileOutputStream(destFile).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }


    private static void doCopyPath(File srcFile, String destPath) throws IOException {
        if (srcFile.isDirectory()) {
            File destFile = new File(destPath);

            if (destFile.isFile())
                throw new IOException("DestFile Cannot Be File");
            createPath(destFile);

            File[] files = srcFile.listFiles();
            if (files == null)
                throw new IOException("Cannot List Files");

            for (File f : files) {
                doCopyPath(f, destPath + SunSiteConstant.pathSeparator + f.getName());
            }
        } else
            copyFile(srcFile, destPath);
    }

    private static File[] getListFiles(File file) throws IOException {
        if (file.isFile())
            throw new IOException("It Must Be A Directory");

        File[] files = file.listFiles();
        if (files == null)
            throw new IOException("Cannot List Files");
        return files;
    }

}
