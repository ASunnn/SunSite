package sunnn.sunsite.message;

public enum StatusCode {
    OJBK(0),
    UPLOAD_ERROR(1 << 0);

    private int code;

    StatusCode(int code) {
    }

    public int getCode() {
        return code;
    }
}
