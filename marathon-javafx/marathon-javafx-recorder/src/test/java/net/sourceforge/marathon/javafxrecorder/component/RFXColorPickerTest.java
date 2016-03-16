package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.ColorPickerSample;
import javafx.application.Platform;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXColorPickerTest extends RFXComponentTest {

    @Test public void selectColor() {
        ColorPicker colorPicker = (ColorPicker) getPrimaryStage().getScene().getRoot().lookup(".color-picker");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXColorPicker rfxColorPicker = new RFXColorPicker(colorPicker, null, null, lr);
            colorPicker.setValue(Color.rgb(234, 156, 44));
            rfxColorPicker.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("#ea9c2c", recording.getParameters()[0]);
    }

    @Test public void colorChooserWithColorName() {
        ColorPicker colorPicker = (ColorPicker) getPrimaryStage().getScene().getRoot().lookup(".color-picker");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXColorPicker rfxColorPicker = new RFXColorPicker(colorPicker, null, null, lr);
            colorPicker.setValue(Color.RED);
            rfxColorPicker.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("#ff0000", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new ColorPickerSample();
    }
}
