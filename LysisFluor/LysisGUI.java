package org.micromanager.api.LysisFluor;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import org.micromanager.api.ScriptInterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import mmcorej.CMMCore;


public class LysisGUI extends javax.swing.JFrame{
    
    private LysisPlugin plugin_;
    private LysisThread LysisThread_;

    private JFrame frame;
    
    static Boolean Valve1state;
    static Boolean Valve2state;
    static Boolean ValveOCstate;
    static Boolean USB;
    static Boolean pStateV1;
    static Boolean rStateV1;
    static Boolean pStateV2;
    static Boolean rStateV2; 
    static Boolean pStateVC;
    static Boolean rStateVC;
    
    static Integer pStateP1;
    static Integer rStateP1;
    static Integer pStateP2;
    static Integer rStateP2;
    static Integer pStatePC;
    static Integer rStatePC;
    
    static Double z0;
    
    static String version;
    static String valve1Address;
    static String valve2Address;
    static String valveOCAddress;
    static String zstage;
    static String[] filterlist;
    static String[] activeFilters;
    static String groupname;
    
    private JLabel winTitle;
    private JLabel lblStatus;
    
    private JSlider pressure1;
    private JSlider pressure2;
    private JSlider ocValve;
    
    private JComboBox selectState;
    private JComboBox filter1;
    private JComboBox filter2;
    private JComboBox filter3;
    
    private JSpinner pressure1val;
    private JSpinner pressure2val;
    private JSpinner ocValvePress;
    private JSpinner sCOMU1;
    private JSpinner sCOMU2;
    private JSpinner sCOMU3;
    
    private JTextField txtflowTime;
    private JTextField txtNumberOfImages;
    private JTextField txtExposureTime;
    private JTextField txtPauseTime;
    private JTextField txtFileName;
    private JTextField p1Field;
    private JTextField p2Field;
    
    private JButton btnPauseFlow;
    private JButton btnResumeFlow;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnReset;
    private JButton btnUSB1;
    private JButton btnUSB2;
    private JButton btnUSB3;
    private JButton btnValve;
    private JButton acqFocus;
    private JButton acqExpos;
    private JButton switchTo1;
    private JButton switchTo2;
    private JButton switchTo3;
    
    private JToggleButton Valve1;
    private JToggleButton Valve2;
    private JToggleButton ValveOC;

    private final CMMCore core_;
    private final ScriptInterface gui_;

    public LysisGUI (ScriptInterface gui) {
        groupname = "Reflector";
        filterlist = new String[6];
        activeFilters = new String[3];
        filterlist[0] = "LED";
        filterlist[1] = "FITC";
        filterlist[2] = "Ref 3";
        filterlist[3] = "Ref 4";
        filterlist[4] = "Ref 5";
        filterlist[5] = "Texas Red";
        
        
        
        valve1Address = "A";
        valve2Address = "B";
        valveOCAddress = "A";
        
        USB = true;
        version = "Version 0.2b_U";
        
        gui_ = gui;
        core_ = gui.getMMCore();
        zstage = core_.getFocusDevice();
                
        Valve1state = false;
        Valve2state = false;
        ValveOCstate = false;
        
        pStateP1 = 0;
        pStateV1 = false;
        pStateP2 = 0;
        pStateV2 = false;
        pStatePC = 0;
        pStateVC = false;
        
        rStateP1 = 0;
        rStateV1 = false;
        rStateP2 = 0;
        rStateV2 = false;
        rStatePC = 0;
        rStateVC = false;
        
        initComponents();
        displayMessage(version);
    }

