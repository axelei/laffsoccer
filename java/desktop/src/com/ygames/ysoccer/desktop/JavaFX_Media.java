package com.ygames.ysoccer.desktop;
import java.io.File;  
  
import javafx.application.Application;  
import javafx.scene.Group;  
import javafx.scene.Scene;  
import javafx.scene.media.Media;  
import javafx.scene.media.MediaPlayer;  
import javafx.scene.media.MediaView;  
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.application.Application.launch;

public class JavaFX_Media extends Application
{  
  
    @Override  
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub  
        //Initialising path of the media file, replace this with your file path   
        String path = "videos/intro.mp4";
          
        //Instantiating Media class  
        Media media = new Media(new File(path).toURI().toString());  
          
        //Instantiating MediaPlayer class   
        MediaPlayer mediaPlayer = new MediaPlayer(media);  
          
        //Instantiating MediaView class   
        MediaView mediaView = new MediaView(mediaPlayer);  
          
        //setting group and scene

        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                // Add Pane to scene
                Group root = new Group();
                root.getChildren().add(mediaView);
                Scene scene = new Scene(root, media.getWidth(), media.getHeight());
                primaryStage.setScene(scene);
                mediaPlayer.play();
            }
        });

        primaryStage.show();




    }  
    public static void main(String[] args) {
        launch(args);  
    }  
      
}  