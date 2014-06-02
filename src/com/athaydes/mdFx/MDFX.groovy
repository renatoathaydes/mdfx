package com.athaydes.mdFx

import com.athaydes.mdFx.ui.MainWindow
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class MDFX {

    public static void main( String[] args ) {
        println "Hello MDFX"
        Application.launch( MDFXApp )
    }

}

class MDFXApp extends Application {

    @Override
    void start( Stage stage ) throws Exception {
        stage.title = "MDFX - MarkDown Editor"
        def root = FXMLLoader.load( MainWindow.getResource( "MainWindow.fxml" ) )
        stage.scene = new Scene( root, 800, 600 )
        stage.centerOnScreen()
        stage.show()
    }

}
