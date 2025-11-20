package lowcoder.openapi.infra;

public class MimeType {

  public static final String JSON = "application/json";

  public static final MimeType MIMETYPE_JSON = new MimeType(JSON);

  public static final String TEXT_PLAIN = "text/plain";

  public static final MimeType MIMETYPE_TEXT_PLAIN = new MimeType(TEXT_PLAIN);

  private final String value;

  public MimeType(String value) {
    super();
    this.value = value;
  }

  public String value() {
    return this.value;
  }
}