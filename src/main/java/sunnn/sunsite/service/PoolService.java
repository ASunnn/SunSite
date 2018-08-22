package sunnn.sunsite.service;

import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.util.StatusCode;

import java.io.File;

public interface PoolService {

    File download(String illustratorName, String collectionName) throws IllegalFileRequestException;

    StatusCode delete(String illustratorName, String collectionName);

}
