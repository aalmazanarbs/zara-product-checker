package com.gade.zaraproductchecker;

import com.gade.zaraproductchecker.model.UrlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

final class UrlConfigurationLoader {

    private static final UrlConfiguration urlConfiguration;

    static {
        final InputStream in = UrlConfigurationLoader.class.getClassLoader().getResourceAsStream("url_configuration.yml");
        final Yaml yaml = new Yaml();
        urlConfiguration = yaml.loadAs(in, UrlConfiguration.class);
    }

    public static UrlConfiguration get(){
        return urlConfiguration;
    }
}
