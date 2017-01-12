package org.micromanager.api.FlowRidah;

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


public class FlowDialog extends javax.swing.JFrame{
    
    private FlowPlugin plugin_;
    private FlowThread FlowThread_;

    private JFrame frame;
    
    private Double z0;
    
    static Boolean Valve1state;
    static Boolean Valve2state;
    static Boolean Valve3state;
    static Boolean Valve4astate;
    static Boolean Valve5state;
    static Boolean Valve6state;
    static Boolean Valve4state;
    static Boolean USB;
    static Boolean valvePressure;
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
    static Integer pv1save;
    static Integer pv2save;
    
    static String version;
    static String valve1Address;
    static String valve2Address;
    static String valve4Address;
    static String valve4aAddress;
    static String valve3Address;
    static String valve5Address;
    static String valve6Address;
    
    private JLabel winTitle;
    private JLabel lblStatus;
    private JLabel lblFocus;
    
    private JSlider pressure1;
    private JSlider pressure2;
    private JSlider pressure3;
    private JSlider pressure4;
    private JSlider pressure5;
    private JSlider pressure6;
    private JSlider ocValve;
    
    private JComboBox selectState;
    
    private JSpinner pressure1val;
    private JSpinner pressure2val;
    private JSpinner pressure3val;
    private JSpinner pressure4val;
    private JSpinner pressure5val;
    private JSpinner pressure6val;
    private JSpinner ocValvePress;
    private JSpinner sCOMU1;
    private JSpinner sCOMU2;
    private JSpinner sCOMU3;
    private JSpinner sLED;
    
    private JTextField txtflowTime;
    private JTextField txtNumberOfImages;
    private JTextField txtExposureTime;
    private JTextField txtPauseTime;
    private JTextField txtFileName;
    private JTextField focusNum1;
    private JTextField focusNum2;
       
    private JButton btnAllOpen;
    private JButton btnAllClosed;
    private JButton btnPauseFlow;
    private JButton btnResumeFlow;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnReset;
    private JButton btnUSB1;
    private JButton btnUSB2;
    private JButton btnUSB3;
    private JButton btnValve;
    private JButton gotoFocus1;
    private JButton gotoFocus2;
    private JButton acqFocus;
    
    private JToggleButton Valve1;
    private JToggleButton Valve2;
    private JToggleButton Valve4;
    private JToggleButton Valve4a;
    private JToggleButton Valve3;
    private JToggleButton Valve5;
    private JToggleButton Valve6;
    
    
    private JCheckBox ledBOX;

    private final CMMCore core_;
    private final ScriptInterface gui_;
    private JTextField p1Field;
    private JTextField p2Field;
    private JTextField p5Field;
    private JTextField p3Field;
    private JTextField p4Field;
    private JTextField p6Field;

    public FlowDialog (ScriptInterface gui) {
        gui_ = gui;
        core_ = gui.getMMCore();
                
        Valve1state = false;
        Valve2state = false;
        Valve3state = false;
        Valve4astate = false;
        Valve5state = false;
        Valve6state = false;
        
        Valve4state = true;
        USB = true;
        valvePressure = false;
        
        pv1save = 0;
        pv2save = 0;
        
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
        
        valve1Address = "A";
        valve2Address = "B";
        valve3Address = "C";
        valve4aAddress = "D";
        valve5Address = "E";
        valve6Address = "F";
        
        //valve3Address = "C";
        valve4Address = "A";
        
        version = "Version 2.1d_U";
        
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
                Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            core_.initializeDevice(port);
            gui_.sleep(10000);
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
        setResizable(true);
        setBounds(100, 100, 591, 550);
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
        Valve4 = new JToggleButton("Valve");
        Valve4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve4state = valveToggle2(Valve4state, valve4Address);
            }
        });
                Valve4.setBounds(345, 421, 69, 29);
                getContentPane().add(Valve4);
        ocValvePress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ocValvePress.setBounds(271, 420, 64, 28);
        getContentPane().add(ocValvePress);     
                
                
