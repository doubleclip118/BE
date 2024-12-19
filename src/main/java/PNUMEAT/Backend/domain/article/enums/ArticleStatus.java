package PNUMEAT.Backend.domain.article.enums;

public enum ArticleStatus {
    DONE(1, "완료"),
    WAITING(2, "대기"),
    FAIL(3, "실패");

    private final int code;
    private final String name;

    ArticleStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
