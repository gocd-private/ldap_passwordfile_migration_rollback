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

public class PasswordFileConfigs implements ConfigElement {

    private final List<Element> elements;

    public PasswordFileConfigs(File configFile) throws JDOMException, IOException {
        XPathExpression<Element> expressionForPasswordFile = XPathFactory.instance().compile("//server/security/passwordFile", new ElementFilter());
        SAXBuilder builder = new SAXBuilder();
        Document from92 = builder.build(configFile);
        Element configBeforeMigration92 = from92.getRootElement();
        elements = expressionForPasswordFile.evaluate(configBeforeMigration92);
    }

    public List<Element> elements() {
        return elements;
    }
}
