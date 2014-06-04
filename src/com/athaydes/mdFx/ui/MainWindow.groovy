package com.athaydes.mdFx.ui

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter as Filter
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.markdownj.MarkdownProcessor

import java.util.concurrent.atomic.AtomicBoolean

class MainWindow extends BorderPane {

    @FXML
    private TextArea textInput

    @FXML
    private WebView webView

    @FXML
    private VBox topBox

    final Label markdownFileName = new Label( 'Markdown file:  not set' )
    final Label htmlFileName = new Label( 'Html file:      not set' )

    private final processor = new MarkdownProcessor()
    private final htmlNeedsUpdate = new AtomicBoolean()
    private final filesNeedUpdate = new AtomicBoolean()
    private final Timer timer = new Timer( 'MdFxUpdateTasks', true )
    private TimerTask updateFilesTask
    private TimerTask updateHtmlTask
    private volatile String html = ''

    long htmlRefreshInMillis = 250
    long fileRefreshInMillis = 2_000
    boolean showFileNames = false
    volatile File markdownFile
    volatile File htmlFile

    MainWindow() {
        setHtmlRefreshInMillis htmlRefreshInMillis
        setFileRefreshInMillis fileRefreshInMillis
    }

    void setHtmlRefreshInMillis( long millis ) {
        this.htmlRefreshInMillis = millis
        updateHtmlTask?.cancel()
        updateHtmlTask = {
            if ( htmlNeedsUpdate.getAndSet( false ) ) {
                html = processor.markdown( textInput.text )
                Platform.runLater {
                    webView.engine.loadContent html
                }
            }
        } as TimerTask
        timer.schedule( updateHtmlTask, htmlRefreshInMillis, htmlRefreshInMillis )
    }

    void setFileRefreshInMillis( long millis ) {
        this.fileRefreshInMillis = millis
        updateFilesTask?.cancel()
        updateFilesTask = {
            if ( filesNeedUpdate.getAndSet( false ) ) {
                markdownFile?.write textInput.text, "UTF-8"
                htmlFile?.write html, "UTF-8"
            }
        } as TimerTask
        timer.schedule( updateFilesTask, fileRefreshInMillis, fileRefreshInMillis )
    }

    @FXML
    void textInputChanged() {
        htmlNeedsUpdate.set( true )
        filesNeedUpdate.set( true )
    }

    @FXML
    void saveHtml() {
        htmlFile = pickFile 'Where do you want to save your Html?'
        updateFileNames()
    }

    @FXML
    void saveMarkdown() {
        markdownFile = pickFile 'Where do you want to save your markdown?'
        updateFileNames()
    }

    @FXML
    void openMarkdown() {
        def picker = new FileChooser( title: 'Select the markdown file to open' )
        picker.extensionFilters.addAll(
                new Filter( 'Markdown files', '*.md' ),
                new Filter( 'All files', '*' ) )
        def file = picker.showOpenDialog( textInput.scene.window )
        if ( file ) {
            textInput.text = file.text
            markdownFile = file
            htmlNeedsUpdate.set true
            updateFileNames()
        }
    }

    @FXML
    public void settings() {
        def popup = new Stage( StageStyle.UTILITY )
        popup.with {
            x = textInput.scene.window.x
            y = textInput.scene.window.y
            modality = Modality.APPLICATION_MODAL
        }
        popup.scene = new Scene( new Settings( this ).root )
        popup.show()
    }

    void setShowFileNames( boolean show ) {
        if ( show ) {
            topBox.children << markdownFileName << htmlFileName
        } else {
            topBox.children.removeAll( markdownFileName, htmlFileName )
        }
        showFileNames = show
    }

    private File pickFile( String title ) {
        def picker = new FileChooser( title: title )
        picker.showSaveDialog( textInput.scene.window )
    }

    private void updateFileNames() {
        markdownFileName.text = 'Markdown file:   ' + ( markdownFile?.absolutePath ?: 'not set' )
        htmlFileName.text = 'Html file:       ' + ( htmlFile?.absolutePath ?: 'not set' )
    }

}
