package sunnn.sunsite.util;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 压缩文件解压工具类
 */
public class ZipDecompress {

    private static final int buffer = 1024;

    /**
     * 解压Zip文件
     * 此方法会将zip文件内所有文件解压至给定的路径
     * 忽略zip文件内的文件目录结构
     *
     * @param srcFile  zip/rar文件
     * @param destPath 目标文件夹，会将zip内的所有文件解压到该文件夹下
     * @return 解压出来的文件
     * @throws IOException 发生错误
     */
    public static File[] decompress(File srcFile, String destPath) throws IOException {
        /*
            检查目标路径
         */
        File destFile = new File(destPath);
        if (!destFile.exists()) {
            if (!FileUtils.createPath(destFile))
                throw new IOException("Can not Create TempPath");
        } else if (!destFile.isDirectory())
            throw new IOException("DestPath Cannot Be A File");
        /*
            CRC32冗余校验
         */
        CheckedInputStream checkInput = new CheckedInputStream(
                new FileInputStream(srcFile),
                new CRC32());
        /*
            解压
         */
        ZipInputStream zipInput = new ZipInputStream(checkInput);
        File[] files = decompress(zipInput, destFile);
        zipInput.close();
        return files;
    }

    private static File[] decompress(ZipInputStream zipFile, File destFile) throws IOException {
        ZipEntry entry;
        ArrayList<File> files = new ArrayList<>();
        /*
            循环解压压缩文件下的东西
         */
        while ((entry = zipFile.getNextEntry()) != null) {
            //若是目录直接忽略
            if (entry.isDirectory())
                continue;
            /*
                生成文件，并将压缩文件里在文件夹内部的文件提出来
             */
            String entryPath = entry.getName();
            File entryFile = new File(
                    destFile.getPath() + File.separator
                            + entryPath.substring(entryPath.lastIndexOf("/")));
            /*
                解压
             */
            doDecompress(zipFile, entryFile);
            /*
                解压出来的文件增加到结果集中
             */
            files.add(entryFile);
        }
        File[] res = new File[files.size()];
        files.toArray(res);
        return res;
    }

    private static void doDecompress(ZipInputStream zipInput, File destFile) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(destFile));
        int count;
        byte[] data = new byte[buffer];
        while ((count = zipInput.read(data, 0, buffer)) != -1) {
            outputStream.write(data, 0, count);
        }
        outputStream.close();
    }

}