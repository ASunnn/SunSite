package sunnn.sunsite.service;

import com.github.kilianB.datastructures.tree.Result;
import com.github.kilianB.hashAlgorithms.DifferenceHash;
import com.github.kilianB.matcher.persistent.database.DatabaseImageMatcher;
import com.github.kilianB.matcher.persistent.database.H2DatabaseImageMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.PicDao;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.entity.Pic;
import sunnn.sunsite.exception.UnSupportSystemException;
import sunnn.sunsite.util.SunSiteProperties;
import sunnn.sunsite.util.SunsiteConstant;
import sunnn.sunsite.util.Utils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class SearchByImageService {

    private static Logger log = LoggerFactory.getLogger(SearchByImageService.class);

    @Resource
    private PicDao picDao;

    @Resource
    private PictureDao pictureDao;

    /**
     * 匹配图库中是否有相似的图片
     *
     * @param pictureFile 拿去匹配的文件
     * @return 匹配中的图片seq
     */
    public long[] matcherPicture(File pictureFile) {
        DatabaseImageMatcher matcher;
        try {
            matcher = getImageMatcher();
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Can't Get H2Database Connection. ", e);
            return new long[0];
        }
        matcher = setAlgorithm(matcher);

        PriorityQueue<Result<String>> result;
        try {
            result = matcher.getMatchingImages(pictureFile);
        } catch (IOException | SQLException e) {
            log.error("Add Image To Hash Failed. ", e);
            return new long[0];
        }

        long[] sequences = new long[result.size()];
        int index = 0;
        for (Result<String> r : result) {
            sequences[index++] = Long.valueOf(r.value);
        }
        return sequences;
    }

    /**
     * 插入新的图片数据
     */
    public void insertPictureData(Pic... pic) {
        DatabaseImageMatcher matcher;
        try {
            matcher = getImageMatcher();
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Can't Get H2Database Connection. ", e);
            return;
        }
        matcher = setAlgorithm(matcher);

        doInsertPictureData(matcher, pic);
    }

    /**
     * 初始化图库的哈希数据
     * 基本上只会运行一次
     */
    public void initPictureHashData() {
        // 获取计数
        int page = 0;
        int limit = SunsiteConstant.PAGE_SIZE;
        int count = picDao.count();

        // 初始化H2Database
        DatabaseImageMatcher matcher;
        try {
            matcher = getImageMatcher();
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Can't Get H2Database Connection. ", e);
            return;
        }
        matcher = setAlgorithm(matcher);

        while (count > 0) {
            List<Pic> picList = picDao.findAll(page++ * limit, limit);

            doInsertPictureData(matcher, (Pic[]) picList.toArray());
            count -= limit;
        }
    }

    private void doInsertPictureData(DatabaseImageMatcher matcher, Pic... picList) {
        for (Pic p : picList) {
            try {
                matcher.addImage(
                        String.valueOf(p.getSequence()),
                        new File(SunSiteProperties.savePath + p.getPath() + p.getName()));
            } catch (IOException | SQLException e) {
                log.error("Add Image To Hash Failed : ", e);
            }
        }
    }

    private DatabaseImageMatcher getImageMatcher() throws SQLException, ClassNotFoundException {
        String databasePath = null;
        try {
            databasePath = Utils.getPropertiesPath() + SunsiteConstant.SEARCH_BY_IMAGE_DATABASE;
        } catch (UnSupportSystemException ignored) {
        }

        String url = "jdbc:h2:" + databasePath;
        Class.forName("org.h2.Driver");

        Connection conn = DriverManager.getConnection(url, "root", "");
        return new H2DatabaseImageMatcher(conn);
    }

    private DatabaseImageMatcher setAlgorithm(DatabaseImageMatcher matcher) {
        matcher.addHashingAlgorithm(new DifferenceHash(64, DifferenceHash.Precision.Double), .2);
        return matcher;
    }
}
