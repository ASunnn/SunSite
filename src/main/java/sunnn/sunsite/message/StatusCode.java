package sunnn.sunsite.message;

public enum StatusCode {
    OJBK(0),
    EMPTY_UPLOAD(1 << 0),
    DUPLICATED_FILENAME(1 << 1),
    ILLEGAL_DATA(1 << 2);



    private int code;

    StatusCode(int code) {
    }

    public int getCode() {
        return code;
    }
}
