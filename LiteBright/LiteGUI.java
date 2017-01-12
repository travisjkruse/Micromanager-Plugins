package org.micromanager.api.LiteBright;

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


public class LiteGUI extends javax.swing.JFrame{
    
    private LitePlugin plugin_;

    private JFrame frame;
    
    static Boolean USB;
    static Boolean isConnected;
    static Boolean isOn;
    
    static Integer deviceNumber;
    
    static long goTime;
    
    static String version;
    
    private JSpinner spinner_Exposure;
    private JSpinner spinner_Delay;
    private JSpinner spinner_Every;
    private JSpinner spinner_COM;
    
    private JSlider slider_Exposure;
    private JSlider slider_Delay;
    private JSlider slider_Every;
    
    private JButton btnInitialize;
    private JButton btnInitiateBlinking;
    
    private JLabel lblStatus_1;
    private JLabel winTitle;

    private final CMMCore core_;
    private final ScriptInterface gui_;

    public LiteGUI (ScriptInterface gui) {

        
        USB = true;
        isConnected = false;
        isOn = true;
        version = "Version 0.1e_U";
        
        gui_ = gui;
        core_ = gui.getMMCore();
        
        initComponents();
        displayMessage(version);
    }

    private void startUSB(Integer deviceNum, Integer comNum) {
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
                Logger.getLogger(LiteGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            core_.initializeDevice(port);
            //displayMessage("Device initialized");
            gui_.sleep(10000);
            //core_.setSerialPortCommand(port, "i", "\r");
            //String answer = core_.getSerialPortAnswer(port, "\r");
            //displayMessage("Connection "+deviceNum+": "+answer+"");
        } catch (Exception e) {
            displayMessage("OK, well... "+e.getMessage());
        }
    }

    private void initComponents() {
        
        frame = new JFrame();
        setTitle("LED Control");
        setResizable(false);
        setBounds(100, 100, 340, 400);
        getContentPane().setLayout(null);
        
        winTitle = new JLabel("LED Lighting");
        winTitle.setFont(new Font("Tahoma", Font.PLAIN, 24));
        winTitle.setHorizontalAlignment(SwingConstants.CENTER);
        winTitle.setBounds(0, 0, 334, 48);
        getContentPane().add(winTitle);
        
        JLabel lblDiagram = new JLabel("");
        lblDiagram.setHorizontalAlignment(SwingConstants.CENTER);
        lblDiagram.setIcon(new ImageIcon("C:\\Users\\Helmut's Helmet\\Pictures\\Diagram-2.png"));
        lblDiagram.setBounds(0, 39, 334, 118);
        getContentPane().add(lblDiagram);
        
        lblStatus_1 = new JLabel("Status...");
        lblStatus_1.setHorizontalAlignment(SwingConstants.TRAILING);
        lblStatus_1.setBounds(0, 352, 334, 20);
        getContentPane().add(lblStatus_1);
//-------------------        
        slider_Exposure = new JSlider();
        slider_Exposure.setPaintTicks(true);
        slider_Exposure.setMajorTickSpacing(25);
        slider_Exposure.setValue(10);
        slider_Exposure.setMinimum(10);
        slider_Exposure.setMaximum(90);
        slider_Exposure.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer expValue = slider_Exposure.getValue();
                spinner_Exposure.setValue(expValue);
                setExposure(expValue);
            }
        });
        slider_Exposure.setOrientation(SwingConstants.VERTICAL);
        slider_Exposure.setBounds(10, 182, 40, 148);
        getContentPane().add(slider_Exposure);
//-------------------        
        slider_Delay = new JSlider();
        slider_Delay.setPaintTicks(true);
        slider_Delay.setMajorTickSpacing(25);
        slider_Delay.setValue(20);
        slider_Delay.setMinimum(20);
        slider_Delay.setMaximum(100);
        slider_Delay.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer delValue = slider_Delay.getValue();
                spinner_Delay.setValue(delValue);
                setDelay(delValue);
            }
        });
        slider_Delay.setOrientation(SwingConstants.VERTICAL);
        slider_Delay.setBounds(70, 182, 40, 148);
        getContentPane().add(slider_Delay);
//-------------------        
        slider_Every = new JSlider();
        slider_Every.setPaintTicks(true);
        slider_Every.setMajorTickSpacing(100);
        slider_Every.setValue(320);
        slider_Every.setMinimum(20);
        slider_Every.setMaximum(500);
        slider_Every.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer delValue = slider_Every.getValue();
                spinner_Every.setValue(delValue);
                setEvery(delValue);
            }
        });
        slider_Every.setOrientation(SwingConstants.VERTICAL);
        slider_Every.setBounds(130, 182, 40, 148);
        getContentPane().add(slider_Every);
