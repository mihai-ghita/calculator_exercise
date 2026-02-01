package digital.metro.pricing.calculator.exception;

public class ArticleNotFoundException extends RuntimeException {

    private final String articleId;

    public ArticleNotFoundException(String articleId) {
        super(String.format("Article not found: %s", articleId));
        this.articleId = articleId;
    }

    public String getArticleId() {
        return articleId;
    }
}
