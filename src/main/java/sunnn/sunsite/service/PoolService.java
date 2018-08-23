package sunnn.sunsite.service;

import sunnn.sunsite.dto.response.PictureListResponse;
import sunnn.sunsite.entity.Picture;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;

import java.io.File;
import java.util.List;

public interface PoolService {

    List<Picture> getPictureFromPool(String illustrator, String collection);

    PictureListResponse getPictureFromPool(String illustrator, String collection, int page, int size);

    File download(String illustratorName, String collectionName) throws IllegalFileRequestException;

    StatusCode delete(String illustratorName, String collectionName);

}
