package net.mnio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public final class Factory {

    private Factory() {
    }

    public final static ObjectMapper YAML_PARSER = new ObjectMapper(new YAMLFactory());
}
