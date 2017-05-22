package com.thoughtworks.go;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GoConfig {
    private SAXBuilder builder;
    private Document currentConfig;
    private Element securityNode;

    public GoConfig(File configFile) throws JDOMException, IOException {
        builder = new SAXBuilder();
        currentConfig = builder.build(configFile);
        XPathExpression<Element> expressionForSecurity = XPathFactory.instance().compile("//server/security", new ElementFilter());
        securityNode = expressionForSecurity.evaluate(currentConfig.getRootElement()).get(0);
    }

    public void addNodeToSecurity(ConfigElement configElement) {
        List<Element> elements = configElement.elements();
        for (Element node : elements) {
            node.detach();
            builder.getJDOMFactory().addContent(securityNode, node);
        }
    }

    public void clearAuthConfigs() {
        securityNode.removeChildren("authConfigs");
    }

    public void writeToFile(File rescuedConfig) throws JDOMException, IOException {
        String correctContent = new XMLOutputter().outputString(currentConfig);
        FileUtils.writeByteArrayToFile(rescuedConfig, correctContent.getBytes());
    }

    public void updateSchemaVersionTo(Integer schemaVersion) {
        Element cruiseNode = currentConfig.getRootElement();
        cruiseNode.setAttribute("schemaVersion", schemaVersion.toString());
    }
}
