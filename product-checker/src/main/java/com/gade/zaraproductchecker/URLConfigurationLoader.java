package com.gade.zaraproductchecker;

import com.gade.zaraproductchecker.model.URLConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

class URLConfigurationLoader {

    private static final URLConfiguration uRLConfiguration;

    static {
        InputStream in = URLConfigurationLoader.class.getClassLoader().getResourceAsStream("url_configuration.yml");
        Yaml yaml = new Yaml();
        uRLConfiguration = yaml.loadAs(in, URLConfiguration.class);
    }

    public static URLConfiguration get(){
        return uRLConfiguration;
    }
}
