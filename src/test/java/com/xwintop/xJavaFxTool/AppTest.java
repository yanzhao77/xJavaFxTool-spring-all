package com.xwintop.xJavaFxTool;


import com.xwintop.xJavaFxTool.utils.xmlUtil.XmlUtils;
import com.xwintop.xJavaFxTool.utils.xmlUtil.XpathHander;
import org.dom4j.Document;
import org.dom4j.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


/**
 * Unit test for simple App.
 */
@SpringBootTest
public class AppTest
{
    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        Assertions.assertTrue(true);
    }

    @Test
    public void xpath_Test() {
        String xml = "D:\\merge工具\\merge\\add\\out\\M1027RecMapper.xml";
//        Document document = XmlUtils.readFileForDocument(xml);
//
//        String xpath = "//* /select";
//
//        List<Node> nodeList = XpathHander.loopDocumentsByRole(document, xpath);
//        System.out.println(nodeList);
    }
}