    private void startUSB(Integer deviceNum, Integer comNum) {
        displayMessage("Initializing connection...");
        String port = "Port"+deviceNum;
        String com = "COM"+comNum;
        try {
            core_.loadDevice(port, "SerialManager", com);
            core_.setProperty(port,"BaudRate","9600");
        } catch (Exception e) {
            try {
                displayMessage("Port already defined...");
                core_.unloadDevice(port);
                core_.loadDevice(port, "SerialManager", com);
                core_.setProperty(port,"BaudRate","9600");
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            core_.initializeDevice(port);
            gui_.sleep(2000);
            core_.setSerialPortCommand(port, "i", "\r");
            String answer = core_.getSerialPortAnswer(port, "\r");
            displayMessage("Connection "+deviceNum+": "+answer+"");
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void initComponents() {

        frame = new JFrame();
        setTitle("Flow Controller");
        setResizable(false);
        setBounds(100, 100, 340, 550);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
//----------------------
        ocValvePress = new JSpinner();
        ocValvePress.setModel(new SpinnerNumberModel(0, 0, 4095, 1));
        ocValvePress.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = (Integer)ocValvePress.getValue();
                ocValve.setValue(pvalue);
                setPressure(pvalue,51);
            }
        });
//----------------------
        ValveOC = new JToggleButton("Valve");
        ValveOC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ValveOCstate = valveToggle("Port2", ValveOCstate, valveOCAddress);
            }
        });
        ValveOC.setBounds(227, 102, 69, 29);
        getContentPane().add(ValveOC);
        ocValvePress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ocValvePress.setBounds(144, 101, 64, 28);
        getContentPane().add(ocValvePress);     
//-----Title------------
        winTitle = new JLabel("Lysis Flow Control");
        winTitle.setFont(new Font("Tahoma", Font.PLAIN, 24));
        winTitle.setHorizontalAlignment(SwingConstants.CENTER);
        winTitle.setBounds(0, 0, 325, 48);
        getContentPane().add(winTitle);
//-----Sliders----------
        pressure1 = new JSlider();
        pressure1.setPaintTicks(true);
        pressure1.setMajorTickSpacing(512);
        pressure1.setValue(0);
        pressure1.setMaximum(4095);
        pressure1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = pressure1.getValue();
                pressure1val.setValue(pvalue);
                setPressure(pvalue, 49);
            }
        });
        pressure1.setOrientation(SwingConstants.VERTICAL);
        pressure1.setBounds(17, 86, 52, 212);
        getContentPane().add(pressure1);
//----------------------        
        pressure2 = new JSlider();
        pressure2.setPaintTicks(true);
        pressure2.setMajorTickSpacing(512);
        pressure2.setValue(0);
        pressure2.setMaximum(4095);
        pressure2.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = pressure2.getValue();
                pressure2val.setValue(pvalue);
                setPressure(pvalue, 50);
            }
        });
        pressure2.setOrientation(SwingConstants.VERTICAL);
        pressure2.setBounds(82, 86, 52, 212);
        getContentPane().add(pressure2);    
//-----Pressure Values--
        pressure1val = new JSpinner();
        pressure1val.setModel(new SpinnerNumberModel(0, 0, 4095, 1));
        pressure1val.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = (Integer)pressure1val.getValue();
                pressure1.setValue(pvalue);
                setPressure(pvalue, 49);
                }
        });
        pressure1val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pressure1val.setBounds(9, 303, 64, 28);
        getContentPane().add(pressure1val);
//----------------------
        pressure2val = new JSpinner();
        pressure2val.setModel(new SpinnerNumberModel(0, 0, 4095, 1));
        pressure2val.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = (Integer)pressure2val.getValue();
                pressure2.setValue(pvalue);
                setPressure(pvalue,50);
            }
        });
        pressure2val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pressure2val.setBounds(79, 303, 64, 28);
        getContentPane().add(pressure2val);
//-----Valve Toggles----
        Valve1 = new JToggleButton("1");
        Valve1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve1state = valveToggle("Port1", Valve1state, valve1Address);
            }
        });
        Valve1.setBounds(17, 337, 50, 29);
        getContentPane().add(Valve1);
