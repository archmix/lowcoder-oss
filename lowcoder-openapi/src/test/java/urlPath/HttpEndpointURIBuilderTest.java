package urlPath;

import lowcoder.openapi.infra.HttpEndpointURIBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class HttpEndpointURIBuilderTest {
  @Test
  public void givenSomePathThenResolveURI(){
    String actualPath = HttpEndpointURIBuilder.create().path("a").build();
    String expectedPath = "/api/v1/a";

    Assert.assertEquals(expectedPath, actualPath);
  }

  @Test
  public void givenSomePathParamsThenResolveURI(){
    String actualPath = HttpEndpointURIBuilder.create().path("a", "b").pathParam("c").build();
    String expectedPath = "/api/v1/a/b/:c";

    Assert.assertEquals(expectedPath, actualPath);
  }

  @Test
  public void givenSomePathAndPathParamsThenResolveURI(){
    String actualPath = HttpEndpointURIBuilder.create().path("a", "b", "c").pathParam("d", "e", "f").build();
    String expectedPath = "/api/v1/a/b/c/:d/:e/:f";

    Assert.assertEquals(expectedPath, actualPath);
  }

  @Test
  public void givenSomePathAndPathParamsAndVersionThenResolveURI(){
    String actualPath = HttpEndpointURIBuilder.create("v2").path("a", "b", "c").pathParam("d", "e", "f").build();
    String expectedPath = "/api/v2/a/b/c/:d/:e/:f";

    Assert.assertEquals(expectedPath, actualPath);
  }
}
