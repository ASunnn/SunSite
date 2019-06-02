package sunnn.sunsite.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * 文件操作工具类
 *
 * @author ASun
 */
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
            throw new IOException("Cannot Create Path :" + path);

        File f = new File(path + file.getOriginalFilename());
        file.transferTo(f);
        return f;
    }

    /**
     * 创建一个文件夹
     *
     * @param path 文件夹路径
     * @return 创建了文件夹或者文件夹已存在时返回true
     */
    public static boolean createPath(String path) {
        return createPath(new File(path));
    }

    /**
     * 创建一个文件夹
     *
     * @param pathFile 文件夹表示
     * @return 创建了文件夹或者文件夹已存在时返回true
     */
    public static boolean createPath(File pathFile) {
        if (pathFile.exists()) {
            return !pathFile.isFile();
        }
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
     * @return 当文件夹不为空，或者文件夹为空且被删除时返回true
     */
    public static boolean deletePath(String path) {
        return deletePath(new File(path));
    }

    /**
     * 删除一个文件夹
     *
     * @param file 文件夹路径表示
     * @return 当文件夹不为空，或者文件夹为空且被删除时返回true
     */
    public static boolean deletePath(File file) {
        File[] files = file.listFiles();
        if (files == null)
            return false;

        if (files.length > 0)
            return true;

        return file.delete();
    }

    /**
     * 强制删除一个文件夹
     *
     * @param path 文件夹路径
     * @return 当且仅当文件夹全部被删除时返回true
     */
    public static boolean deletePathForce(String path) {
        return deletePathForce(new File(path));
    }

    /**
     * 强制删除一个文件夹
     *
     * @param file 文件夹路径表示
     * @return 当且仅当文件夹全部被删除时返回true
     */
    public static boolean deletePathForce(File file) {
        boolean flag = true;

        File[] files = file.listFiles();
        if (files == null)
            return false;

        for (File f : files) {
            if (!(f.isDirectory() ? deletePathForce(f) : deleteFile(f))) {
                flag = false;
            }

        }

        return flag && file.delete();
    }

    /**
     * 对一个文件或文件夹进行重命名
     *
     * @param srcFile 原文件
     * @param newName 新的文件名
     * @return 当且仅当重命名成功时返回true
     */
    public static boolean rename(File srcFile, String newName) {
        String parentPath = srcFile.getParent() + File.separator;

        File newFile = new File(parentPath + newName);
        if (newFile.exists())
            return false;

        return srcFile.renameTo(newFile);
    }

    /**
     * 对一个文件夹进行复制
     *
     * @param srcFile     源文件夹
     * @param destPath    目标路径，目标路径不存在时，会自动创建
     * @param onlyContent 复制的时候是否只复制文件夹内的文件
     * @throws IOException 发生了意外错误
     */
    public static boolean copyPath(File srcFile, String destPath, boolean onlyContent) throws IOException {
        if (srcFile.isFile())
            return false;

        return onlyContent ?
                doCopyPath(srcFile, destPath) :
                doCopyPath(srcFile, destPath + File.separator + srcFile.getName());
    }

    private static boolean doCopyPath(File srcFile, String destPath) throws IOException {
        File destFile = new File(destPath);

        if (destFile.isFile())
            return false;
        if (!createPath(destFile))
            return false;

        File[] files = srcFile.listFiles();
        if (files == null)
            return false;

        boolean flag = true;
        for (File f : files) {
            if (f.isDirectory())
                flag = doCopyPath(f, destPath + File.separator + f.getName());
            else
                flag = copyFile(f, destPath);
        }
        return flag;
    }

    /**
     * 对一个文件进行复制
     *
     * @param srcFile  源文件
     * @param destPath 复制的目标路径，路径不存在时，会自动创建
     * @throws IOException 发生了意外错误
     */
    public static boolean copyFile(File srcFile, String destPath) throws IOException {
        if (srcFile.isDirectory())
            return false;

        String fileName = srcFile.getName();
        File destFile = new File(destPath + File.separator + fileName);
        if (!destFile.getParentFile().exists())
            createPath(destPath);

        try (FileChannel inputChannel =
                     new FileInputStream(srcFile).getChannel();
             FileChannel outputChannel =
                     new FileOutputStream(destFile).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
        return true;
    }

    /**
     * 检查路径或文件是否存在
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return  file.exists();
    }

    /**
     * 检测文件名中是否含有操作系统不支持的非法字符
     * 不支持匹配整个路径
     *
     * @return 当匹配到非法字符时，返回true
     */
    public static boolean fileNameMatcher(String key) {
        /*
            \/:*?"<>|
            [\\/:*?"<>|]
         */
        return Pattern.compile("[\\\\/:*?\"<>|]").matcher(key).find();
    }

    public static String generateHashcode(File file) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
        }

        try {
            InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = is.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
            is.close();
        } catch (IOException e) {
            return null;
        }

        return MD5s.byteToString(digest.digest());
    }
}
