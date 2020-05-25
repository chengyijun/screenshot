package com.abel;

import javafx.application.Application;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                screenShot();
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
                    shotBorderPane.setStyle("-fx-border-color:#ffffffff;");
                }
            }
        });

        anchorPane.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
//                System.out.println("over");
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

