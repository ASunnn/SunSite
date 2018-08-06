package sunnn.sunsite.util;

public enum StatusCode {
    OJBK(0),
    ERROR(-1),
    EMPTY_UPLOAD(1 << 0),
    ILLEGAL_DATA(1 << 1),
    NO_DATA(1 << 2),
    UPLOAD_TIMEOUT(1 << 3);

    private int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Illustrator{" +
                "msg='" + this.name() + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
