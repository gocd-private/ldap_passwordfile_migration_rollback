package com.thoughtworks.go;

import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class LdapAndPasswordFileMigration {
    public static void main(String... args) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        GoSettings settings = new CommandLineParser().parse(args);
        new LdapAndPasswordFileMigration().rollback(settings);
    }

    public void rollback(GoSettings settings) throws IOException {
        try {
            LdapConfigs ldapConfigs = new LdapConfigs(new File(settings.configDir(), "go-config-before-migration-91.xml"));
            PasswordFileConfigs passwordFileConfigs = new PasswordFileConfigs(new File(settings.configDir(), "go-config-before-migration-92.xml"));
            GoConfig goConfig = new GoConfig(new File(settings.configDir(), "cruise-config.xml"));
            goConfig.addNodeToSecurity(ldapConfigs);
            goConfig.addNodeToSecurity(passwordFileConfigs);
            goConfig.clearAuthConfigs();
            goConfig.writeToFile(new File(settings.configDir(), "rescued-cruise-config.xml"));
            goConfig.updateSchemaVersionTo(92);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

