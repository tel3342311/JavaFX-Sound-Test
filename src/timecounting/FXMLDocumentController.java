/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timecounting;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.SwingUtilities;
/**
 *
 * @author trdcmacpro
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    TargetDataLine mTargetDataLine;
    @FXML
    private void handleButtonAction(ActionEvent event) throws LineUnavailableException {
        System.out.println("You clicked me!");
        label.setText("Hello World2!");
        if (mTargetDataLine == null) {
//            AudioFormat audioFormat = getAudioFormat();
//            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
//            mTargetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);  

        }
        new Thread(new Recorder(label)).start();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
 
    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }
}
