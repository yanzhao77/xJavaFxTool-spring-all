package com.xwintop.xJavaFxTool.services.developTools;

import com.xwintop.xJavaFxTool.controller.developTools.XpathConvertToolController;
import com.xwintop.xJavaFxTool.utils.SpringContextUtil;
import com.xwintop.xJavaFxTool.utils.xmlUtil.XmlFileUtils;
import com.xwintop.xJavaFxTool.utils.xmlUtil.XmlUtils;
import com.xwintop.xJavaFxTool.utils.xmlUtil.XpathHander;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: JsonConvertToolService
 * @Description: Json转换工具
 * @author: xufeng
 * @date: 2018/2/5 17:04
 */

@Getter
@Setter
@Slf4j
public class XpathConvertToolService
{
    @Resource
    Environment environment;

    private XpathConvertToolController xpathConvertToolController;

    public XpathConvertToolService(XpathConvertToolController xpathConvertToolController) {
        this.xpathConvertToolController = xpathConvertToolController;
    }

    public void xmlToXpathAction() {
        String xpathString = xpathConvertToolController.getXpathTextArea().getText();
        String allXmlString = xpathConvertToolController.getAllXmlTextArea().getText();
        String nodeXmlString = xpathConvertToolController.getNodeXmlTextArea().getText();

    }


    /**
     * 验证xpath公式是否正确
     * 查找节点
     */
    public void findXmlNodeByXpathAction() {
        String allXmlString = xpathConvertToolController.getAllXmlTextArea().getText().trim();
        String xpath = xpathConvertToolController.getXpathTextArea().getText().trim();
        if (StringUtils.isEmpty(allXmlString)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "xml 内容不能为空！");
            alert.show();
            return;
        }
        if (StringUtils.isEmpty(xpath)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "xpath 内容不能为空！");
            alert.show();
            return;
        }


        Document document = XmlFileUtils.readValueForDocument(allXmlString);
        List<Node> nodeList = XpathHander.loopDocumentsByRole(document, xpath);

        StringBuilder stringBuffer = new StringBuilder();
        for (Node node : nodeList) {
            stringBuffer.append(node.asXML().trim());
            stringBuffer.append(System.lineSeparator());
            stringBuffer.append("---------------------------------------------------------");
            stringBuffer.append(System.lineSeparator());
        }

        xpathConvertToolController.getNodeXmlTextArea().clear();
        xpathConvertToolController.getNodeXmlTextArea().setText(stringBuffer.toString());
    }

    /**
     * 生成xpath公式
     */
    public void createXpathByXmlNodeAction() {
        String allXmlString = xpathConvertToolController.getAllXmlTextArea().getText().trim();
        String nodeXmlString = xpathConvertToolController.getNodeXmlTextArea().getText().trim();
        if (StringUtils.isEmpty(allXmlString)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "xml 内容不能为空！");
            alert.show();
            return;
        }
        if (StringUtils.isEmpty(nodeXmlString)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "xml node 内容不能为空！");
            alert.show();
            return;
        }
        Document allDocument = XmlFileUtils.readValueForDocument(allXmlString);
        if (environment == null) {
            environment = SpringContextUtil.getBean(Environment.class);
        }
        String property = environment.getProperty("server.servlet.context-path");

        nodeXmlString = "<" + property + "Root>" + nodeXmlString + "</\"+property+\"Root>";
        Node nodeDocument = XmlUtils.strForDocument(nodeXmlString);
        String xpath = "";
        if (nodeDocument instanceof Document) {
            Document document = (Document) nodeDocument;
            xpath = XpathHander.accurateXpathRule(document.getRootElement().content());
        }
        xpathConvertToolController.getXpathTextArea().clear();
        xpathConvertToolController.getXpathTextArea().setText(xpath);

    }
}