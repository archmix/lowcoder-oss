package lowcoder.openapi.infra;

import io.vertx.core.json.JsonObject;
import lowcoder.api.infra.HttpEndpointURIBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public interface HttpUri {
  String get();

  class Resource implements HttpUri {
    private final String uri;

    public Resource(String resource) {
      this.uri = HttpEndpointURIBuilder.create().path(resource).build();
    }

    public static Resource of(String resource) {
      return new Resource(resource);
    }

    @Override
    public String get() {
      return this.uri;
    }
  }

  class Location implements HttpUri {
    private final String uri;

    public Location(String location) {
      this.uri = location;
    }

    public static Location of(String location) {
      return new Location(location);
    }

    public String id(){
      var parts = this.uri.split("/");
      return parts[parts.length -1];
    }

    @Override
    public String get() {
      return this.uri;
    }
  }
}
