package lowcoder.openapi.infra;

import lowcoder.metadata.interfaces.Table;

public class HttpEndpointURIBuilder {
  private StringBuilder uri = new StringBuilder();

  private static final String SLASH = "/";

  public static HttpEndpointURIBuilder create() {
    return new HttpEndpointURIBuilder("v1");
  }

  public static HttpEndpointURIBuilder create(String version) {
    return new HttpEndpointURIBuilder(version);
  }

  HttpEndpointURIBuilder(String version) {
    path("api", version);
  }

  public HttpEndpointURIBuilder from(Table table) {
    return this.path(table.getName())
      .pathParam(table.getPrimaryKeys().stream().map(pk -> pk.getColumn().getName()).toArray(String[]::new));
  }

  public HttpEndpointURIBuilder path(String... paths) {
    if(paths == null) {
      return this;
    }

    for(String path : paths) {
      uri.append(SLASH).append(path);
    }

    return this;
  }

  public HttpEndpointURIBuilder pathParam(String... placeholders){
    if(placeholders == null) {
      return this;
    }

    for(String placeholder : placeholders) {
      uri.append(SLASH).append(":").append(placeholder);
    }

    return this;
  }

  public String build() {
    return uri.toString();
  }
}
