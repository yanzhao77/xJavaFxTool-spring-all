package com.xwintop.xJavaFxTool.utils.xmlUtil;


import com.xwintop.xJavaFxTool.utils.CommonConst;
import com.xwintop.xJavaFxTool.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yanzhao
 * @version 1.0
 * @classname FileUtils
 * @date 2022/8/16 14:44
 * @description TODO
 */
@Slf4j
public class XmlFileUtils
{
    /**
     * 读取文本文件里的xml
     *
     * @return
     * @throws Exception
     */
    public static String readXML(String filePath, String encode) throws Exception {
        return readXML(new File(filePath), encode);
    }

    /**
     * 读取文本文件里的xml
     *
     * @return
     * @throws Exception
     */
    public static String readXML(File file, String encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (file.exists() && file.isFile()) {
            InputStreamReader rd = new InputStreamReader(new FileInputStream(file), Charset.forName(encode));
            BufferedReader bufferedReader = new BufferedReader(rd);
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str).append(System.lineSeparator());
            }
            rd.close();
        }
        log.debug(sb.toString());
        return sb.toString();
    }


    /**
     * 读取xml 返回 Document
     *
     * @param xmlPath
     * @return
     */
    public static Document readFileForDocument(String xmlPath) {
        return readFileForDocument(new File(xmlPath));
    }

    /**
     * 读取xml 返回 Document
     *
     * @param xmlPath
     * @return
     */
    public static Document readFileForDocument(File xmlPath) {
        Document document = null;
        try {
            String xml = XmlFileUtils.readXML(xmlPath, CommonConst.ENCODE);
            document = readValueForDocument(xml);
            return document;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return document;
        }
    }

    /**
     * 读取xml 返回 Document
     *
     * @param xml
     * @return
     */
    public static Document readValueForDocument(String xml) {
        Document document = null;
        try {
            StringReader reader = new StringReader(xml);
            // 创建一个新的SAXBuilder
            SAXReader sb = new SAXReader();
            // 通过输入源构造一个Document
            document = sb.read(reader);
            document.setXMLEncoding(CommonConst.ENCODE);
            return document;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return document;
        }
    }


    /**
     * 字符串转输入流
     *
     * @param str
     * @return
     */
    public static InputStream getStringStream(String str) {
        if (str != null && !str.trim().equals("")) {
            try {
                return new ByteArrayInputStream(str.getBytes());
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e.getCause());
            }
        }
        return null;
    }

    /**
     * 获取文件夹下所有的文件
     *
     * @param strPath
     * @param fileList
     * @return
     */
    public static List<File> getFileList(String strPath, List<File> fileList, String lastName) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath(), fileList, lastName); // 获取文件绝对路径
                } else if (files[i].isFile() && XmlFileUtils.lastName(files[i]).equals(lastName)) { // 判断文件名
                    String strFileName = files[i].getAbsolutePath();
                    fileList.add(files[i]);
                }
            }
        } else if (dir.isDirectory()) {
            getFileList(dir.getAbsolutePath(), fileList, lastName); // 获取文件绝对路径
        } else if (dir.isFile()) { // 判断文件名
            fileList.add(dir);
        }
        return fileList;
    }

    /**
     * split截取后缀名
     *
     * @param file
     * @return
     */
    public static String lastName(File file) {
        if (file == null) return null;
        String filename = file.getName();
        // split用的是正则，所以需要用 //. 来做分隔符
        String[] split = filename.split("\\.");
        //注意判断截取后的数组长度，数组最后一个元素是后缀名
        if (split.length > 1) {
            return split[split.length - 1];
        } else {
            return "";
        }
    }

    /**
     * 写出xmlDocument
     *
     * @param document
     * @param outFilePath
     */
    public static void writeDocumentToFile(Document document, String outFilePath) {
        try {
            File file = new File(outFilePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            XMLWriter writer = new XMLWriter(Files.newBufferedWriter(Paths.get(outFilePath), Charset.forName(CommonConst.ENCODE)));
            writer.write(document);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    /**
     * 读取文件
     *
     * @param configFilepath
     * @return
     */
    public static String readFile(String configFilepath) {
        return readFile(new File(configFilepath));
    }

    /**
     * 读取文件
     *
     * @param configFile
     * @return
     */
    public static String readFile(File configFile) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader inputStreamReader = new FileReader(configFile);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        return sb.toString();
    }

    /**
     * 读取文件
     *
     * @param configFile
     * @return
     */
    public static List<String> readFileLines(File configFile) {
        List<String> stringList = new ArrayList<>();
        try {
            InputStreamReader inputStreamReader = new FileReader(configFile);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            Stream<String> lines = bufferedReader.lines();
            stringList = lines.collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        return stringList;
    }

    /**
     * 获取文件名字
     *
     * @param file
     * @return
     */
    public static String getFileOnlyName(File file) {
        String rex = ".+(?=\\.)";
        return getFileOnlyName(file.getName());
    }

    /**
     * 获取文件名字
     *
     * @param fileName
     * @return
     */
    public static String getFileOnlyName(String fileName) {
        String rex = ".+(?=\\.)";
        return StringUtil.getStringRex(fileName, rex).get(0);
    }

    /**
     * 验证文件名是否相同
     *
     * @param fileName1
     * @param fileName2
     * @return
     */
    public static boolean checkFileName(String fileName1, String fileName2) {
        return getFileOnlyName(fileName1).equals(getFileOnlyName(fileName2));
    }


    /**
     * 写出文件
     *
     * @param valueList
     * @param filePath
     */
    public static void writeToFile(List<String> valueList, String filePath) {
        log.info("write to file: <{}>; ", filePath);
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        BufferedWriter bufferedWriter = null;
        try {
            OutputStreamWriter outputStreamWriter = new FileWriter(filePath);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            for (String str : valueList) {
                bufferedWriter.write(str + System.lineSeparator());
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        } finally {
            try {
                Objects.requireNonNull(bufferedWriter).close();
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }
        }
    }

    /**
     * 写出文件
     *
     * @param value
     * @param filePath
     */
    public static void writeToFile(String filePath, String value) {
        List<String> stringList = new ArrayList<>();
        stringList.add(value);
        writeToFile(stringList, filePath);
    }

    /**
     * 复制文件
     *
     * @param source
     * @param dest
     */
    public static void copyFile(File source, File dest) {
        log.info("copy <{}> to file: <{}>; ", source, dest);
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        } finally {
            try {
                assert inputChannel != null;
                assert outputChannel != null;
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }
        }
    }
}