//-------------------         
        spinner_Exposure = new JSpinner();
        spinner_Exposure.setModel(new SpinnerNumberModel(10,10,90,1));
        spinner_Exposure.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer expValue = (Integer)spinner_Exposure.getValue();
                slider_Exposure.setValue(expValue);
                setExposure(expValue);
            }
        });
        spinner_Exposure.setBounds(10, 332, 50, 20);
        getContentPane().add(spinner_Exposure);
//------------------- 
        spinner_Delay = new JSpinner();
        spinner_Delay.setModel(new SpinnerNumberModel(20, 20, 100, 1));
        spinner_Delay.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer delValue = (Integer)spinner_Delay.getValue();
                slider_Delay.setValue(delValue);
                setDelay(delValue);
            }
        });
        spinner_Delay.setBounds(70, 332, 50, 20);
        getContentPane().add(spinner_Delay);
//-------------------         
        spinner_Every = new JSpinner();
        spinner_Every.setModel(new SpinnerNumberModel(320, 20, 500, 1));
        spinner_Every.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Integer delValue = (Integer)spinner_Every.getValue();
                slider_Every.setValue(delValue);
                setEvery(delValue);
            }
        });
        spinner_Every.setBounds(130, 332, 50, 20);
        getContentPane().add(spinner_Every);
//-------------------         
        btnInitialize = new JButton("PUSH");
        btnInitialize.setForeground(Color.RED);
        btnInitialize.setBackground(Color.RED);
        btnInitialize.setBounds(194, 168, 117, 64);
        btnInitialize.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                startUSB(deviceNumber, (Integer)spinner_COM.getValue());
            }
        });
        getContentPane().add(btnInitialize);
//-------------------        
        btnInitiateBlinking = new JButton("Initiate Blinking");
        btnInitiateBlinking.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                initiateBlinking();
            }
        });
        btnInitiateBlinking.setBounds(194, 238, 117, 23);
        getContentPane().add(btnInitiateBlinking);
        
        JButton btnDoubleBlink = new JButton("Double Blink");
        btnDoubleBlink.setBounds(194, 267, 117, 23);
        getContentPane().add(btnDoubleBlink);
        
        JLabel lblExposureTime = new JLabel("LED On (ms)");
        lblExposureTime.setBounds(10, 165, 50, 20);
        getContentPane().add(lblExposureTime);
        
        JLabel lblDelay = new JLabel("Delay (us)");
        lblDelay.setBounds(70, 165, 50, 20);
        getContentPane().add(lblDelay);
        
        JLabel lblEvery = new JLabel("Every... (ms)");
        lblEvery.setBounds(130, 165, 50, 20);
        getContentPane().add(lblEvery);
        
        JButton btnTripleBlink = new JButton("Triple Blink");
        btnTripleBlink.setBounds(194, 295, 117, 23);
        getContentPane().add(btnTripleBlink);
        
        JButton btnUltraBlink = new JButton("Ultra Blink");
        btnUltraBlink.setBounds(194, 323, 117, 23);
        getContentPane().add(btnUltraBlink);
        
        spinner_COM = new JSpinner();
        spinner_COM.setModel(new SpinnerNumberModel(11,0,20,1));
        spinner_COM.setBounds(299, 0, 35, 20);
        getContentPane().add(spinner_COM);
        
        JLabel lblCom = new JLabel("COM");
        lblCom.setHorizontalAlignment(SwingConstants.CENTER);
        lblCom.setBounds(299, 23, 35, 14);
        getContentPane().add(lblCom);
    }
   
    public void switchLED(){
        //Switch the LED from blinking to steady on with isOn
    }
    
    public void setExposure(Integer exposeTime){   
        try {
            core_.setSerialPortCommand("Port"+deviceNumber,"C"+exposeTime*2,"\r");
            String answer = core_.getSerialPortAnswer("Port"+deviceNumber, "\r");
            displayMessage("LED \"on\" time: "+exposeTime+"ms. "+answer);
        } catch (Exception ex) {
            Logger.getLogger(LiteGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setDelay(Integer delayTime){
    try {
            core_.setSerialPortCommand("Port"+deviceNumber,"B"+delayTime*2,"\r");
            String answer = core_.getSerialPortAnswer("Port"+deviceNumber, "\r");
            displayMessage("Delay time: "+delayTime+"ms. "+answer);
        } catch (Exception ex) {
            Logger.getLogger(LiteGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setEvery(Integer everyTime){
        goTime = Math.round(62500/(1/((double)everyTime/1000)));
        try {
            core_.setSerialPortCommand("Port"+deviceNumber,"A"+goTime,"\r");
            String answer = core_.getSerialPortAnswer("Port"+deviceNumber, "\r");
            displayMessage("Repeat time: "+everyTime+"ms. "+answer);
        } catch (Exception ex) {
            Logger.getLogger(LiteGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initiateBlinking(){}

    private void handleError(String message) {
        JOptionPane.showMessageDialog(this, message);     
    }

    public void displayMessage(String txt) {
        lblStatus_1.setText(txt);
    }

    public void setPlugin(LitePlugin plugin) {
        plugin_ = plugin;
    }
}