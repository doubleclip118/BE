package PNUMEAT.Backend.domain.article.enums;

public enum ArticleCategory {
    NOMAL(1, "일반"),
    WAITING(2, "대기"),
    ANOUNCE(3, "공지");

    private final int code;
    private final String name;

    ArticleCategory(int code, String name) {
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