//-----Title------------
                
                
        winTitle = new JLabel("Pressure and Valve Control");
        winTitle.setFont(new Font("Tahoma", Font.PLAIN, 24));
        winTitle.setHorizontalAlignment(SwingConstants.CENTER);
        winTitle.setBounds(0, 0, 335, 48);
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
//----------------------        
        pressure3 = new JSlider();
        pressure3.setValue(0);
        pressure3.setPaintTicks(true);
        pressure3.setOrientation(SwingConstants.VERTICAL);
        pressure3.setMaximum(4095);
        pressure3.setMajorTickSpacing(512);
        pressure3.setBounds(161, 86, 52, 212);
        pressure3.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = pressure3.getValue();
                pressure3val.setValue(pvalue);
                setPressure(pvalue,51);
            }
        });
        getContentPane().add(pressure3);
//----------------------        
        pressure4 = new JSlider();
        pressure4.setValue(0);
        pressure4.setPaintTicks(true);
        pressure4.setOrientation(SwingConstants.VERTICAL);
        pressure4.setMaximum(4095);
        pressure4.setMajorTickSpacing(512);
        pressure4.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = pressure4.getValue();
                pressure4val.setValue(pvalue);
                setPressure(pvalue,52);
            }
        });
        pressure4.setBounds(226, 86, 52, 212);
        getContentPane().add(pressure4); 
//----------------------           
        pressure5 = new JSlider();
        pressure5.setValue(0);
        pressure5.setPaintTicks(true);
        pressure5.setOrientation(SwingConstants.VERTICAL);
        pressure5.setMaximum(4095);
        pressure5.setMajorTickSpacing(512);
        pressure5.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = pressure5.getValue();
                pressure5val.setValue(pvalue);
                setPressure(pvalue,53);
            }
        });
        pressure5.setBounds(303, 86, 52, 212);
        getContentPane().add(pressure5);
//----------------------           
        pressure6 = new JSlider();
        pressure6.setValue(0);
        pressure6.setPaintTicks(true);
        pressure6.setOrientation(SwingConstants.VERTICAL);
        pressure6.setMaximum(4095);
        pressure6.setMajorTickSpacing(512);
        pressure6.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = pressure6.getValue();
                pressure6val.setValue(pvalue);
                setPressure(pvalue,54);
            }
        });
        pressure6.setBounds(368, 86, 52, 212);
        getContentPane().add(pressure6);    
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
        ocValve.setBounds(271, 377, 152, 52);      
        
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
//----------------------        
        pressure4val = new JSpinner();
        pressure4val.setModel(new SpinnerNumberModel(0, 0, 4095, 1));
        pressure4val.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = (Integer)pressure4val.getValue();
                pressure4.setValue(pvalue);
                setPressure(pvalue,52);
            }
        });
        pressure4val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pressure4val.setBounds(223, 303, 64, 28);
        getContentPane().add(pressure4val);
//----------------------        
        pressure3val = new JSpinner();
        pressure3val.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = (Integer)pressure3val.getValue();
                pressure3.setValue(pvalue);
                setPressure(pvalue,51);
            }
        });
        pressure3val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pressure3val.setBounds(153, 303, 64, 28);
        getContentPane().add(pressure3val);       
//----------------------        
        pressure5val = new JSpinner();
        pressure5val.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = (Integer)pressure5val.getValue();
                pressure5.setValue(pvalue);
                setPressure(pvalue,53);
            }
        });
        pressure5val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pressure5val.setBounds(295, 303, 64, 28);
        getContentPane().add(pressure5val);
//----------------------        
        pressure6val = new JSpinner();
        pressure6val.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer pvalue = (Integer)pressure6val.getValue();
                pressure6.setValue(pvalue);
                setPressure(pvalue,54);
            }
        });
        pressure6val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pressure6val.setBounds(365, 303, 64, 28);
        getContentPane().add(pressure6val);
        
        
//-----All Toggles------
        
        
        btnAllOpen = new JButton("All open");
        btnAllOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAllValves();
            }
        });                
        btnAllOpen.setBounds(463, 199, 90, 29);
        getContentPane().add(btnAllOpen);
