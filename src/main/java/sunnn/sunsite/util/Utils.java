package sunnn.sunsite.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunnn.sunsite.exception.UnSupportSystemException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Utils {

    private static Logger log = LoggerFactory.getLogger(Utils.class);

    public static boolean isIllegalPageParam(int page) {
        return page < 0;
    }

    /* 快速获取图片分辨率 */

    public static int[] getPictureSize(String path) throws IOException {
        Dimension dimension = getImageDimension(path);
        if (dimension == null)
            throw new IOException("No Dimension Info");
        String dimensionString = dimension.toString();

        //分割出图片信息部分
        String info = dimensionString.substring(
                dimensionString.indexOf("[") + 1,
                dimensionString.indexOf("]"));
        //获取长宽信息
        String width = info.substring(info.indexOf("=") + 1, info.indexOf(","));
        String height = info.substring(info.lastIndexOf("=") + 1);

        int[] size = new int[2];
        size[0] = Integer.valueOf(width);
        size[1] = Integer.valueOf(height);
        return size;
    }

    private static Dimension getImageDimension(String path) throws IOException {
        if (path.lastIndexOf('.') == -1)
            return null;

        Dimension result = null;
        // 获取后缀名
        String extension = path.substring(path.lastIndexOf('.') + 1);
        // 解码具有给定后缀的文件
        Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix(extension);
        if (iterator.hasNext()) {
            ImageReader reader = iterator.next();
            try (FileImageInputStream inputStream = new FileImageInputStream(new File(path))) {
                reader.setInput(inputStream);
                result = new Dimension(
                        reader.getWidth(reader.getMinIndex()),
                        reader.getHeight(reader.getMinIndex()));
            } finally {
                reader.dispose();
            }
        }
        return result;
    }

    /**
     * 根据不同系统获取配置文件路径
     */
    public static String getPropertiesPath() throws UnSupportSystemException {
        String sys = System.getProperty("os.name");

        if (sys.contains("Windows")) {
            return "C:\\ProgramData\\Sunnn\\sunsite\\";
        } else if (sys.contains("Linux"))
            return "";

        throw new UnSupportSystemException(sys);
    }

    @Deprecated
    public static String readDataFromFile(int line) {
//        InputStreamReader is;
//        try {
//            is = new InputStreamReader(new FileInputStream(new File(getDataFilePath())));
//        } catch (FileNotFoundException e) {
//            return null;
//        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getDataFilePath()));
            while (--line > 0) {
                reader.readLine();
            }
            String data = reader.readLine();
            reader.close();

            return data;
        } catch (IOException e) {
            log.error("Read Data File Error : ", e);
        }
        return null;
    }

    @Deprecated
    public static void writeDataToFile(String data, int line) {
        ArrayList<String> content = new ArrayList<>();
        String filePath = getDataFilePath();

        // 读取
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String s;
            while ((s = reader.readLine()) != null) {
                content.add(s);
            }
            reader.close();
        } catch (IOException ignored) {
        }

        // 覆盖
        if (content.size() < line)
            line = content.size();
        content.set(line - 1, data);

        // 写入
        if (filePath.isEmpty())
            return;
        try {
            BufferedWriter outputStream = new BufferedWriter(new FileWriter(filePath));
            for (String c : content) {
                outputStream.write(c);
                outputStream.newLine();
            }
            outputStream.close();
        } catch (IOException e) {
            log.error("Write Data File Error : ", e);
        }
    }

    @Deprecated
    private static String getDataFilePath() {
        try {
            return Utils.getPropertiesPath() + SunsiteConstant.DATA_FILE;
        } catch (UnSupportSystemException ignored) {
        }
        return "";
    }
}
