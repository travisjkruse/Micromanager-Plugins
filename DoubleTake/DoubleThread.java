package org.micromanager.api.DoubleTake;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import ij.gui.*;
import ij.*;
import ij.plugin.*;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import mmcorej.CMMCore;

import org.micromanager.api.ScriptInterface;

public class DoubleThread extends Thread {
    private ScriptInterface gui_;
    CMMCore core_;
    String outDirName_;
    DoubleDialog dialog_;
    boolean stop_ = false;
    int num_,flow_,pause_,expo_;
    String name_;
    String path_ = "C:/acquisitionData/";
    
    public DoubleThread(ScriptInterface gui, CMMCore core, DoubleDialog dialog, Integer numImg, Integer exposure, String fileName) {
        core_ = core;
        gui_ = gui;
        dialog_ = dialog;
        name_ = fileName;
        if (numImg==0){num_=1;}else{num_ = numImg;}
        if (exposure==0){expo_=1;}else{expo_ = exposure;}
    }
    
    public void run() {
        stop_ = false;
        int imgCount = 0;
        try {
            core_.setExposure(expo_);
            gui_.openAcquisition(name_,path_,num_,0,0,0,true,true);
            while (!stop_) {
                gui_.snapAndAddImage(name_,imgCount,0,0);
                imgCount++;
            }
            gui_.closeAllAcquisitions();
        } catch (InterruptedException e) {
            dialog_.displayMessage("Oops, that was an error.");
        } catch (Exception e) {
            dialog_.displayMessage(e.getMessage());
        }
    }
}
