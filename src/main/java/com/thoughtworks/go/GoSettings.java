package com.thoughtworks.go;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class GoSettings {
    @Parameter(names = "-configDir", description = "Go server config dir, Usually '/etc/go/` or `C:\\program files\\Go Server\\config\\`. Provide absolute path")
    private String configDir = "./";

    @Parameter(names = "-help", help = true, description = "Print this help")
    boolean help;

    public GoSettings() {
    }

    public GoSettings(String configDir) {
        this.configDir = configDir;
    }

    public String configDir() {
        return configDir;
    }
}
