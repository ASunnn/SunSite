package sunnn.sunsite.util;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Deprecated
class SunZipDecompress {

    /**
     * 解压Zip文件
     *
     * @param srcFile  zip文件
     * @param destPath 目标文件夹，会将zip内的所有文件解压到该文件夹下（不包括路径）
     * @return 解压出来的文件夹
     * @throws IOException 出错了
     */
    public static File[] decompressFile(File srcFile, String destPath) throws IOException {
        File destFile = new File(destPath);
        if (!destFile.isDirectory())
            throw new IOException("目标路径非文件夹");

        CheckedInputStream checkInput = new CheckedInputStream(
                new FileInputStream(srcFile),
                new CRC32());

        ZipInputStream zipInput = new ZipInputStream(checkInput);
        File[] files = decompressFile(zipInput, destFile);
        zipInput.close();
        return files;
    }

    private static File[] decompressFile(ZipInputStream zipFile, File destFile) throws IOException {
        ZipEntry entry;
        ArrayList<File> files = new ArrayList<>();

        while ((entry = zipFile.getNextEntry()) != null) {
            File entryFile = new File(
                    destFile.getPath() + File.separator + entry.getName());

            checkFile(entryFile);

            if (entryFile.isDirectory())
                entryFile.mkdir();
            else
                decompress(zipFile, entryFile);

            files.add(entryFile);
        }

        File[] res = new File[files.size()];
        files.toArray(res);
        return res;
    }

    private static void checkFile(File entryFile) {
        File parentFile = new File(entryFile.getParent());
        if (!parentFile.exists())
            parentFile.mkdirs();
    }

    private static void decompress(ZipInputStream zipInput, File destFile) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(destFile));
        int bufferSize = 1024;
        int count;
        byte[] data = new byte[bufferSize];
        while ((count = zipInput.read(data, 0, bufferSize)) != -1) {
            outputStream.write(data, 0, count);
        }
        outputStream.close();
    }

}

@Deprecated
class ZipDecompress {

    /**
     * 解压Zip文件
     * 此方法会将zip文件内所有文件解压至给定的路径
     * 忽略zip文件内的文件目录结构
     * 啊对了，虽然写着ZipDecompress，rar文件也能支持
     *
     * @param srcFile  zip文件
     * @param destPath 目标文件夹，会将zip内的所有文件解压到该文件夹下（不包括路径）
     * @return 解压出来的文件夹
     * @throws IOException 出错了
     */
    public static File[] decompressFile(File srcFile, String destPath) throws IOException {
        /*
            检查目标路径
         */
        File destFile = new File(destPath);
        if (!destFile.exists())
            destFile.mkdirs();
        else if (!destFile.isDirectory())
            throw new IOException("目标路径非文件夹");

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
        File[] files = decompressFile(zipInput, destFile);
        zipInput.close();
        return files;
    }

    private static File[] decompressFile(ZipInputStream zipFile, File destFile) throws IOException {
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
            decompress(zipFile, entryFile);
            /*
                解压出来的文件增加到结果集中
             */
            files.add(entryFile);
        }
        File[] res = new File[files.size()];
        files.toArray(res);
        return res;
    }

    private static void decompress(ZipInputStream zipInput, File destFile) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(destFile));
        int bufferSize = 1024;
        int count;
        byte[] data = new byte[bufferSize];
        while ((count = zipInput.read(data, 0, bufferSize)) != -1) {
            outputStream.write(data, 0, count);
        }
        outputStream.close();
    }

}