//----------------------        
        btnAllClosed = new JButton("All closed");
        btnAllClosed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeAllValves();
            }
        });
        btnAllClosed.setBounds(463, 228, 90, 29);
        getContentPane().add(btnAllClosed);   
        
        
//-----Valve Toggles----
        
        
        Valve1 = new JToggleButton("1");
        Valve1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve1state = valveToggle(Valve1state, valve1Address);
            }
        });
        Valve1.setBounds(17, 337, 50, 29);
        getContentPane().add(Valve1);
//----------------------
        Valve2 = new JToggleButton("2");
        Valve2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve2state = valveToggle(Valve2state, valve2Address);
            }
        });
        Valve2.setBounds(86, 337, 50, 29);
        getContentPane().add(Valve2);   
//----------------------        
        Valve3 = new JToggleButton("3");
        Valve3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve3state = valveToggle(Valve3state, valve3Address);
            }
        });
        Valve3.setBounds(161, 337, 50, 29);
        getContentPane().add(Valve3);
//----------------------        
        Valve4a = new JToggleButton("4");
        Valve4a.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve4astate = valveToggle(Valve4astate, valve4aAddress);
            }
        });
        Valve4a.setBounds(230, 337, 50, 29);
        getContentPane().add(Valve4a);
//----------------------        
        Valve5 = new JToggleButton("5");
        Valve5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve5state = valveToggle(Valve5state, valve5Address);
            }
        });
        Valve5.setBounds(303, 337, 50, 29);
        getContentPane().add(Valve5);
//----------------------        
        Valve6 = new JToggleButton("6");
        Valve6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Valve6state = valveToggle(Valve6state, valve6Address);
            }
        });
        Valve6.setBounds(372, 337, 50, 29);
        getContentPane().add(Valve6);
        
        
//------Pause/Resume----
        
        
        btnPauseFlow = new JButton("Pause Flow");
        btnPauseFlow.setFont(new Font("Tahoma", Font.PLAIN, 9));
        btnPauseFlow.setBounds(463, 123, 90, 29);
        btnPauseFlow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseFlow();
            }
        });
        getContentPane().add(btnPauseFlow);
//----------------------
        btnResumeFlow = new JButton("Resume Flow");
        btnResumeFlow.setFont(new Font("Tahoma", Font.PLAIN, 9));
        btnResumeFlow.setBounds(463, 153, 90, 29);
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
        btnStart.setBounds(177, 386, 60, 28);
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
        btnStop.setBounds(177, 420, 60, 28);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                stop();
            }
        });
        getContentPane().add(btnStop);
//----------------------        
        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnReset.setBounds(513, 0, 62, 23);
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
        btnUSB1.setBounds(439, 59, 44, 23);
        btnUSB1.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                startUSB(1, (Integer)sCOMU1.getValue());
            }
        });
        getContentPane().add(btnUSB1);
          
        btnUSB2 = new JButton("U2");
        btnUSB2.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnUSB2.setBounds(483, 59, 44, 23);
        btnUSB2.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                startUSB(2, (Integer)sCOMU2.getValue());
            }
        });
        getContentPane().add(btnUSB2);
        
        btnUSB3 = new JButton("U3");
        btnUSB3.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnUSB3.setBounds(527, 59, 44, 23);
        btnUSB3.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                startUSB(3, (Integer)sCOMU3.getValue());
            }
        });
        getContentPane().add(btnUSB3);
//---------------------- 
        sCOMU1 = new JSpinner();
        sCOMU1.setModel(new SpinnerNumberModel(5, 1, 15, 1));
        sCOMU1.setBounds(440, 85, 42, 20);
        getContentPane().add(sCOMU1);

        sCOMU2 = new JSpinner();
        sCOMU2.setModel(new SpinnerNumberModel(8, 1, 15, 1));
        sCOMU2.setBounds(484, 85, 42, 20);
        getContentPane().add(sCOMU2);
        
        sCOMU3 = new JSpinner();
        sCOMU3.setBounds(528, 85, 42, 20);
        getContentPane().add(sCOMU3);
        
