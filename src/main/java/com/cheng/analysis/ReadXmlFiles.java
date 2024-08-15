package com.cheng.analysis;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ReadXmlFiles {
    public static List<Map<String, Object>> analysisDatabase(String directoryPath, List<String> tgtTableNames) {
        //String directoryPath = "D:\\snsoft90\\hdsnsoft\\work\\wynca-code\\hd_wynca\\wynca-trd\\wynca-trd-ui";
        List<Map<String, Object>> List = new ArrayList<>();
        try {
            // 获取目录下所有以createDatabase开头的xml文件
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile) // 过滤出普通文件
                    .filter(path -> !path.toString().contains("target\\classes")) // 过滤出以.xml结尾的文件
                    .filter(path -> path.getFileName().toString().startsWith("CreateDatabase")) // 过滤出文件名以createDatabase开头的文件
                    .forEach(xmlFile -> {
                        System.out.println("Reading file: " + xmlFile);
                        try {
                            // 使用DOM解析XML文件
                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                            Document doc = dBuilder.parse(xmlFile.toFile());
                            doc.getDocumentElement().normalize();
                            // 获取所有<table>标签
                            NodeList tableNodes = doc.getElementsByTagName("table");
                            for (int i = 0; i < tableNodes.getLength(); i++) {
                                Node tableNode = tableNodes.item(i);
                                Element tableElement = (Element) tableNode;
                                String tableName = tableElement.getAttribute("name");
                                System.out.println("Table Name: " + tableName);
                                LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
                                if (tableNode.getNodeType() == Node.ELEMENT_NODE && tgtTableNames.contains(tableName)) {
                                    // 获取<table>标签下的所有<column>标签
                                    NodeList columnNodes = tableElement.getElementsByTagName("column");
                                    String sqltype;
                                    for (int j = 0; j < columnNodes.getLength(); j++) {
                                        Node columnNode = columnNodes.item(j);
                                        if (columnNode.getNodeType() == Node.ELEMENT_NODE) {
                                            Element columnElement = (Element) columnNode;
                                            String columnName = columnElement.getAttribute("name");
                                            String columnType = columnElement.getAttribute("type");
                                            if (columnType.contains("CHAR")) {
                                                sqltype = "12";
                                            } else if (columnType.contains("NUMERIC")) {
                                                sqltype = "2";
                                            } else if (columnType.contains("INTEGER")) {
                                                sqltype = "4";
                                            } else if (columnType.contains("SMALLINT")) {
                                                sqltype = "4";
                                            } else if (columnType.contains("DATE")) {
                                                if(columnName.equals("predate")||columnName.equals("modifydate")||columnName.equals("submitdate")||columnName.equals("performdate")||columnName.equals("ratifydate")){
                                                    sqltype = "93";
                                                }else{
                                                    sqltype = "91";
                                                }
                                            } else {
                                                sqltype = "12";
                                            }
                                            hashMap.put(columnName, sqltype);
                                            System.out.println("Column Name: " + columnName + ", Column Type: " + columnType);
                                        }
                                    }
                                }
                                if (hashMap.size() > 0) {
                                    List.add(hashMap);
                                    tgtTableNames.remove(tableName);
                                }
                                if (tgtTableNames.size() == 0) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List;
    }
}
