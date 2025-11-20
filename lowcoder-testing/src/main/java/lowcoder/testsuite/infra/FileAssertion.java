package lowcoder.testsuite.infra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Getter
public class FileAssertion {
  private final String content;

  public static FileAssertion file(String path) {
    String resourcePath = path;
    if (!path.startsWith("/")) {
      resourcePath = "/" + path;
    }

    try (InputStream resourceFile = FileAssertion.class.getResourceAsStream(resourcePath)) {
      assert resourceFile != null;
      String content = new String(resourceFile.readAllBytes(), StandardCharsets.UTF_8);
      return new FileAssertion(content);
    } catch (IOException e) {
      throw new RuntimeException("Resource file not found at " + path);
    }
  }

  public void assertEquals(String expected) {
    Assertions.assertEquals(expected, content);
  }
}
