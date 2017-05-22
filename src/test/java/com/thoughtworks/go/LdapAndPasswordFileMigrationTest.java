package com.thoughtworks.go;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LdapAndPasswordFileMigrationTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File configDir;
    private File configFileWithLdapAndPasswordFileTag;
    private Element originalConfig;

    @Before
    public void setUp() throws Exception {
        configDir = temporaryFolder.newFolder();
        IOUtils.copy(getClass().getResourceAsStream("/go-config-before-migration-91.xml"), new FileOutputStream(new File(configDir, "go-config-before-migration-91.xml")));
        IOUtils.copy(getClass().getResourceAsStream("/go-config-before-migration-92.xml"), new FileOutputStream(new File(configDir, "go-config-before-migration-92.xml")));
        IOUtils.copy(getClass().getResourceAsStream("/cruise-config.xml"), new FileOutputStream(new File(configDir, "cruise-config.xml")));
        new LdapAndPasswordFileMigration().rollback(new GoSettings(configDir.getAbsolutePath()));
        configFileWithLdapAndPasswordFileTag = new File(configDir, "rescued-cruise-config.xml");
        File originalFile = new File(configDir, "cruise-config.xml");
        originalConfig = new SAXBuilder().build(originalFile).getRootElement();
    }

    @Test
    public void shouldMigrateLdapSettingsBackToOlderVersion() throws IOException, JDOMException {
        XPathExpression<Element> expressionForLdap = XPathFactory.instance().compile("//server/security/ldap", new ElementFilter());
        List<Element> ldapNodes = expressionForLdap.evaluate(originalConfig);
        assertThat(ldapNodes.isEmpty(), is(true));


        assertThat(configFileWithLdapAndPasswordFileTag.exists(), is(true));
        Document document = new SAXBuilder().build(configFileWithLdapAndPasswordFileTag);

        Element cruiseNode = document.getRootElement();
        ldapNodes = expressionForLdap.evaluate(cruiseNode);
        assertThat(ldapNodes.size(), is(1));
        Element ldapNode = ldapNodes.get(0);
        assertThat(ldapNode.getAttributeValue("uri"), is("ldap://ldap-server"));
        assertThat(ldapNode.getAttributeValue("managerDn"), is("manager_dn"));
        assertThat(ldapNode.getAttributeValue("managerPassword"), is("secret"));
        assertThat(ldapNode.getAttributeValue("searchFilter"), is("(sAMAccountName={0})"));
        List<Element> bases = ldapNode.getChild("bases").getChildren("base");
        assertThat(bases.get(0).getAttributeValue("value"), is("ou=ou1"));
        assertThat(bases.get(1).getAttributeValue("value"), is("ou=ou2"));
    }

    @Test
    public void shouldMigratePasswordFileSettingsBackToOlderVersion() throws IOException, JDOMException {
        XPathExpression<Element> expressionForPasswordFile = XPathFactory.instance().compile("//server/security/passwordFile", new ElementFilter());
        List<Element> passwordFileNodes = expressionForPasswordFile.evaluate(originalConfig);
        assertThat(passwordFileNodes.isEmpty(), is(true));

        Document document = new SAXBuilder().build(configFileWithLdapAndPasswordFileTag);
        Element cruiseNode = document.getRootElement();
        passwordFileNodes = expressionForPasswordFile.evaluate(cruiseNode);
        assertThat(passwordFileNodes.size(), is(1));
        Element ldapNode = passwordFileNodes.get(0);
        assertThat(ldapNode.getAttributeValue("path"), is("/etc/go/password.properties"));
    }

    @Test
    public void shouldClearExistingAuthConfigs() throws IOException, JDOMException {
        XPathExpression<Element> expressionForAuthConfigs = XPathFactory.instance().compile("//server/security/authConfigs/authConfig", new ElementFilter());
        List<Element> authConfigs = expressionForAuthConfigs.evaluate(originalConfig);
        assertThat(authConfigs.size(), is(2));

        Document document = new SAXBuilder().build(configFileWithLdapAndPasswordFileTag);

        Element cruiseNode = document.getRootElement();
        authConfigs = expressionForAuthConfigs.evaluate(cruiseNode);
        assertThat(authConfigs.isEmpty(), is(true));
    }

    @Test
    public void shouldUpdateTheSchemaVersionTo92() throws IOException, JDOMException {
        Element cruiseNode = new SAXBuilder().build(configFileWithLdapAndPasswordFileTag).getRootElement();
        assertThat(cruiseNode.getAttributeValue("schemaVersion"), is("92"));
    }
}