//----------------------
        Valve2 = new JToggleButton("2");
        Valve2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve2state = valveToggle("Port1",Valve2state, valve2Address);
            }
        });
        Valve2.setBounds(86, 337, 50, 29);
        getContentPane().add(Valve2);            
//------Pause/Resume----
        btnPauseFlow = new JButton("Pause Flow");
        btnPauseFlow.setFont(new Font("Tahoma", Font.PLAIN, 9));
        btnPauseFlow.setBounds(141, 140, 90, 29);
        btnPauseFlow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseFlow();
            }
        });
        getContentPane().add(btnPauseFlow);
//----------------------
        btnResumeFlow = new JButton("Resume Flow");
        btnResumeFlow.setFont(new Font("Tahoma", Font.PLAIN, 9));
        btnResumeFlow.setBounds(232, 140, 90, 29);
        btnResumeFlow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resumeFlow();
            }
        });
        getContentPane().add(btnResumeFlow);        
//-----Start/Stop/Reset-
        btnStart = new JButton("Start");
        btnStart.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnStart.setBackground(new Color(0, 255, 0));
        btnStart.setBounds(191, 354, 60, 28);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                start();
            }
        });
        getContentPane().add(btnStart);
//----------------------
        btnStop = new JButton("Stop");
        btnStop.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnStop.setBackground(Color.RED);
        btnStop.setBounds(259, 354, 60, 28);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                stop();
            }
        });
        getContentPane().add(btnStop);
//----------------------        
        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnReset.setBounds(255, 418, 62, 23);
        getContentPane().add(btnReset);
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                reset();
            }
        });
        getContentPane().add(btnReset);

        
//-----COM Selection----
       
        btnUSB1 = new JButton("U1");
        btnUSB1.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnUSB1.setBounds(187, 443, 44, 23);
        btnUSB1.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                startUSB(1, (Integer)sCOMU1.getValue());
            }
        });
        getContentPane().add(btnUSB1);
          
        btnUSB2 = new JButton("U2");
        btnUSB2.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnUSB2.setBounds(231, 443, 44, 23);
        btnUSB2.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                startUSB(2, (Integer)sCOMU2.getValue());
            }
        });
        getContentPane().add(btnUSB2);
        
        btnUSB3 = new JButton("U3");
        btnUSB3.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnUSB3.setBounds(275, 443, 44, 23);
        btnUSB3.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                startUSB(3, (Integer)sCOMU3.getValue());
            }
        });
        getContentPane().add(btnUSB3);
//---------------------- 
        sCOMU1 = new JSpinner();
        sCOMU1.setModel(new SpinnerNumberModel(5, 1, 15, 1));
        sCOMU1.setBounds(188, 469, 42, 20);
        getContentPane().add(sCOMU1);

        sCOMU2 = new JSpinner();
        sCOMU2.setModel(new SpinnerNumberModel(8, 1, 15, 1));
        sCOMU2.setBounds(232, 469, 42, 20);
        getContentPane().add(sCOMU2);
        
        sCOMU3 = new JSpinner();
        sCOMU3.setBounds(276, 469, 42, 20);
        getContentPane().add(sCOMU3);
//-----Text Fields------
        txtflowTime = new JTextField();
        txtflowTime.setText("1000");
        txtflowTime.setBounds(10, 423, 44, 20);
        getContentPane().add(txtflowTime);
        txtflowTime.setColumns(10);
//----------------------
        txtNumberOfImages = new JTextField();
        txtNumberOfImages.setText("10");
        txtNumberOfImages.setBounds(10, 400, 44, 20);
        getContentPane().add(txtNumberOfImages);
        txtNumberOfImages.setColumns(10);        
//----------------------
        txtExposureTime = new JTextField();
        txtExposureTime.setText("100");
        txtExposureTime.setBounds(10, 469, 44, 20);
        getContentPane().add(txtExposureTime);
        txtExposureTime.setColumns(10);
