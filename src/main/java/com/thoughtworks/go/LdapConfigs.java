package com.thoughtworks.go;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LdapConfigs implements ConfigElement {

    private final List<Element> elements;

    public LdapConfigs(File configFile) throws JDOMException, IOException {
        XPathExpression<Element> expressionForLdap = XPathFactory.instance().compile("//server/security/ldap", new ElementFilter());
        SAXBuilder builder = new SAXBuilder();
        Document from91 = builder.build(configFile);
        Element configBeforeMigration91 = from91.getRootElement();
        elements = expressionForLdap.evaluate(configBeforeMigration91);
    }

    public List<Element> elements() {
        return elements;
    }

}
