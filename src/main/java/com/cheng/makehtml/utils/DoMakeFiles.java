package com.cheng.makehtml.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
  * @Function: 
  * @ClassName: com.cheng.makehtml.utils.DoMakeFiles
  * @SheetCode: 
  * @Description: 
  * @Author: ChengAng
  * @Date: 2024-08-12 9:25
  * @Corporation: 北京南北天地科技股份有限公司
  * @Version: 1.0
 */
public class DoMakeFiles {
    public static void makeHTML(String path, String fileName,String content) throws IOException {
        Path resourceDir = Paths.get(path);
        // 文件路径
        Path filePath = resourceDir.resolve(fileName + ".xml");
        // 确保目录存在
        if (!Files.exists(resourceDir)) {
            Files.createDirectories(resourceDir);
        }
        // 创建文件并写入内容
        Files.write(filePath, content.getBytes());
    }
}
