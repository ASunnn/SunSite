package sunnn.sunsite.dto.response;

import sunnn.sunsite.util.StatusCode;

public class BaseResponse {

    private int code;

    private String msg;

    public BaseResponse(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.name();
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