//----------------------
        txtPauseTime = new JTextField();
        txtPauseTime.setText("500");
        txtPauseTime.setBounds(10, 446, 44, 20);
        getContentPane().add(txtPauseTime);
        txtPauseTime.setColumns(10);
//----------------------        
        txtFileName = new JTextField();
        txtFileName.setText("ImageSet1");
        txtFileName.setBounds(10, 377, 90, 20);
        getContentPane().add(txtFileName);
        txtFileName.setColumns(10);
//-----Labels----------- 
        JLabel lblNumberOfImages = new JLabel("Number of Images");
        lblNumberOfImages.setHorizontalAlignment(SwingConstants.LEFT);
        lblNumberOfImages.setBounds(64, 400, 112, 20);
        getContentPane().add(lblNumberOfImages);
//---------------------- 
        JLabel lblflowTime = new JLabel("Flow Time (ms)");
        lblflowTime.setHorizontalAlignment(SwingConstants.LEFT);
        lblflowTime.setBounds(64, 423, 112, 20);
        getContentPane().add(lblflowTime);        
//---------------------- 
        JLabel lblExposureTimes = new JLabel("Exposure Time (ms)");
        lblExposureTimes.setHorizontalAlignment(SwingConstants.LEFT);
        lblExposureTimes.setBounds(64, 472, 108, 14);
        getContentPane().add(lblExposureTimes);
//---------------------- 
        JLabel lblPauseTime = new JLabel("Pause Time (ms)");
        lblPauseTime.setHorizontalAlignment(SwingConstants.LEFT);
        lblPauseTime.setBounds(64,449,78,14);
        getContentPane().add(lblPauseTime);
//---------------------- 
        JLabel lblFileName = new JLabel("File Name");
        lblFileName.setHorizontalAlignment(SwingConstants.LEFT);
        lblFileName.setBounds(110, 380, 62, 14);
        getContentPane().add(lblFileName);
//----------------------         
        JLabel lblImage = new JLabel("Image 1");
        lblImage.setBounds(152, 208, 46, 14);
        getContentPane().add(lblImage);
//---------------------- 
        JLabel lblImage_1 = new JLabel("Image 2");
        lblImage_1.setBounds(152, 232, 46, 14);
        getContentPane().add(lblImage_1);
//---------------------- 
        JLabel lblImage_2 = new JLabel("Image 3");
        lblImage_2.setBounds(152, 255, 46, 14);
        getContentPane().add(lblImage_2);
//---------------------- 
        lblStatus = new JLabel("Status...");
        lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        lblStatus.setBounds(2, 505, 330, 14);
        getContentPane().add(lblStatus);
