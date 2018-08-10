package sunnn.sunsite.util;

import java.io.*;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件压缩工具类
 */
public class ZipCompress {

    private static final int buffer = 1024;

    private static final String pathSeparator = "/";

    /**
     * 压缩文件
     *
     * @param srcPath  需要压缩的源文件
     * @param destPath 目标文件
     * @throws IOException 发生错误
     */
    public static void compress(String srcPath, String destPath) throws IOException {
        compress(new File(srcPath), new File(destPath));
    }

    private static void compress(File srcFile, File destFile) throws IOException {
        /*
            检查目标路径
         */
        if (destFile.isDirectory())
            throw new IOException("DestFile Cannot Be Directory");
        if (!FileUtils.createPath(destFile.getParent()))
            throw new IOException("Can not Create TempPath");
        /*
            对输出文件做CRC32校验
         */
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(
                destFile), new CRC32());

        ZipOutputStream zipOutputStream = new ZipOutputStream(cos);

        doCompress(srcFile, zipOutputStream, "");

        zipOutputStream.flush();
        zipOutputStream.close();
    }

    /**
     * 压缩
     *
     * @param srcFile         需要压缩的源文件
     * @param zipOutputStream 压缩文件输出流
     * @param basePath        压缩包内的相对路径
     */
    private static void doCompress(
            File srcFile, ZipOutputStream zipOutputStream, String basePath) throws IOException {
        if (srcFile.isDirectory())
            compressDir(srcFile, zipOutputStream, basePath);
        else
            compressFile(srcFile, zipOutputStream, basePath);
    }

    /**
     * 对目录进行压缩处理
     *
     * @param path            需要压缩的目录
     * @param zipOutputStream 压缩文件输出流
     * @param basePath        压缩包内的相对路径
     */
    private static void compressDir(
            File path, ZipOutputStream zipOutputStream, String basePath) throws IOException {

        File[] files = path.listFiles();
        String newPath = basePath + path.getName() + pathSeparator;
        /*
            对子文件/目录递归调用compress
            如果当前目录为空则直接构造空目录
         */
        if (Objects.requireNonNull(files).length <= 0) {
            ZipEntry entry = new ZipEntry(newPath);
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.closeEntry();
        } else {
            for (File file : files) {
                doCompress(file, zipOutputStream, newPath);
            }
        }
    }

    /**
     * 对文件进行压缩
     *
     * @param file            待压缩的文件
     * @param zipOutputStream 压缩文件输出流
     * @param path            压缩包内的相对路径
     */
    private static void compressFile(
            File file, ZipOutputStream zipOutputStream, String path) throws IOException {
        //据说用winrar打开会乱码
        ZipEntry entry = new ZipEntry(path + file.getName());
        zipOutputStream.putNextEntry(entry);
        /*
            写入压缩文件
         */
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        int count;
        byte data[] = new byte[buffer];
        while ((count = inputStream.read(data, 0, buffer)) != -1)
            zipOutputStream.write(data, 0, count);

        inputStream.close();
        zipOutputStream.closeEntry();
    }

}
