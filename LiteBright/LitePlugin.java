package org.micromanager.api.LiteBright;


import org.micromanager.api.ScriptInterface;
import org.micromanager.api.MMPlugin;
import mmcorej.CMMCore;


public class LitePlugin implements MMPlugin {
    
    public static String menuName = "Lite Bright";
    public static String tooltipDescription = "Analysis.";
    private CMMCore core_;
    private ScriptInterface gui_;
    private LiteGUI dialog_;
    
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
        return "Streylab.com";
    }
    
    @Override
    public String getDescription() {
        return "Analysis.";
    }
    
    @Override
    public String getInfo() {
        return null;
    }
    
    @Override
    public String getVersion() {
        return "0.1";
    }
    
    @Override
    public void setApp(ScriptInterface app) {
        gui_ = app;
        core_ = app.getMMCore();
        if (dialog_ == null)
            dialog_ = new LiteGUI(gui_);
        dialog_.setVisible(true);   
    }

    public void show() {
        String ig = "Lite Bright";
    }
    
    public void run(String string) {
        //Nothing right now
    }

    
}
