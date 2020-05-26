package com.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * 描述： 演示JavaFX系统托盘功能
 * 作者： 老九学堂-Naaman
 * 日期： 2019/12/4 10:34
 * 版权： ©  <a href="http://www.xuetang9.com">老九学堂 </a>2006 - 2019
 * 地点： 老九学堂.成都西部国际金融中心2201
 * 版本： v1.0
 */
public class TrayIconDemo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {


        Scene scene = new Scene(new BorderPane(), 435, 329);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("系统托盘演示示例");


        //保证窗口关闭后，Stage对象仍然存活
        Platform.setImplicitExit(false);
        //构建系统托盘图标
        BufferedImage image = ImageIO.read(Objects.requireNonNull(TrayIconDemo.class.getClassLoader().getResourceAsStream("com/image/ico.png")));
        PopupMenu popup = new PopupMenu();
        MenuItem item_show = new MenuItem("show");
        MenuItem item_exit = new MenuItem("exit");
        popup.add(item_show);
        popup.add(item_exit);
        // 创建一个托盘图标 初始化需要三个参数  1-托盘图标图片 2-托盘鼠标提示信息 3-上下文菜单（可选）
        TrayIcon trayIcon = new TrayIcon(image, "演示示例", popup);
        trayIcon.setImageAutoSize(true);
        //获取系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        //添加托盘图标
        tray.add(trayIcon);
        //添加事件监听-> 托盘图标被点击
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //双击左键
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
                    Platform.runLater(() -> {
                        if (primaryStage.isIconified()) {
                            primaryStage.setIconified(false);
                        }
                        if (!primaryStage.isShowing()) {
                            primaryStage.show();
                        }
                        primaryStage.toFront();
                    });
                }
            }
        });
        //添加事件监听-> 托盘弹出菜单项被点击
        item_show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (primaryStage.isIconified()) {
                            primaryStage.setIconified(false);
                        }
                        if (!primaryStage.isShowing()) {
                            primaryStage.show();
                        }
                        primaryStage.toFront();
                    }
                });
            }
        });
        item_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Platform.setImplicitExit(true);
                        tray.remove(trayIcon);
                        Platform.runLater(primaryStage::close);
                    }
                });
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
