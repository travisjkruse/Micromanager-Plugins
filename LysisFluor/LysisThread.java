package org.micromanager.api.LysisFluor;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import mmcorej.CMMCore;

import org.micromanager.api.ScriptInterface;

public class LysisThread extends Thread {
    private ScriptInterface gui_;
    CMMCore core_;
    String outDirName_;
    LysisGUI dialog_;
    boolean stop_ = false;
    int num_,flow_,pause_;
    double expo_;
    String[] filterlist_;
    String name_;
    String path_ = "C:/acquisitionData/";
    Double z0;
    
    public LysisThread(ScriptInterface gui, CMMCore core, LysisGUI dialog, Integer numImg, Integer flowTime, Integer pauseTime, Double exposure, String fileName, String[] filterlist) {
        core_ = core;
        gui_ = gui;
        dialog_ = dialog;
        name_ = fileName;
        filterlist_ = filterlist;
        if (numImg<3){num_=3;}else{num_ = numImg;}
        if (flowTime==0){flow_=1;}else{flow_ = flowTime;}
        if (pauseTime==0){pause_=1;}else{pause_ = pauseTime;}
        if (exposure==0){expo_=1;}else{expo_ = exposure;}
    }
    
    public void run() {
        stop_ = false;
        int imgCount = 0;
        int n;
        int currentImg;
        try {
            dialog_.pauseFlow();
            core_.setExposure(expo_);
            gui_.openAcquisition(name_,path_,num_,0,0,0,true,true);
            while (imgCount < num_ && !stop_) {
                dialog_.resumeFlow();
                Thread.sleep(flow_);
                dialog_.pauseFlow();
                Thread.sleep(pause_);
                for (n = 0; n<3; n++) {
                    currentImg = imgCount*3+n;
                    dialog_.displayMessage("Image "+(currentImg+1)+" taken, of "+num_+".");
                    dialog_.setFilter(filterlist_[n]);
                    gui_.snapAndAddImage(name_,currentImg,0,0);
                }
                imgCount++;
            }
            dialog_.displayMessage("Sequence complete.");
            gui_.closeAllAcquisitions();
        } catch (InterruptedException e) {
            dialog_.displayMessage("Oops, that was an error.");
        } catch (Exception e) {
            dialog_.displayMessage(e.getMessage());
        }
    }
}
