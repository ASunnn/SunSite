package sunnn.sunsite.util;

public enum StatusCode {

    OJBK(0),
    ERROR(-1),
    DUPLICATE_INPUT(1 << 0),
    ILLEGAL_INPUT(1 << 1),
    NO_DATA(1 << 2),
    UPLOAD_TIMEOUT(1 << 3),
    DELETE_FAILED(1 << 4),
    MODIFY_FAILED(1 << 5),
    VERIFY_FAILED(1 << 6);

    private int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ":" + this.name();
    }
}
