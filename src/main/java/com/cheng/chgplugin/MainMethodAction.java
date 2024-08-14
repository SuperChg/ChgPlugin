package com.cheng.chgplugin;

import com.cheng.analysis.ReadXmlFiles;
import com.cheng.makehtml.entry.EntryTemplate;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.List;

public class MainMethodAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();
        //从Action中得到一个虚拟文件
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (Objects.nonNull(virtualFile) && !virtualFile.isDirectory()) {
            virtualFile = virtualFile.getParent();
        }
        Module module = ModuleUtil.findModuleForFile(virtualFile, project);
        //01
        String moduleRootPath = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        //02
        String actionDir = virtualFile.getPath();
        if(!moduleRootPath.endsWith("-ui")){
            JOptionPane.showMessageDialog(null, "选择ui组件下的目录!");
            return;
        }
        SwingUtilities.invokeLater(() -> createAndShowGUI(moduleRootPath, actionDir));
    }

    private static void createAndShowGUI(String moduleRootPath, String actionDir) {
        JFrame frame = new JFrame();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton buttonEntry = new JButton("创建入口界面");
        buttonEntry.addActionListener(e -> {
            try {
                showDialogAndCreateJSON(moduleRootPath, actionDir);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        /*buttonEntry.setIcon(new ImageIcon("images/logo.png"));
        buttonEntry.setRolloverIcon(new ImageIcon("images/logo.png"));
        buttonEntry.setRolloverEnabled(true);*/
        panel.add(buttonEntry);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack(); // 自动调整大小以适应内容
        int height = frame.getHeight(); // 获取当前高度
        frame.setSize(180, height);
        frame.setVisible(true);
    }

    private static void showDialogAndCreateJSON(String moduleRootPath, String actionDir) throws IOException {
        //ComboBox<String> addressField = new ComboBox<>(new String[]{"api", "ui"}); // 示例选项
        JTextField tableNameField = new JTextField(20);
        JTextField fileNameField = new JTextField(20);
        JTextField titleNameField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        /*panel.add(new JLabel(moduleRootPath));
        panel.add(new JLabel(actionDir));*/
        panel.add(new JLabel("数据库表名:"));
        panel.add(tableNameField);
        panel.add(new JLabel("目标文件名称:"));
        panel.add(fileNameField);
        panel.add(new JLabel("界面标题:"));
        panel.add(titleNameField);
        int result = JOptionPane.showConfirmDialog(null, panel,
                "我是你的神吗o.O?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String tableName = tableNameField.getText();
            String fileName = fileNameField.getText();
            String titleName = titleNameField.getText();
            List<String> tgtTableNames = new ArrayList<>();
            tgtTableNames.add(tableName);
            List<Map<String, Object>> dbColumnList = ReadXmlFiles.analysisDatabase(moduleRootPath, tgtTableNames);
            if(dbColumnList.size()==0){
                JOptionPane.showMessageDialog(null, "该ui组件下不存在表: "+tableName);
                return;
            }
            String message = EntryTemplate.makeEntry(actionDir, fileName,titleName, tableName, dbColumnList);
            JOptionPane.showMessageDialog(null, message);
            /*try {
                JSONObject json = new JSONObject();
                Path filePath = Paths.get(address, fileName + ".json");
                Files.createDirectories(filePath.getParent());
                try (FileWriter fileWriter = new FileWriter(filePath.toString())) {
                    fileWriter.write(json.toString()); // 4 is the number of spaces for indentation
                    fileWriter.flush();
                }
                JOptionPane.showMessageDialog(null, "Creating file created successfully!!!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error creating file:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }*/
        }
    }
}
