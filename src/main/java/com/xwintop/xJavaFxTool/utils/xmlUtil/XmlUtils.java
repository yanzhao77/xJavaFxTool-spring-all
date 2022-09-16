package com.xwintop.xJavaFxTool.utils.xmlUtil;

import com.xwintop.xJavaFxTool.utils.CommonConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultComment;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yanzhao
 * @version 1.0
 * @classname XmlUtils
 * @date 2022/9/7 14:01
 * @description TODO
 */
@Slf4j
public class XmlUtils
{


    /**
     * 字符串转 Dom节点
     *
     * @param value
     * @return
     */
    public static Node strForDocument(String value) {
        Node node = null;
        try {
            node = DocumentHelper.parseText(value);
        } catch (DocumentException e) {
            node = DocumentHelper.createText(value);
        }
        return node;
    }


    /**
     * 借此安
     *
     * @param node
     * @return
     */
    public static String nodeForStr(Node node) {
        return node.asXML();
    }

    /**
     * @param nodeList
     * @param elementList
     * @return
     */
    public static boolean checkList(List<Node> nodeList, List<Node> elementList) {
        for (Node node : nodeList) {
            for (Node element : elementList) {
                if (StringUtils.isEmpty(node.getText().trim())) continue;
                if (StringUtils.isEmpty(element.getText().trim())) continue;
                if (element.getText().trim().equals(node.getText().trim())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 验证节点属性是否相同
     *
     * @param defaultElement
     * @param defaultElementBefore
     * @return
     */
    public static boolean checkAttribute(Element defaultElement, Element defaultElementBefore) {
        boolean flag = true;
        Iterator<Attribute> attributeIterator = defaultElement.attributeIterator();
        while (attributeIterator.hasNext()) {
            Attribute attribute = attributeIterator.next();
            if (defaultElementBefore.attribute(attribute.getName()) != null) {
                if (!attribute.getValue().equals(defaultElementBefore.attribute(attribute.getName()).getValue())) {
                    flag = false;
                }
            }
        }
        return flag;
    }


    /**
     * 查看是否是大节点
     *
     * @param nodeList
     * @return
     */
    public static boolean checkPentElement(List<Node> nodeList) {
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            if (null != node.getName() && node.getName().matches(CommonConst.XML_RESULT_MAP + "|" + CommonConst.XML_SELECT + "|" + CommonConst.XML_UPDATE + "|" + CommonConst.XML_INSERT + "|" + CommonConst.XML_DELETE)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取到两个标签之间的节点信息
     *
     * @param element
     * @param beginMap
     * @param nodeListMap
     * @return
     */
    public static Map<Node, List<Node>> loopBeginCommentNodes(Element element, Map<String, Node> beginMap, Map<Node, List<Node>> nodeListMap) {
        for (int i = null != beginMap.get(CommonConst.BEGIN) ? element.content().indexOf(beginMap.get(CommonConst.BEGIN)) + 1 : 0; i < element.content().size(); i++) {
            Node node = element.content().get(i);
            if (node instanceof DefaultText) {
                DefaultText defaultText = (DefaultText) node;
                if (StringUtils.isEmpty(defaultText.getText().trim())) {
                    continue;
                }
            }
            if (node instanceof DefaultComment) {
                DefaultComment defaultComment = (DefaultComment) node;
                if (defaultComment.getText().contains(CommonConst.XNXZ) || defaultComment.getText().contains(CommonConst.QAXZ)) {
                    if (defaultComment.getText().contains(CommonConst.BEGIN)) {
                        beginMap.clear();
                        beginMap.put(CommonConst.BEGIN, defaultComment);
                        nodeListMap.putIfAbsent(defaultComment, new ArrayList<>());
                        continue;
                    } else if (defaultComment.getText().contains(CommonConst.END)) {
                        beginMap.put(CommonConst.END, defaultComment);
                    }
                }
            }
            if (beginMap.get(CommonConst.BEGIN) != null && element.content().contains(beginMap.get(CommonConst.BEGIN)) && beginMap.get(CommonConst.END) == null) {
                nodeListMap.get(beginMap.get(CommonConst.BEGIN)).add(node);
            }


            if (node instanceof DefaultElement) {
                DefaultElement defaultElement = (DefaultElement) node;
                if (defaultElement.content().size() > 0) {
                    nodeListMap = loopBeginCommentNodes(defaultElement, beginMap, nodeListMap);
                }
            }
        }
        return nodeListMap;
    }


    /**
     * 删除注释节点
     *
     * @param nodeList
     */
    public static void removeDefaultComment(List<Node> nodeList) {
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            if (nodeList.get(i) instanceof DefaultComment) {
                DefaultComment defaultComment = (DefaultComment) nodeList.get(i);
                nodeList.remove(defaultComment);
                if (nodeList.get(i - 1) instanceof DefaultText) {
                    DefaultText defaultText = (DefaultText) nodeList.get(i);
                    if (StringUtils.isEmpty(defaultText.getText().trim())) {
                        nodeList.remove(nodeList.get(i - 1));
                    }
                }
            } else if (nodeList.get(i) instanceof DefaultElement) {
                DefaultElement defaultElement = (DefaultElement) nodeList.get(i);
                if (defaultElement.content().size() > 0) {
                    removeDefaultComment(defaultElement.content());
                }
            }
        }
    }

    /**
     * 删除注释节点
     *
     * @param element
     * @return
     */
    public static void removeCommentElement(Element element) {
        for (int i = element.content().size() - 1; i >= 0; i--) {
            Node node = element.content().get(i);
            if (node instanceof DefaultComment) {
                DefaultComment defaultComment = (DefaultComment) node;
                element.remove(defaultComment);
            } else if (node instanceof DefaultElement) {
                DefaultElement defaultElement = (DefaultElement) node;
                removeCommentElement(defaultElement);
            }
        }
    }


    /**
     * 获取主节点
     *
     * @param node
     * @return
     */
    public static DefaultElement loopPentElement(Node node) {
        if (null != node.getName() && node.getName().matches(CommonConst.XML_RESULT_MAP + "|" + CommonConst.XML_SELECT + "|" + CommonConst.XML_UPDATE + "|" + CommonConst.XML_INSERT + "|" + CommonConst.XML_DELETE)) {
            for (int i = node.getParent().content().indexOf(node); i < node.getParent().content().size(); i++) {
                Node node1 = node.getParent().content().get(i);
                if (node1 instanceof DefaultElement) {
                    return (DefaultElement) node;
                }
            }
        } else {
            return loopPentElement(node.getParent());
        }
        return null;
    }

    /**
     * 获取前置节点（主节点）
     *
     * @param node
     * @return
     */
    public static DefaultElement getBeginElement(Node node) {
        if (node.getName().matches(CommonConst.XML_RESULT_MAP + "|" + CommonConst.XML_SELECT + "|" + CommonConst.XML_UPDATE + "|" + CommonConst.XML_INSERT + "|" + CommonConst.XML_DELETE + "|")) {
            for (int i = node.getParent().content().indexOf(node) - 1; i > 0; i--) {
                Node node1 = node.getParent().content().get(i);
                if (node1 instanceof DefaultElement) {
                    return (DefaultElement) node;
                }
            }
        } else {
            return getBeginElement(node.getParent());
        }
        return null;
    }

    /**
     * 获取 子节点的所有主节点
     *
     * @param nodeList
     * @return
     */
    public static List<DefaultElement> getPentElement(List<Node> nodeList) {
        List<DefaultElement> elementList = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            DefaultElement element = loopPentElement(nodeList.get(i));
            if (!elementList.contains(element)) {
                elementList.add(element);
            }
        }
        return elementList;
    }


    /**
     * 获取start end 之间的 contentNode
     *
     * @param node
     * @param rootElement
     * @param index
     * @return
     */
    public static List<Node> getNodes(Node node, Element rootElement, int index) {
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            Node contentNode = rootElement.content().get(i);
            if (contentNode.getText().contains(CommonConst.XNXZ) && contentNode.getText().contains(CommonConst.END)) {
                break;
            }
            nodeList.add(node);
        }
        return nodeList;
    }

    /**
     * 获取需要修改的节点(add时为前置节点)
     *
     * @param nodeList
     * @param contentNode
     */
    public static List<Node> getPentNode(List<Node> nodeList, Node contentNode) {
        List<Node> nodePentList = new ArrayList<>();

        for (int i = nodeList.indexOf(contentNode); i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            if (node instanceof DefaultElement) {
                DefaultElement defaultElement = (DefaultElement) node;
                if (defaultElement.getName().matches(CommonConst.XML_RESULT_MAP + "|" + CommonConst.XML_SELECT + "|" + CommonConst.XML_UPDATE + "|" + CommonConst.XML_INSERT + "|" + CommonConst.XML_DELETE + "|")) {
                    nodePentList.add(defaultElement);
                }
            }
        }
        if (nodeList.size() == 0) {
            return getPentNode(nodeList.get(0).getParent().content(), nodeList.get(0));
        }
        return nodePentList;
    }

    /**
     * 获取节点的
     *
     * @param nodeList
     */
    public static List<Node> loopNodeElement(List<Node> nodeList) {
        for (int i = 0; i < nodeList.size(); i++) {
            if (!(nodeList.get(i) instanceof DefaultElement)) {
                nodeList.set(i, nodeList.get(i).getParent());
            }
        }
        XmlUtils.removeDefaultComment(nodeList);
        return nodeList.stream().distinct().collect(Collectors.toList());
    }
}