//-----LED Form---------
        
        
        /*sLED = new JSpinner();
        sLED.setBounds(353, 400, 30, 20);
        getContentPane().add(sLED);
//----------------------
        ledBOX = new JCheckBox("");
        ledBOX.setHorizontalAlignment(SwingConstants.CENTER);
        ledBOX.setBounds(353, 362, 30, 30);
        getContentPane().add(ledBOX);
//----------------------
        JLabel lblBlinkLed = new JLabel("Blink LED");
        lblBlinkLed.setHorizontalAlignment(SwingConstants.CENTER);
        lblBlinkLed.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBlinkLed.setBounds(283, 369, 69, 16);
        getContentPane().add(lblBlinkLed);
//----------------------        
        JLabel lblHz = new JLabel("Hz");
        lblHz.setHorizontalAlignment(SwingConstants.RIGHT);
        lblHz.setBounds(291, 401, 52, 16);
        getContentPane().add(lblHz);*/
        
        
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
      
        txtFileName = new JTextField();
        txtFileName.setText("ImageSet1");
        txtFileName.setBounds(10, 377, 90, 20);
        getContentPane().add(txtFileName);
        txtFileName.setColumns(10);
//----------------------          
        p1Field = new JTextField();
        p1Field.setText("Emulsion 1");
        p1Field.setBounds(10, 59, 59, 20);
        getContentPane().add(p1Field);
        p1Field.setColumns(10);
//----------------------          
        p2Field = new JTextField();
        p2Field.setText("Oil 1");
        p2Field.setColumns(10);
        p2Field.setBounds(75, 59, 59, 20);
        getContentPane().add(p2Field);
         
        p5Field = new JTextField();
        p5Field.setText("Oil 3");
        p5Field.setColumns(10);
        p5Field.setBounds(296, 59, 59, 20);
        getContentPane().add(p5Field);
//----------------------         
        p3Field = new JTextField();
        p3Field.setText("Emulsion 2");
        p3Field.setColumns(10);
        p3Field.setBounds(154, 59, 59, 20);
        getContentPane().add(p3Field);
//----------------------        
        p4Field = new JTextField();
        p4Field.setText("Oil 2");
        p4Field.setColumns(10);
        p4Field.setBounds(219, 59, 59, 20);
        getContentPane().add(p4Field);   
//----------------------          
        p6Field = new JTextField();
        p6Field.setText("Oil 4");
        p6Field.setColumns(10);
        p6Field.setBounds(361, 59, 59, 20);
        getContentPane().add(p6Field);
        
        
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
        lblStatus = new JLabel("Status...");
        lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        lblStatus.setBounds(2, 496, 570, 14);
        getContentPane().add(lblStatus);
        
        
//-----State Save-------  
        
        
        selectState = new JComboBox();
        selectState.setModel(new DefaultComboBoxModel(new String[] {"Pause State", "Resume State"}));
        selectState.setBounds(455, 267, 90, 23);
        getContentPane().add(selectState);
        
        JButton btnSaveState = new JButton("");
        btnSaveState.setBackground(Color.BLUE);
        btnSaveState.setBounds(548, 267, 23, 23);
        btnSaveState.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                saveState();
            }
        });
        getContentPane().add(btnSaveState);       
        getContentPane().add(ocValve);
        
        
