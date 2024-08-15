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
import com.intellij.ui.JBColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.List;

public class MainMethodAction extends AnAction {
    static JProgressBar progressBar;
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
        JFrame frame = new JFrame("选择创建的界面类型");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton buttonEntry = new JButton("创建入口界面");
        Dimension buttonSizeInit = new Dimension(130, 30); // 按钮的宽度和高度
        buttonEntry.addActionListener(e -> {
            try {
                showDialogAndMake(1,moduleRootPath, actionDir);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonEntry.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // 设置按钮的最大大小
                Dimension buttonSize = new Dimension(300, 150); // 按钮的宽度和高度
                // 读取图片并调整大小以完全适应按钮
                ImageIcon hoverImage = createScaledImageIcon("/images/entry.png", buttonSize.width, buttonSize.height);
                buttonEntry.setPreferredSize(buttonSize);
                buttonEntry.setMinimumSize(buttonSize);
                buttonEntry.setMaximumSize(buttonSize);
                // 当鼠标悬浮时显示缩放后的图片
                buttonEntry.setIcon(hoverImage);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                buttonEntry.setPreferredSize(buttonSizeInit);
                buttonEntry.setMinimumSize(buttonSizeInit);
                buttonEntry.setMaximumSize(buttonSizeInit);
                buttonEntry.setIcon(null);
            }
        });
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL,0,100);
        progressBar.setForeground(Color.WHITE);
        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(true);
        progressBar.setVisible(false);
        panel.add(progressBar);
        panel.add(buttonEntry);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack(); // 自动调整大小以适应内容
        int height = frame.getHeight(); // 获取当前高度
        int width = frame.getWidth(); // 获取当前宽度
        frame.setSize(width, height);
        frame.setVisible(true);
    }

    private static void showDialogAndMake(int fileType,String moduleRootPath, String actionDir) throws IOException {
        /*String[] strings = {"api", "ui"};
        ComboBox<String> addressField = new ComboBox<>(strings);*/
        JTextField tableNameField = new JTextField(20);
        JTextField fileNameField = new JTextField(20);
        JTextField titleNameField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("数据库表名:"));
        panel.add(tableNameField);
        panel.add(new JLabel("目标文件名称:"));
        panel.add(fileNameField);
        panel.add(new JLabel("界面标题:"));
        panel.add(titleNameField);
        int result = JOptionPane.showConfirmDialog(null, panel,
                "我是你的神吗o.O?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            progressBar.setVisible(true);
            progressBar.setValue(0);
            String tableName = tableNameField.getText();
            String fileName = fileNameField.getText();
            String titleName = titleNameField.getText();
            List<String> tgtTableNames = new ArrayList<>();
            tgtTableNames.add(tableName);
            progressBar.setValue(20);
            List<Map<String, Object>> dbColumnList = ReadXmlFiles.analysisDatabase(moduleRootPath, tgtTableNames);
            if(dbColumnList.size()==0){
                JOptionPane.showMessageDialog(null, "该ui组件下不存在表: "+tableName);
                return;
            }
            progressBar.setValue(40);
            String message="";
            switch (fileType) {
                case 1:
                    message = EntryTemplate.makeEntry(actionDir, fileName,titleName, tableName, dbColumnList);
                    progressBar.setValue(100);
                    break;
                case 2:
                    message = "Value is 2";
                    break;
                case 3:
                    message = "Value is 3";
                    break;
            }
            //progressBar.setVisible(false);
            JOptionPane.showMessageDialog(null, message);
            progressBar.setValue(0);
            progressBar.setVisible(false);
        }
    }
    private static ImageIcon createScaledImageIcon(String path, int buttonWidth, int buttonHeight) {
        try {
            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(Objects.requireNonNull(MainMethodAction.class.getResource(path)));
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            // 计算缩放比例
            double widthRatio = (double) buttonWidth / originalWidth;
            double heightRatio = (double) buttonHeight / originalHeight;
            double scaleFactor = Math.min(widthRatio, heightRatio); // 选择最小比例以确保图片完全显示
            // 计算缩放后的宽度和高度
            int scaledWidth = (int) (originalWidth * scaleFactor);
            int scaledHeight = (int) (originalHeight * scaleFactor);
            // 创建缩放后的图片
            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            BufferedImage bufferedScaledImage = new BufferedImage(buttonWidth, buttonHeight, BufferedImage.TYPE_INT_ARGB);
            // 将缩放后的图片绘制到新创建的图像上，保持图片居中
            Graphics2D g2d = bufferedScaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(scaledImage, (buttonWidth - scaledWidth) / 2, (buttonHeight - scaledHeight) / 2, null);
            g2d.dispose();
            return new ImageIcon(bufferedScaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
