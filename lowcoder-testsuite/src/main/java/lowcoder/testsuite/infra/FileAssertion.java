package lowcoder.testsuite.infra;

import com.google.common.io.CharStreams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RequiredArgsConstructor
@Getter
public class FileAssertion {
  private final String content;

  public static FileAssertion file(String path) {
    String resourcePath = path;
    if (!path.startsWith("/")) {
      resourcePath = new StringBuilder("/").append(path).toString();
    }

    try (InputStream resourceFile = FileAssertion.class.getResourceAsStream(resourcePath)) {
      String content = CharStreams.toString(new InputStreamReader(resourceFile));
      return new FileAssertion(content);
    } catch (IOException e) {
      throw new RuntimeException("Resource file not found at " + path);
    }
  }

  public void assertEquals(String expected) {
    Assertions.assertEquals(expected, content);
  }
}
