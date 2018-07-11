package sunnn.sunsite.message.response;

import sunnn.sunsite.message.StatusCode;

public class BaseResponse {

    private int code;

    private String msg;

    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

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
