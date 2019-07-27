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
            log.error("Add Image To Hash Failed : ", e);
            return new long[0];
        }

        long[] sequences = new long[result.size()];
        int index = 0;
        for (Result<String> r : result) {
            sequences[index++] = Long.valueOf(r.value);
        }
        return sequences;
    }

    public void initPictureHashData() {
        int page = 0;
        int limit = SunsiteConstant.PAGE_SIZE;
        int count = picDao.count();

        while (count > 0) {
            DatabaseImageMatcher matcher;
            try {
                matcher = getImageMatcher();
            } catch (SQLException | ClassNotFoundException e) {
                log.error("Can't Get H2Database Connection. ", e);
                return;
            }
            matcher = setAlgorithm(matcher);

            List<Pic>  picList = picDao.findAll(page++ * limit, limit);
            long totalSize = 0;
            for (Pic p : picList) {
                totalSize += p.getSize();
            }
            System.out.println(totalSize / 1024 + "kb");
            for (Pic p : picList) {
                try {
                    matcher.addImage(
                            String.valueOf(p.getSequence()),
                            new File(SunSiteProperties.savePath + p.getPath() + p.getName()));
                } catch (IOException | SQLException e) {
                    log.error("Add Image To Hash Failed : ", e);
                }
            }

            count -= limit;
            System.out.println(count);
        }
    }

//    public void initPictureHashData() {
//        DatabaseImageMatcher matcher = null;
//        try {
//            matcher = getImageMatcher();
//        } catch (SQLException | ClassNotFoundException e) {
//            log.error("Can't Get H2Database Connection. ", e);
//            return;
//        }
//        matcher = setAlgorithm(matcher);
//
//            List<Picture>  picList = pictureDao.findAllByCollection(7125496005125348588L);
//
//            for (Picture pic : picList) {
//                System.out.println(pic.getName());
//                Pic p = picDao.find(pic.getSequence());
//                try {
//                    matcher.addImage(
//                            String.valueOf(p.getSequence()),
//                            new File(SunSiteProperties.savePath + p.getPath() + p.getName()));
//                } catch (IOException | SQLException e) {
//                    log.error("Add Image To Hash Failed : ", e);
//                }
//            }
//    }

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
