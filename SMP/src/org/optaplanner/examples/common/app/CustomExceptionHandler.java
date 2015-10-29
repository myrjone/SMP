package org.optaplanner.examples.common.app;

import java.awt.Frame;
import javax.swing.JOptionPane;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Frame activeFrame = null;
        for (Frame frame : Frame.getFrames()) {
            if (frame.isEnabled()) {
                activeFrame = frame;
            }
        }
        if (activeFrame != null) {
            JOptionPane.showMessageDialog(activeFrame, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