//-----Focus------------
        
        acqFocus = new JButton("?");
        acqFocus.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                //startUSB(3, (Integer)sCOMU3.getValue());
                recordFocus();
            }
        });
        acqFocus.setBounds(483, 317, 37, 23);
        getContentPane().add(acqFocus);
        
        lblFocus = new JLabel("Current Focus: _____");
        lblFocus.setHorizontalAlignment(SwingConstants.CENTER);
        lblFocus.setBounds(439, 344, 126, 14);
        getContentPane().add(lblFocus);
        
        gotoFocus1 = new JButton("");
        gotoFocus1.setBackground(new Color(139, 0, 0));
        gotoFocus1.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                setFocus(focusNum1.getText());
            }
        });
        gotoFocus1.setBounds(460, 362, 23, 23);
        getContentPane().add(gotoFocus1);

        gotoFocus2 = new JButton("");
        gotoFocus2.setBackground(new Color(139, 0, 139));
        gotoFocus2.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                setFocus(focusNum2.getText());
            }
        });
        gotoFocus2.setBounds(460, 389, 23, 23);
        getContentPane().add(gotoFocus2);

        focusNum2 = new JTextField();
        focusNum2.setBounds(493, 392, 52, 20);
        getContentPane().add(focusNum2);
        focusNum2.setColumns(10);

        focusNum1 = new JTextField();
        focusNum1.setColumns(10);
        focusNum1.setBounds(493, 363, 52, 20);
        getContentPane().add(focusNum1);
  
    }
    
    public void saveState(){
        if (selectState.getSelectedIndex() == 0){
            pStateV1 = Valve1state;
            pStateV2 = Valve2state;
            pStateVC = Valve4state;
            pStateP1 = (Integer)pressure1val.getValue();
            pStateP2 = (Integer)pressure2val.getValue();
            pStatePC = (Integer)ocValvePress.getValue();
            displayMessage("Pause state saved. "+pStateV1+", "+pStateV2+", "+pStateVC+", "+pStateP1+", "+pStateP2+", "+pStatePC);
        }
        else {
            rStateV1 = Valve1state;
            rStateV2 = Valve2state;
            rStateVC = Valve4state;
            rStateP1 = (Integer)pressure1val.getValue();
            rStateP2 = (Integer)pressure2val.getValue();
            rStatePC = (Integer)ocValvePress.getValue();
            displayMessage("Resume state saved. "+rStateV1+", "+rStateV2+", "+rStateVC+", "+rStateP1+", "+rStateP2+", "+rStatePC);
        }
    }
    
    public Boolean valveToggle(Boolean onoff, String valve){
        if (!onoff){
            onoff = true;
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", valve+"1", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                    displayMessage("Valve "+valve+" "+onoff+". "+answer);
                } catch (Exception ex) {
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else{
            onoff = false;
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port1", valve+"0", "\r");
                    String answer = core_.getSerialPortAnswer("Port1", "\r");
                    displayMessage("Valve "+valve+" "+onoff+". "+answer);
                } catch (Exception ex) {
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return onoff;
    }
    
    public Boolean valveToggle2(Boolean onoff, String valve){
        if (!onoff){
            onoff = true;
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", valve+"0", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                    displayMessage("Valve "+valve+" "+onoff+". "+answer);
                } catch (Exception ex) {
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return onoff;
    }    
    
    public void openAllValves(){
        Valve1state=true;
        Valve2state=true;
        Valve4state=false;
        Valve1.setSelected(true);
        Valve2.setSelected(true);
        Valve4.setSelected(false);
        if (USB) {
            try {
                core_.setSerialPortCommand("Port1", "O", "\r");
                String answer = core_.getSerialPortAnswer("Port1", "\r");
                displayMessage("Valves open. "+answer);
            } catch (Exception ex) {
                Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                core_.setSerialPortCommand("Port2", "A0", "\r");
                String answer = core_.getSerialPortAnswer("Port2", "\r");
            } catch (Exception ex) {
                Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void closeAllValves(){
        Valve1state=false;
        Valve2state=false;
        Valve4state=true;
        Valve1.setSelected(false);
        Valve2.setSelected(false);
        Valve4.setSelected(true);
        if (USB) {
            try {
                core_.setSerialPortCommand("Port1", "V", "\r");
                String answer = core_.getSerialPortAnswer("Port1", "\r");
                displayMessage("Valves closed. "+answer);
            } catch (Exception ex) {
                Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                core_.setSerialPortCommand("Port2", "A1", "\r");
                String answer = core_.getSerialPortAnswer("Port2", "\r");
            } catch (Exception ex) {
                Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
        }
        if (pStateVC) {
            Valve4state = true;
            Valve4.setSelected(true);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", "A1", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
        }
        else if (!pStateVC) {
            Valve4state = false;
            Valve4.setSelected(false);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", "A0", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        setPressure(49, pStateP1);
        pressure1val.setValue(pStateP1);
        setPressure(50, pStateP2);
        pressure2val.setValue(pStateP2);
        setPressure(51, pStatePC);
        ocValvePress.setValue(pStatePC);
        
        /*closeAllValves();
        if (pressure1.getValue() != 0){pv1save=pressure1.getValue();}
        if (pressure2.getValue() != 0){pv2save=pressure2.getValue();}
        pressure1val.setValue(0);
        pressure1.setValue(0);
        pressure2val.setValue(0);
        pressure2.setValue(0);
        if (USB) {
            try {
                //core_.setSerialPortCommand("Port1", "P", "\r");
                //String answer = core_.getSerialPortAnswer("Port1", "\r");
                //displayMessage("Flow paused. "+answer);
            } catch (Exception ex) {
                //Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
        }
        if (rStateVC) {
            Valve4state = true;
            Valve4.setSelected(true);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", "A0", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
        }
        else if (!rStateVC) {
            Valve4state = false;
            Valve4.setSelected(false);
            if (USB) {
                try {
                    core_.setSerialPortCommand("Port2", "A1", "\r");
                    String answer = core_.getSerialPortAnswer("Port2", "\r");
                } catch (Exception ex) {
                    Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        setPressure(49, rStateP1);
        pressure1val.setValue(rStateP1);
        setPressure(50, rStateP2);
        pressure2val.setValue(rStateP2);
        setPressure(51, rStatePC);
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
        aaa[1] = (byte)(pvalue+1);
        aaa[2] = (byte)(contNum+0);
        byte [] abc = {aaa[2],aaa[0],aaa[1]};
        String def = null;
        try {
            def = new String(abc,"US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            core_.setSerialPortCommand("Port1", def, "\r");
            String answer = core_.getSerialPortAnswer("Port1", "\r");
            displayMessage("Pressure "+pvalue+" "+contNum+". "+answer);
        } catch (Exception ex) {
            Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        //displayMessage(""+aaa[2]+", "+aaa[0]+", "+aaa[1]);
    }

    private void stop() {
        FlowThread_.stop_ = true;
    }

    private void start() {
        Integer flowTime = Integer.parseInt(txtflowTime.getText());
        Integer pauseTime = Integer.parseInt(txtPauseTime.getText());
        Integer exposure = Integer.parseInt(txtExposureTime.getText());
        Integer numImg = Integer.parseInt(txtNumberOfImages.getText());
        String name = txtFileName.getText();
        FlowThread_ = new FlowThread(gui_, core_, this, numImg,flowTime,pauseTime,exposure, name);
        displayMessage("Starting sequence...");
        FlowThread_.start();
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
        ocValve.setValue(0);
        pv1save = 0;
        pv2save = 0;
        gui_.closeAllAcquisitions();
        txtFileName.setText("ImageSet1");
        txtPauseTime.setText("500");
        txtExposureTime.setText("100");
        txtNumberOfImages.setText("10");
        txtflowTime.setText("1000");
        displayMessage(version);
    }
    
    private void recordFocus() {
        String zstage = core_.getFocusDevice();
        try {
            z0 = core_.getPosition(zstage);
        } catch (Exception ex) {
            Logger.getLogger(FlowDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        displayFocus(""+z0);
    }
    
    private void setFocus(String focalsize) {
        try {
            gui_.setStagePosition(new Double(focalsize));
        } catch (Exception e) {
            displayMessage("Could not set focus.");
        }
    }

    private void handleError(String message) {
        JOptionPane.showMessageDialog(this, message);     
    }

    public void displayMessage(String txt) {
        lblStatus.setText(txt);
    }
    
    public void displayFocus(String txt) {
        lblFocus.setText("Focus: "+txt);
    }

    public void setPlugin(FlowPlugin plugin) {
        plugin_ = plugin;
    }
}