package lowcoder.core.infra;

import lombok.NoArgsConstructor;
import lowcoder.config.interfaces.Config;

public class ConfigEntries {
    @NoArgsConstructor(staticName = "create")
    public static class HttpPort implements Config.Entry<Number> {
        @Override
        public String value() {
            return "http.port";
        }

        @Override
        public Number defaultValue() {
            return 8080;
        }
    }
}
