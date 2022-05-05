package lowcoder.container.interfaces;

import lowcoder.core.interfaces.LowcoderCommandLine;
import lowcoder.core.interfaces.LowcoderContainer;

public class LowcoderApplication {
    public static void main(String[] args) {
        LowcoderCommandLine cmd = LowcoderCommandLine.create(args);
        LowcoderContainer.create(cmd).start();
    }
}
