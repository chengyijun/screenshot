package com.abel;

import com.test.TrayIconDemo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    Stage primaryStage = null;
    double poiStartX = 0;
    double poiStartY = 0;
    double poiEndX = 0;
    double poiEndY = 0;
    BorderPane shotBorderPane = null;
    double width = 0;
    double height = 0;
    ImageView imageView = null;
    Pane pane = null;

    public Main() {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        BorderPane root = new BorderPane();
        // 往根节点添加node
        Button button = new Button("截图");
        root.setTop(button);
        pane = new Pane();
        imageView = new ImageView();
        pane.getChildren().add(imageView);
        root.setCenter(pane);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("截图工具");
        primaryStage.show();

        // 设置系统托盘
        setTrayIcon();

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                screenShot();
            }
        });


    }

    private void setTrayIcon() {
        //保证窗口关闭后，Stage对象仍然存活
        Platform.setImplicitExit(false);
        //构建系统托盘图标
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(TrayIconDemo.class.getClassLoader().getResourceAsStream("com/image/ico.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PopupMenu popup = new PopupMenu();
        MenuItem item_show = new MenuItem("show");
        MenuItem item_exit = new MenuItem("exit");
        popup.add(item_show);
        popup.add(item_exit);
        // 创建一个托盘图标 初始化需要三个参数  1-托盘图标图片 2-托盘鼠标提示信息 3-上下文菜单（可选）
        TrayIcon trayIcon = new TrayIcon(image, "子龙的截图工具", popup);
        trayIcon.setImageAutoSize(true);
        //获取系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        //添加托盘图标
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        //添加事件监听-> 托盘图标被点击
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                //双击左键
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1 && e.getClickCount() == 1) {
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
            public void actionPerformed(java.awt.event.ActionEvent e) {
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
            public void actionPerformed(java.awt.event.ActionEvent e) {
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

    private void screenShot() {
        // 先最小化主stage
        primaryStage.setIconified(true);
        AnchorPane anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        // anchorPane 半透明 遮罩效果
        anchorPane.setStyle("-fx-background:#00000055;");
        // Scene要透明
        scene.setFill(null);
        // Stage要没有窗口装饰
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();


        // 监听拖动 开始动作 仅执行一次
        anchorPane.setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                // 移除上一次的截图borderpane
                anchorPane.getChildren().remove(shotBorderPane);
                // 记录截图起始位置
                poiStartX = event.getScreenX();
                poiStartY = event.getScreenY();
                // 创建一个BorderPane作为截图区域选择框
                shotBorderPane = new BorderPane();
                anchorPane.getChildren().add(shotBorderPane);
                anchorPane.snappedLeftInset();
                anchorPane.setTopAnchor(shotBorderPane, poiStartY);
                anchorPane.setLeftAnchor(shotBorderPane, poiStartX);
            }
        });

        anchorPane.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorPane.startFullDrag();
            }
        });

        // 监听拖动过程  拖动就执行  反复执行
        anchorPane.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
//                System.out.println(event.getX() + " - " + event.getY());
                poiEndX = event.getScreenX();
                poiEndY = event.getScreenY();
                width = poiEndX - poiStartX;
                height = poiEndY - poiStartY;
                if (shotBorderPane != null) {
                    shotBorderPane.setPrefSize(width, height);
                    shotBorderPane.setStyle("-fx-background-color:transparent;");
                    shotBorderPane.setStyle("-fx-border-color:#FF0000;");
                }
            }
        });

        anchorPane.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
//                System.out.println("over");
                // 截图之前先关闭半透明遮罩 防止被截到
                stage.close();
                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                BufferedImage bufferedImage = null;
                if (robot != null) {
                    // 截图保存到磁盘
                    bufferedImage = robot.createScreenCapture(new Rectangle((int) poiStartX, (int) poiStartY, (int) width, (int) height));
                    try {
                        ImageIO.write(bufferedImage, "png", new File("C:\\Users\\abel\\Pictures\\1.png"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
                    // 截图保存到系统剪贴板
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putImage(writableImage);
                    clipboard.setContent(clipboardContent);
                    // 截图设置到ImageView
                    imageView.setImage(writableImage);
                    System.out.println("截图成功 已经保存");


                    // 保持截图长宽比 通过监听ImageView父节点Pane的宽高 动态改变ImageView的宽高
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(pane.getHeight());
                    imageView.setFitWidth(pane.getWidth());
                    if (pane != null) {
                        pane.widthProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                imageView.setFitWidth(newValue.doubleValue());

                            }
                        });
                        pane.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                imageView.setFitHeight(newValue.doubleValue());
                            }
                        });
                    }
                }
                stage.close();
                primaryStage.setIconified(false);
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                    primaryStage.setIconified(false);
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

