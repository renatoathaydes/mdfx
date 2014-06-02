package com.athaydes.mdFx.ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane

/**
 *
 */
class Settings extends GridPane {

    @FXML
    private TextField htmlRefreshRate

    @FXML
    private TextField fileSavingRate

    @FXML
    private CheckBox showFileNames

    final MainWindow mainWindow

    final GridPane root

    Settings( MainWindow mainWindow ) {
        this.mainWindow = mainWindow
        def fxmlLoader = new FXMLLoader( getClass().getResource( "Settings.fxml" ) )
        fxmlLoader.controller = this
        root = fxmlLoader.load()
    }

    @FXML
    void initialize() {
        htmlRefreshRate.text = mainWindow.htmlRefreshInMillis.toString()
        fileSavingRate.text = mainWindow.fileRefreshInMillis.toString()
        showFileNames.selected = mainWindow.showFileNames
    }

    @FXML
    void setHtmlRefreshRate() {
        try {
            mainWindow.htmlRefreshInMillis = htmlRefreshRate.text as long
        } catch ( e ) {
            println "Cannot make text a long: ${htmlRefreshRate.text}"
        }
    }

    @FXML
    void setFileSavingRate( ActionEvent actionEvent ) {
        try {
            mainWindow.fileRefreshInMillis = fileSavingRate.text as long
        } catch ( e ) {
            println "Cannot make text a long: ${fileSavingRate.text}"
        }
    }

    @FXML
    void setShowFileNames( ActionEvent actionEvent ) {
        mainWindow.showFileNames = showFileNames.selected
    }

}
