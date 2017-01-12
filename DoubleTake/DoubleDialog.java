package org.micromanager.api.DoubleTake;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import org.micromanager.api.ScriptInterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import mmcorej.CMMCore;


public class DoubleDialog extends javax.swing.JFrame{
    
    private DoublePlugin plugin_;
    private DoubleThread DoubleThread_;
    
    private JFrame frame;
    
    private JTextField textFieldExposureDelay;
    private JTextField textFieldExposureTime;
    private JTextField textFieldRepeat;
    private JTextField txtFileName;
    private JButton txtTriggered;
    private JComboBox dataType;
    private JLabel lblStatus;
    private JButton btnUsbConnect;

    static Integer exposureDelay;
    static Integer exposureTime;
    static Integer repeatTime;
    static Integer typetype;
    
    static Double convertTime;
    
    static String version;
    
    static Boolean USB;
    static Boolean startBtn;
    
    private final CMMCore core_;
    private final ScriptInterface gui_;
    
    public DoubleDialog(ScriptInterface gui) {
        gui_ = gui;
        core_ = gui.getMMCore();
        
        exposureDelay = 2000;
        exposureTime = 100;
        repeatTime = 20000;
        
        USB = false;
        startBtn = false;
        
        version = "Version 1.0b_U";
        
        initComponents();
        displayMessage(version);
    }
    
