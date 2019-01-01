package sunnn.sunsite.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Utils {

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
        //获取后缀名
        String extension = path.substring(path.lastIndexOf('.') + 1);
        //解码具有给定后缀的文件
        Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix(extension);
        if (iterator.hasNext()) {
            ImageReader reader = iterator.next();
            try {
                reader.setInput(new FileImageInputStream(new File(path)));
                result = new Dimension(
                        reader.getWidth(reader.getMinIndex()),
                        reader.getHeight(reader.getMinIndex()));
            } finally {
                reader.dispose();
            }
        }
        return result;
    }
}
