package com.ygames.ysoccer.desktop;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.ygames.ysoccer.framework.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;  
import javafx.scene.media.Media;  
import javafx.scene.media.MediaPlayer;  
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;

import static javafx.application.Application.launch;

public class JavaFXVideoPlayer extends Application
{

    private static LinkedList<String> mediaList = new LinkedList<>();
    private static String currMedia;
    private static Stage primaryStage;
    private static Settings gameSettings;
    private static boolean end = false;
    private static MediaPlayer mediaPlayer = null;

    @Override  
    public void start(Stage primaryStageParam) throws Exception {
        Platform.setImplicitExit(false);
        primaryStage = primaryStageParam;
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setFullScreen(gameSettings.fullScreen);
        primaryStage.setFullScreenExitHint(StringUtils.EMPTY);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOnCloseRequest(event -> {
            Gdx.app.exit();
            System.exit(-1);
        });
    }

    public static void halt() {
        end = true;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        primaryStage.hide();
    }

    public static void playMedia(Settings settings, String...media) {
        gameSettings = settings;
        mediaList.addAll(Arrays.asList(media));

        if (Platform.isImplicitExit()) {
            Executors.newSingleThreadExecutor().execute(() -> launch());
        }

        mediaList.forEach(actual -> {
            end = false;
            currMedia = actual;

            Media mediaObject = new Media(new File(currMedia).toURI().toString());

            mediaPlayer = new MediaPlayer(mediaObject);
            MediaView mediaView = new MediaView(mediaPlayer);

            mediaView.setStyle("-fx-background-color: #000;");

            mediaPlayer.setOnEndOfMedia(JavaFXVideoPlayer::halt);
            mediaPlayer.setOnHalted(JavaFXVideoPlayer::halt);
            mediaPlayer.setOnStopped(JavaFXVideoPlayer::halt);

            mediaPlayer.setOnReady(() -> {
                // Add Pane to scene
                Group root = new Group();
                root.setOnKeyPressed(event -> halt());
                root.setOnMouseClicked(event -> halt());

                root.getChildren().add(mediaView);

                Integer width = 1;
                Integer height = 1;

                mediaView.fitWidthProperty().bind(primaryStage.widthProperty());
                mediaView.fitHeightProperty().bind(primaryStage.heightProperty());

                if (!settings.fullScreen) {
                    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                    primaryStage.setX(primaryScreenBounds.getWidth() / 2 - mediaObject.getWidth() / 2);
                    primaryStage.setY(primaryScreenBounds.getHeight() / 2 - mediaObject.getHeight() / 2);
                }

                Scene scene = new Scene(root, mediaObject.getWidth(), mediaObject.getHeight());
                primaryStage.setScene(scene);
                mediaPlayer.play();
                primaryStage.show();
            });

            while(!end) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        mediaList.clear();

    }

}  