    private void startUSB() {
        displayMessage("Initializing connection...");
        displayMessage("Successful connection.");
        try {
            core_.loadDevice("Port", "SerialManager", "COM5");
            core_.setProperty("Port","BaudRate","9600");
        } catch (Exception e) {
            try {
                displayMessage("Port already defined...");
                core_.unloadDevice("Port");
                core_.loadDevice("Port", "SerialManager", "COM5");
                core_.setProperty("Port","BaudRate","9600");
            } catch (Exception ex) {
                Logger.getLogger(DoubleDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            core_.initializeDevice("Port");
            gui_.sleep(2000);
            core_.setSerialPortCommand("Port", "i", "\r");
            displayMessage("Successful connection.");
            USB = true;
            startBtn = true;
            btnUsbConnect.setEnabled(false);
            txtTriggered.setEnabled(startBtn);
            txtTriggered.setBackground(Color.GREEN);
            txtTriggered.setText("Start");
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }    
    
    private void initComponents(){
        frame = new JFrame();
        setTitle("Double Take");
        setResizable(false);
        setBounds(100, 100, 320, 340);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblExposureControlSystem = new JLabel("Exposure Control System");
        lblExposureControlSystem.setHorizontalAlignment(SwingConstants.LEFT);
        lblExposureControlSystem.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblExposureControlSystem.setBounds(10, 11, 239, 30);
        getContentPane().add(lblExposureControlSystem);

        JLabel lblDelayBetweenImages = new JLabel("Delay Between Exposures");
        lblDelayBetweenImages.setHorizontalAlignment(SwingConstants.CENTER);
        lblDelayBetweenImages.setBounds(0, 52, 145, 14);
        getContentPane().add(lblDelayBetweenImages);

        JLabel lblExposureTime = new JLabel("Exposure Time");
        lblExposureTime.setHorizontalAlignment(SwingConstants.CENTER);
        lblExposureTime.setBounds(10, 81, 125, 14);
        getContentPane().add(lblExposureTime);

        JLabel lblRepeateEvery = new JLabel("Repeate Every...");
        lblRepeateEvery.setHorizontalAlignment(SwingConstants.CENTER);
        lblRepeateEvery.setBounds(10, 109, 125, 14);
        getContentPane().add(lblRepeateEvery);

        JLabel lblMs = new JLabel("µs");
        lblMs.setBounds(234, 52, 46, 20);
        getContentPane().add(lblMs);

        JLabel label = new JLabel("µs");
        label.setBounds(234, 80, 46, 20);
        getContentPane().add(label);

        JLabel label_1 = new JLabel("µs");
        label_1.setBounds(234, 109, 46, 20);
        getContentPane().add(label_1);

        textFieldExposureDelay = new JTextField();
        textFieldExposureDelay.setText(""+exposureDelay);
        textFieldExposureDelay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                        exposureDelay = Integer.parseInt(textFieldExposureDelay.getText());
                        setExposureDelay(exposureDelay);
                }
        });
        textFieldExposureDelay.setBounds(145, 52, 86, 20);
        getContentPane().add(textFieldExposureDelay);
        textFieldExposureDelay.setColumns(10);		

        textFieldExposureTime = new JTextField();
        textFieldExposureTime.setText(""+exposureTime);
        textFieldExposureTime.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                        exposureTime = Integer.parseInt(textFieldExposureTime.getText());
                        setExposureTime(exposureTime);
                }
        });
        textFieldExposureTime.setColumns(10);
        textFieldExposureTime.setBounds(145, 80, 86, 20);
        getContentPane().add(textFieldExposureTime);

        textFieldRepeat = new JTextField();
        textFieldRepeat.setText(""+repeatTime);
        textFieldRepeat.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                        repeatTime = Integer.parseInt(textFieldRepeat.getText());
                        setRepeatTime(repeatTime);
                }
        });
        textFieldRepeat.setColumns(10);
        textFieldRepeat.setBounds(145, 109, 86, 20);
        getContentPane().add(textFieldRepeat);

        txtTriggered = new JButton();
        txtTriggered.setBackground(Color.LIGHT_GRAY);
        txtTriggered.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtTriggered.setHorizontalAlignment(SwingConstants.CENTER);
        txtTriggered.setText("Standby");
        txtTriggered.setEnabled(startBtn);
        txtTriggered.setBounds(33, 134, 86, 30);
        txtTriggered.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                if (startBtn) {
                    start();
                    txtTriggered.setText("Stop");
                    txtTriggered.setBackground(Color.RED);
                }
                else if (!startBtn) {
                    stop();
                    txtTriggered.setText("Start");
                    txtTriggered.setBackground(Color.GREEN);
                }
            }
        });
        getContentPane().add(txtTriggered);

        btnUsbConnect = new JButton("USB");
        btnUsbConnect.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                startUSB();
            }
        });
        btnUsbConnect.setBounds(229, 139, 60, 23);
        getContentPane().add(btnUsbConnect);

        dataType = new JComboBox(new String[] {"256", "128"});
        dataType.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                typetype = Integer.parseInt((String)dataType.getSelectedItem());
                convertTime = (double)16000000/typetype;
            }
        });
        dataType.setBounds(248, 11, 46, 30);
        getContentPane().add(dataType);
        typetype = Integer.parseInt((String)dataType.getSelectedItem());
        convertTime = (double)16000000/typetype;

        JLabel label_2 = new JLabel("");
        label_2.setHorizontalAlignment(SwingConstants.CENTER);
        label_2.setIcon(new ImageIcon(DoubleDialog.class.getResource("/org/micromanager/api/DoubleTake/Diagram-2.png")));
        label_2.setBounds(0, 173, 304, 97);
        getContentPane().add(label_2);

        lblStatus = new JLabel("Status...");
        lblStatus.setBounds(10, 281, 209, 21);
        getContentPane().add(lblStatus);
        
        txtFileName = new JTextField();
        txtFileName.setText("File Name");
        txtFileName.setBounds(218, 281, 86, 20);
        getContentPane().add(txtFileName);
        txtFileName.setColumns(10);
    }

    public void start(){
        startBtn = !startBtn;
        DoubleThread_ = new DoubleThread(gui_, core_, this, 99999, (int) Math.round((100+2*exposureTime+exposureDelay+200)*.001),txtFileName.getText());
        DoubleThread_.start();
    }
    
    public void stop(){
        startBtn = !startBtn;
        DoubleThread_.stop_ = true;
    }
    
    public void setExposureDelay(Integer x){
        if ((300+exposureTime*2+x) > repeatTime || x < 8){
            displayMessage("Invalid Exposure Delay.");
        }
        else if (USB){
            setStatus(x, exposureTime, repeatTime, typetype);
            Double y = (double)x;
            Long n = Math.round(convertTime*y*0.000001);
            displayMessage(""+n);
            try {
                core_.setSerialPortCommand("Port", "A"+n, "\r");
            } catch (Exception ex) {
                Logger.getLogger(DoubleDialog.class.getName()).log(Level.SEVERE, null, ex);
            }     
        }
    }

    public void setExposureTime(Integer x){
        if ((300+exposureDelay+2*x) > repeatTime || x < 8){
            displayMessage("Invalid Exposure Time.");
        }
        else if (USB){
            setStatus(exposureDelay, x, repeatTime, typetype);
            Double y = (double)x;
            Double n = convertTime*y*0.000001;
            try {
                core_.setSerialPortCommand("Port", "B"+n, "\r");
            } catch (Exception ex) {
                Logger.getLogger(DoubleDialog.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }

    public void setRepeatTime(Integer x){
        if ((300+exposureDelay+2*exposureTime) > x || x < 300){
            displayMessage("Invalid Repeat Time.");
        }
        else if (USB){
            setStatus(exposureDelay, exposureTime, x, typetype);
            Double y = (double)x;
            Double n = convertTime*y*0.000001;
            try {
                core_.setSerialPortCommand("Port", "C"+n, "\r");
            } catch (Exception ex) {
                Logger.getLogger(DoubleDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setStatus(Integer x, Integer y, Integer z, Integer num){
        displayMessage("ED: "+x+", ET: "+y+", RT: "+z+", DT: "+num);
    }

    public void displayMessage(String x){
        lblStatus.setText(x);
    }
    
    private void handleError(String message) {
        JOptionPane.showMessageDialog(this, message);     
    }

    public void setPlugin(DoublePlugin plugin) {
        plugin_ = plugin;
    }
}
