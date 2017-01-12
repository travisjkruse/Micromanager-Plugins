package org.micromanager.api.LysisFluor;


import org.micromanager.api.ScriptInterface;
import org.micromanager.api.MMPlugin;
import mmcorej.CMMCore;


public class LysisPlugin implements MMPlugin {
    
    public static String menuName = "Lysis Control";
    public static String tooltipDescription = "Flow control for lysis analysis.";
    private CMMCore core_;
    private ScriptInterface gui_;
    private LysisGUI dialog_;
    
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
        return "Flow control for lysis analysis.";
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
            dialog_ = new LysisGUI(gui_);
        dialog_.setVisible(true);   
    }

    public void show() {
        String ig = "Lysis Control";
    }
    
    public void run(String string) {
        //Nothing right now
    }

    
}
