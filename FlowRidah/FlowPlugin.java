package org.micromanager.api.FlowRidah;

import org.micromanager.api.ScriptInterface;
import org.micromanager.api.MMPlugin;
import mmcorej.CMMCore;


public class FlowPlugin implements MMPlugin {
    
    public static String menuName = "Flow Controller";
    public static String tooltipDescription = "This does everything I want";
    private CMMCore core_;
    private ScriptInterface gui_;
    private FlowDialog dialog_;
    
    @Override
    public void configurationChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void dispose() {
        if (dialog_ != null) {
            dialog_.setVisible(false);
            dialog_.dispose();
            dialog_ = null;
        }
    }
    
    @Override
    public String getCopyright() {
        return "Mine, all mine.";
    }
    
    @Override
    public String getDescription() {
        return "Really, I just hope it works.";
    }
    
    @Override
    public String getInfo() {
        return null;
    }
    
    @Override
    public String getVersion() {
        return "0.0ish";
    }
    
    @Override
    public void setApp(ScriptInterface app) {
        gui_ = app;
        core_ = app.getMMCore();
        if (dialog_ == null)
            dialog_ = new FlowDialog(gui_);
        dialog_.setVisible(true);   
    }

    public void show() {
        String ig = "Flow Contoller";
    }
    
    public void run(String string) {
        //Nothing right now
    }

    
}
