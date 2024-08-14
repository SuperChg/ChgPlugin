package com.cheng.makehtml.entry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Function:
 * @ClassName: com.cheng.makehtml.entry.EntryTemplate
 * @SheetCode:
 * @Description:
 * @Author: ChengAng
 * @Date: 2024-08-09 16:30
 * @Corporation:
 * @Version: 1.0
 */
public class EntryTemplate {
    public static String makeEntry(String path, String fileName, String tabletitle,String tableName, List<Map<String, Object>> tgtTableNames) throws IOException {
        //path="D:/snsoft90/hdsnsoft/work/wynca-code/hd_wynca/wynca-trd/wynca-trd-ui";
        /*Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("costicode","12");
        hashMap.put("costcode","12");
        tgtTableNames.add(hashMap);*/
        Path resourceDir = Paths.get(path);
        // 文件路径
        Path filePath = resourceDir.resolve(fileName + ".xml");
        // 确保目录存在
        if (!Files.exists(resourceDir)) {
            Files.createDirectories(resourceDir);
        }
        Map<String, Object> map = tgtTableNames.get(0);
        StringBuilder sb = new StringBuilder();
        map.keySet().forEach(key->{
            sb.append("\n        <c name=\"").append(key).append("\" title=\"${RES.C}\" sqltype=\"").append(map.get(key).toString()).append("\" tipIfOverflow=\"true\"/>");
        });
        String filterHtml=sb.toString();
        String content = "";
        // 创建文件并写入内容
        Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
        return ("入口界面定义创建成功!!!");
    }
}
