/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timecounting;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.SwingUtilities;

/**
 *
 * @author trdcmacpro
 */
public class Recorder implements Runnable {
        final Label meter;

        Recorder(final Label meter) {
            this.meter = meter;
        }

        @Override
        public void run() {
            AudioFormat fmt = new AudioFormat(44100f, 16, 1, true, false);
            final int bufferByteSize = 2048;
            Line lineMic = null;
            try {
                lineMic = getLine();
            } catch (LineUnavailableException ex) {
                Logger.getLogger(Recorder.class.getName()).log(Level.SEVERE, null, ex);
            }
            TargetDataLine line = (TargetDataLine)lineMic;
            //AudioSystem.getLine(info)lineMic
            try {
                //line = AudioSystem.getTargetDataLine(fmt);
                line.open(fmt, bufferByteSize);
            } catch(LineUnavailableException e) {
                System.err.println(e);
                return;
            }

            byte[] buf = new byte[bufferByteSize];
            float[] samples = new float[bufferByteSize / 2];

            float lastPeak = 0f;

            line.start();
            for(int b; (b = line.read(buf, 0, buf.length)) > -1;) {

                // convert bytes to samples here
                for(int i = 0, s = 0; i < b;) {
                    int sample = 0;

                    sample |= buf[i++] & 0xFF; // (reverse these two lines
                    sample |= buf[i++] << 8;   //  if the format is big endian)

                    // normalize to range of +/-1.0f
                    samples[s++] = sample / 32768f;
                }

                float rms = 0f;
                float peak = 0f;
                for(float sample : samples) {

                    float abs = Math.abs(sample);
                    if(abs > peak) {
                        peak = abs;
                    }

                    rms += sample * sample;
                }

                rms = (float)Math.sqrt(rms / samples.length);

                if(lastPeak > peak) {
                    peak = lastPeak * 0.875f;
                }

                lastPeak = peak;

                setMeterOnEDT(rms, peak);
            }
        }

        void setMeterOnEDT(final float rms, final float peak) {
            Platform.runLater(() -> {
                meter.setText("Rms : " + Math.abs(rms) * 100 + " , Peak : " + Math.abs(peak));
            });
        }
        
        private TargetDataLine getLine() throws LineUnavailableException {
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            for (Mixer.Info info : mixerInfos) {
                Mixer m = AudioSystem.getMixer(info);
                Line.Info[] lineInfos = m.getSourceLineInfo();
//                for (Line.Info lineInfo : lineInfos) {
//                    System.out.println(info.getName() + "---" + lineInfo);
//                    Line line = m.getLine(lineInfo);
//                    System.out.println("\t-----" + line);
//                }
                lineInfos = m.getTargetLineInfo();
                int i = 0;
                for (Line.Info lineInfo : lineInfos) {
                    
                    if (lineInfo.getLineClass().equals(TargetDataLine.class)) {
                        System.out.println(m + "---" + lineInfo);
                        Line line = m.getLine(lineInfo);
                        System.out.println("\t-----" + line);
                        if (i == 0) {
                            return (TargetDataLine) AudioSystem.getLine(lineInfo);
                        }
                        i++;
                    }
                }

            }
            return null;
        }
    }