//-----State Save-------       
        selectState = new JComboBox();
        selectState.setModel(new DefaultComboBoxModel(new String[] {"Pause State", "Resume State"}));
        selectState.setBounds(156, 173, 90, 23);
        getContentPane().add(selectState);
        
        JButton btnSaveState = new JButton("");
        btnSaveState.setBackground(Color.BLUE);
        btnSaveState.setBounds(254, 173, 23, 23);
        btnSaveState.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                saveState();
            }
        });
        getContentPane().add(btnSaveState);       
        
        p1Field = new JTextField();
        p1Field.setText("Emulsion");
        p1Field.setBounds(10, 59, 59, 20);
        getContentPane().add(p1Field);
        p1Field.setColumns(10);
        
        p2Field = new JTextField();
        p2Field.setText("Oil");
        p2Field.setColumns(10);
        p2Field.setBounds(75, 59, 59, 20);
        getContentPane().add(p2Field);
        //----------------------        
        ocValve = new JSlider();
        ocValve.setPaintTicks(true);
        ocValve.setMajorTickSpacing(512);
        ocValve.setValue(0);
        ocValve.setMaximum(4095);
        ocValve.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = ocValve.getValue();
                ocValvePress.setValue(pvalue);
                setPressure(pvalue, 51);
            }
        });
        ocValve.setBounds(144, 50, 174, 52);
        getContentPane().add(ocValve);

        acqFocus = new JButton("Focus: ___");
        acqFocus.setBounds(155, 285, 120, 23);
        acqFocus.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                try {
                    z0 = core_.getPosition(zstage);
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                acqFocus.setText("Focus: " + z0);
            }
        });
        getContentPane().add(acqFocus);
        
        acqExpos = new JButton("Exposure: ___");
        acqExpos.setBounds(155, 311, 120, 23);
        acqExpos.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                try {
                    z0 = core_.getExposure();
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                acqExpos.setText("Exposure: " + z0);
            }
        });
        getContentPane().add(acqExpos);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        separator.setBounds(137, 133, 182, 7);
        getContentPane().add(separator);

        JSeparator separator_1 = new JSeparator();
        separator_1.setForeground(Color.BLACK);
        separator_1.setBounds(137, 280, 182, 7);
        getContentPane().add(separator_1);


        filter1 = new JComboBox();
        filter1.setBounds(197, 206, 112, 20);
        filter1.setModel(new DefaultComboBoxModel(new String[] {filterlist[0],filterlist[1],filterlist[2],filterlist[3],filterlist[4],filterlist[5]}));
        getContentPane().add(filter1);

        filter2 = new JComboBox();
        filter2.setBounds(197, 229, 112, 20);
        filter2.setModel(new DefaultComboBoxModel(new String[] {filterlist[0],filterlist[1],filterlist[2],filterlist[3],filterlist[4],filterlist[5]}));
        getContentPane().add(filter2);

        filter3 = new JComboBox();
        filter3.setBounds(197, 252, 112, 20);
        filter3.setModel(new DefaultComboBoxModel(new String[] {filterlist[0],filterlist[1],filterlist[2],filterlist[3],filterlist[4],filterlist[5]}));
        getContentPane().add(filter3);
        
        switchTo1 = new JButton("");
        switchTo1.setBounds(310, 208, 15, 15);
        switchTo1.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                setFilter(filter1.getSelectedItem().toString());
            }
        });
        getContentPane().add(switchTo1);
                
        switchTo2 = new JButton("");
        switchTo2.setBounds(310, 231, 15, 15);
        switchTo2.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                setFilter(filter2.getSelectedItem().toString());
            }
        });
        getContentPane().add(switchTo2);
                
        switchTo3 = new JButton("");
        switchTo3.setBounds(310, 255, 15, 15);
        switchTo3.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                setFilter(filter3.getSelectedItem().toString());
            }
        });        
        getContentPane().add(switchTo3);
        
    }
    
    public void saveState(){
        if (selectState.getSelectedIndex() == 0){
            pStateV1 = Valve1state;
            pStateV2 = Valve2state;
            pStateVC = ValveOCstate;
            pStateP1 = (Integer)pressure1val.getValue();
            pStateP2 = (Integer)pressure2val.getValue();
            pStatePC = (Integer)ocValvePress.getValue();
            displayMessage("Pause state saved. "+pStateV1+", "+pStateV2+", "+pStateVC+", "+pStateP1+", "+pStateP2+", "+pStatePC);
        }
        else {
            rStateV1 = Valve1state;
            rStateV2 = Valve2state;
            rStateVC = ValveOCstate;
            rStateP1 = (Integer)pressure1val.getValue();
            rStateP2 = (Integer)pressure2val.getValue();
            rStatePC = (Integer)ocValvePress.getValue();
            displayMessage("Resume state saved. "+rStateV1+", "+rStateV2+", "+rStateVC+", "+rStateP1+", "+rStateP2+", "+rStatePC);
        }
    }
    
    public Boolean valveToggle(String port, Boolean onoff, String valve){
        if (!onoff){
            onoff = true;
            if (USB) {
                try {
                    core_.setSerialPortCommand(port, valve+"1", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                    displayMessage("Valve "+valve+" "+onoff+". "+answer);
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else{
            onoff = false;
            if (USB) {
                try {
                    core_.setSerialPortCommand(port, valve+"0", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                    displayMessage("Valve "+valve+" "+onoff+". "+answer);
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return onoff;
    }
    
    /*public Boolean valveToggle2(Boolean onoff, String valve){
        if (!onoff){
            onoff = true;
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", valve+"0", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                    displayMessage("Valve "+valve+" "+onoff+". "+answer);
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else{
            onoff = false;
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", valve+"1", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                    displayMessage("Valve "+valve+" "+onoff+". "+answer);
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return onoff;
    }  */  
    
    public void closeAllValves(){
        Valve1state=false;
        Valve2state=false;
        ValveOCstate=true;
        Valve1.setSelected(false);
        Valve2.setSelected(false);
        ValveOC.setSelected(true);
        if (USB) {
            try {
                core_.setSerialPortCommand("Port1", "V", "\r");
                String answer = core_.getSerialPortAnswer("Port1", "\r");
                displayMessage("Valves closed. "+answer);
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                core_.setSerialPortCommand("Port2", "A1", "\r");
                String answer = core_.getSerialPortAnswer("Port2", "\r");
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void pauseFlow(){
        if (pStateV1 == true) {
            Valve1state = true;
            Valve1.setSelected(true);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", "A1", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        else if (pStateV1 == false) {
            Valve1state = false;
            Valve1.setSelected(false);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", "A0", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }      
            }
        }
        if (pStateV2) {
            Valve2state = true;
            Valve1.setSelected(true);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", "B1", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        else if (!pStateV2) {
            Valve2state = false;
            Valve2.setSelected(false);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", "B0", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
        }
        if (pStateVC) {
            ValveOCstate = true;
            ValveOC.setSelected(true);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", "A1", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
        }
        else if (!pStateVC) {
            ValveOCstate = false;
            ValveOC.setSelected(false);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", "A0", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        setPressure(pStateP1, 49);
        pressure1val.setValue(pStateP1);
        setPressure(pStateP2, 50);
        pressure2val.setValue(pStateP2);
        setPressure(pStatePC, 51);
        ocValvePress.setValue(pStatePC);

    }   
    
    public void resumeFlow(){
        if (rStateV1 == true) {
            Valve1state = true;
            Valve1.setSelected(true);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", "A1", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
        }
        else if (rStateV1 == false) {
            Valve1state = false;
            Valve1.setSelected(false);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", "A0", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        if (rStateV2) {
            Valve2state = true;
            Valve2.setSelected(true);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", "B1", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }  
            }
        }
        else if (!rStateV2) {
            Valve2state = false;
            Valve2.setSelected(false);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", "B0", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
        }
        if (rStateVC) {
            ValveOCstate = true;
            ValveOC.setSelected(true);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", "A1", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
        }
        else if (!rStateVC) {
            ValveOCstate = false;
            ValveOC.setSelected(false);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", "A0", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        setPressure(rStateP1, 49);
        pressure1val.setValue(rStateP1);
        setPressure(rStateP2, 50);
        pressure2val.setValue(rStateP2);
        setPressure(rStatePC, 51);
        ocValvePress.setValue(rStatePC);
       
        //openAllValves();
        //pressure1val.setValue(pv1save);
        //pressure1.setValue(pv1save);
        //pressure2val.setValue(pv2save);
        //pressure2.setValue(pv2save);
        //if (USB) {
            //try {
                //core_.setSerialPortCommand("Port1", "Q", "\r");
                //String answer = core_.getSerialPortAnswer("Port1", "\r");
                //displayMessage("Flow resumed. "+answer);
            //} catch (Exception ex) {
                //Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
            //}
        //}
    }
    
    public void setPressure(Integer pvalue, Integer contNum){
        byte[] aaa = new byte[3];
        aaa[0] = (byte)((pvalue >> 6)+1);
        aaa[1] = (byte)((pvalue & 0x3f) +1);
        aaa[2] = (byte)(contNum+0);
        byte [] abc = {aaa[2],aaa[0],aaa[1]};
        String def = null;
        try {
            def = new String(abc,"US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            core_.setSerialPortCommand("Port1", def, "\r");
            String answer = core_.getSerialPortAnswer("Port1", "\r");
            displayMessage("Pressure "+pvalue+" "+contNum+". "+answer);
        } catch (Exception ex) {
            Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        //displayMessage(""+aaa[2]+", "+aaa[0]+", "+aaa[1]);
    }

    public void setFilter(String filtername){
        if (filtername.equals("LED")) {
            try {
                //Set LED ON
                //Set focus
                core_.setConfig("Shutter", "Closed");
                core_.setConfig("Reflector", "Ref 3");
                core_.waitForConfig("Reflector", "Ref 3");
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (filtername.equals("Texas Red")) {
            try {
                //Set LED OFF
                //Set focus
                core_.setConfig("Shutter", "Open");
                core_.setConfig("Reflector", "Texas Red");
                core_.waitForConfig("Reflector", "Texas Red");
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (filtername.equals("FITC")) {
            try {
                //Set LED OFF
                //Set focus
                core_.setConfig("Shutter", "Open");
                core_.setConfig("Reflector", "FITC");
                core_.waitForConfig("Reflector", "FITC");
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (filtername.equals("Ref 3")) {
            try {
                //Set LED OFF
                //Set focus
                core_.setConfig("Shutter", "Open");
                core_.setConfig("Reflector", "Ref 3");
                core_.waitForConfig("Reflector", "Ref 3");
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (filtername.equals("Ref 4")) {
            try {
                //Set LED OFF
                //Set focus
                core_.setConfig("Shutter", "Open");
                core_.setConfig("Reflector", "Ref 4");
                core_.waitForConfig("Reflector", "Ref 4");
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (filtername.equals("Ref 5")) {
            try {
                //Set LED OFF
                //Set focus
                core_.setConfig("Shutter", "Open");
                core_.setConfig("Reflector", "Ref 5");
                core_.waitForConfig("Reflector", "Ref 5");
            } catch (Exception ex) {
                Logger.getLogger(LysisGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    private void stop() {
        LysisThread_.stop_ = true;
    }

    private void start() {
        Integer flowTime = Integer.parseInt(txtflowTime.getText());
        Integer pauseTime = Integer.parseInt(txtPauseTime.getText());
        Double exposure = Double.parseDouble(txtExposureTime.getText());
        Integer numImg = Integer.parseInt(txtNumberOfImages.getText());
        numImg = numImg*3;
        String name = txtFileName.getText();
        activeFilters[0] = filter1.getSelectedItem().toString();
        activeFilters[1] = filter2.getSelectedItem().toString();
        activeFilters[2] = filter3.getSelectedItem().toString();       
        LysisThread_ = new LysisThread(gui_, core_, this, numImg,flowTime,pauseTime,exposure, name, activeFilters);
        displayMessage("Starting sequence...");
        LysisThread_.start();
    }
    
    private void reset(){
        closeAllValves();
        setPressure(0,49);
        setPressure(0,50);
        setPressure(0,51);
        setPressure(0,52);
        pressure1val.setValue(0);
        pressure1.setValue(0);
        pressure2val.setValue(0);
        pressure2.setValue(0);
        ocValvePress.setValue(0);
        ocValve.setValue(0);
        gui_.closeAllAcquisitions();
        txtFileName.setText("ImageSet1");
        txtPauseTime.setText("500");
        txtExposureTime.setText("100");
        txtNumberOfImages.setText("10");
        txtflowTime.setText("1000");
        displayMessage(version);
    }

    private void handleError(String message) {
        JOptionPane.showMessageDialog(this, message);     
    }

    public void displayMessage(String txt) {
        lblStatus.setText(txt);
    }

    public void setPlugin(LysisPlugin plugin) {
        plugin_ = plugin;
    }
}