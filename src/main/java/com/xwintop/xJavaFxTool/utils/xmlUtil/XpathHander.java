package com.xwintop.xJavaFxTool.utils.xmlUtil;

import com.xwintop.xJavaFxTool.utils.CommonConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultComment;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanzhao
 * @version 1.0
 * @classname XpathHeler
 * @date 2022/8/31 15:41
 * @description TODO
 */
@Slf4j
public class XpathHander
{

    /**
     * 根据xpath获取节点
     *
     * @param document
     * @param expression
     * @return
     */
    public static List<Node> loopDocumentsByRole(Document document, String expression) {
        List<Node> nodeList = document.selectNodes(expression);
        XmlUtils.removeDefaultComment(nodeList);
        return nodeList;
    }

    /**
     * 根据节点生成xpath公式
     *
     * @param node
     */
    public static String forNodeCreatXpathRule(Node node) {
        String rule = "";
        if (node instanceof DefaultElement) {
            DefaultElement defaultElement = (DefaultElement) node;
            rule = "//*[@id='" + defaultElement.attribute("id").getValue() + "']";
        }
        return rule;
    }

    /**
     * 根据节点生成xpath公式
     *
     * @param nodeList
     */
    public static String accurateXpathRule(List<Node> nodeList) {
        StringBuffer rule = new StringBuffer();
        String spilt = " | ";
        List<String> stringList = new ArrayList<>();
        for (Node node : nodeList) {
            String elementPath = loopElementPath(node);
            if (!stringList.contains(elementPath)) stringList.add(elementPath);
        }
        for (String str : stringList) {
            rule.append(str).append(spilt);
        }
        rule.delete(rule.lastIndexOf(spilt), rule.length());
        return rule.toString();
    }

    /**
     * 根据节点生成xpath公式
     *
     * @param node
     */
    public static String accurateXpathRuleForEdit(Node node) {
        if (!(node instanceof DefaultElement)) {
            node = node.getParent();
        }
        return loopElementPath(node);
    }

    /**
     * 根据节点生成xpath公式
     *
     * @param node
     */
    public static String accurateXpathRuleForAdd(Node node) {
        if (node.getParent().indexOf(node) > 0) {
            for (int i = node.getParent().indexOf(node) - 1; i >= 0; i--) {
                if (node.getParent().content().get(i) instanceof DefaultText) {
                    DefaultText defaultText = (DefaultText) node.getParent().content().get(i);
                    if (StringUtils.isEmpty(defaultText.getText().trim())) {
                        continue;
                    }
                }
                if (node.getParent().content().get(i) instanceof DefaultComment) {
                    continue;
                }
                node = node.getParent().content().get(i);
                break;
            }
        }
        return loopElementPath(node);
    }


    /**
     * 获取节点从根节点到子节点的xpath公式
     *
     * @param node
     * @return
     */
    private static String loopElementPath(Node node) {
        StringBuffer rule = new StringBuffer();
        boolean flag = true;
        List<Node> nodeListPent = new ArrayList<>();
        nodeListPent.add(node);
        // 获取节点到根节点的父级节点集合
        Element parent = node.getParent();
        while (flag) {
            if (parent.getName().equals("mapper")) {
                flag = false;
            } else {
                nodeListPent.add(parent);
                parent = parent.getParent();
            }
        }

        // 获取从根节点到子节点的所有节点顺延xpath信息
        if (nodeListPent.size() > 0) {
            rule.append("//*");
            for (int i = nodeListPent.size() - 1; i >= 0; i--) {
                Node pentNode = nodeListPent.get(i);
                String spilt = " and";
                if (pentNode instanceof DefaultElement) {
                    DefaultElement defaultElement = (DefaultElement) pentNode;
                    rule.append(" /").append(defaultElement.getName()).append("[");
                    if (!StringUtils.isEmpty(defaultElement.getName()) &&
                            (defaultElement.getName().matches(CommonConst.XML_RESULT_MAP + "|" + CommonConst.XML_SELECT + "|" +
                                    CommonConst.XML_UPDATE + "|" + CommonConst.XML_INSERT + "|" + CommonConst.XML_DELETE + "|"))) {
                        rule.append(" @").append("id").append("=").append(CommonConst.CHAR_QUOTES_DOUBLE)
                                .append(defaultElement.attribute("id").getValue()).append(CommonConst.CHAR_QUOTES_DOUBLE).append(spilt);
                    } else {
                        for (Attribute attribute : defaultElement.attributes()) {
                            char quotes = attribute.getValue().contains(String.valueOf(CommonConst.CHAR_QUOTES_ONE)) ? CommonConst.CHAR_QUOTES_DOUBLE : CommonConst.CHAR_QUOTES_ONE;
                            rule.append(" @").append(attribute.getName()).append("=").append(quotes)
                                    .append(attribute.getValue()).append(quotes).append(spilt);
                        }
                    }

                    if (defaultElement.attributes().size() > 0) {
                        rule.delete(rule.lastIndexOf(spilt), rule.length());
                        rule.append("]");
                    } else {
                        rule.deleteCharAt(rule.length() - 1);
                    }
                }
            }
        } else {
            rule.append(forNodeCreatXpathRule(node));
        }
        return rule.toString();
    }


    /**
     * 获取节点的xpath公式
     *
     * @param rootElement
     * @param node
     * @param rule
     * @return
     */
    public static StringBuffer getElementPath(Element rootElement, Node node, StringBuffer rule) {
        for (int i = 0; i < rootElement.content().size(); i++) {
            Node contentNode = rootElement.content().get(i);
            if (contentNode == node) {
                rule.append("[" + i + "]");
                return rule;
            } else if (contentNode instanceof DefaultElement) {
                DefaultElement defaultElement = (DefaultElement) contentNode;
                rule.append("/").append(defaultElement.getName()).append("[");
                for (Attribute attribute : defaultElement.attributes()) {
                    rule.append("@").append(attribute).append("=").append("\"").append(attribute.getValue()).append("\"").append("|");
                }
                rule.deleteCharAt(rule.length() - 1);
                rule.append("]");
                getElementPath(defaultElement, node, rule);
            }
        }
        return rule;
    }

    /**
     * 取出 主节点id 拼接
     *
     * @param nodeList
     * @return
     */
    public static String forNodesCreatXpathRule(List<DefaultElement> nodeList) {
        StringBuffer rule = new StringBuffer();

        String split = " or";
        rule.append("//*[");
        for (int i = 0; i < nodeList.size(); i++) {
            DefaultElement defaultElement = nodeList.get(i);
            rule.append(" @id='").append(defaultElement.attribute("id").getValue()).append("'").append(split);
        }
        rule.delete(rule.lastIndexOf(split), rule.length());
        rule.append("]");
        return rule.toString();
    }


}
