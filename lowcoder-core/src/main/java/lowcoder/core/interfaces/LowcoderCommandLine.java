package lowcoder.core.interfaces;

import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class LowcoderCommandLine {
    private static final String CONFIG_OPTION = "c";

    private final String configFile;

    LowcoderCommandLine(CommandLine commandLine) {
        this.configFile = commandLine.getOptionValue(CONFIG_OPTION);
    }

    public static LowcoderCommandLine create(String[] args) {
        CommandLine commandLine = CLI.create("lowcoder-cmd")
                .addOption(new Option().setLongName(CONFIG_OPTION).setShortName(CONFIG_OPTION).setDefaultValue("conf/application.yml"))
                .parse(Arrays.asList(args));

        return new LowcoderCommandLine(commandLine);
    }
}
