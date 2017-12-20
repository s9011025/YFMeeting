/*
 * The MIT License
 *
 * Copyright 2017 Chang,Yen-Fu.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.yfc.yfmeeting;

import org.yfc.yfmeeting.P2PListener;
import org.yfc.yfmeeting.YFMeeting;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import org.litesoft.p2pchat.ActivePeerManager;
import org.litesoft.p2pchat.IllegalArgument;
import org.litesoft.p2pchat.MyInfo;
import org.litesoft.p2pchat.PeerInfo;
import org.litesoft.p2pchat.PendingPeerManager;
import org.litesoft.p2pchat.ThisMachine;
import org.litesoft.p2pchat.UserDialog;
import org.litesoft.p2pchat.UserDialogPrivateMessageAWT;
import static org.yfc.yfmeeting.YFMeeting.DEFAULTPORT;
import static org.litesoft.p2pchat.AbstractP2PChat.getServerSocket;

/**
 *
 * @author Chang,Yen-Fu
 */
public class YFUI extends javax.swing.JFrame implements UserDialog {

    RunVNCViewer vnc_viewer = null;
    RunVNCServer vnc_server = null;
    private String vnc_server_dest = null;

    private MyInfo zMyInfo;
    private ActivePeerManager zActivePeerManager = null;
    private PendingPeerManager zPendingPeerManager = null;
    private Map zPrivateMessagersMap = new HashMap();

    private PendingPeerManager ppm;
    private ServerSocket serverSocket;
    private ActivePeerManager apm;
    private P2PListener plistener;

    /**
     * Creates new form YFUI
     */
    public YFUI(String title, MyInfo pMyInfo) {
        String s;
        initComponents();
        setTitle(title);
        this.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/org/yfc/yfmeeting/share-symbol.png")).getImage());
        for (int i = 0; null != (s = ThisMachine.getIPAddress(i)); i++) {
            if (s.equals("")) {
                continue;
            }
            choice_MyIP.add(s);
        }
        String[] scale_str = {"25", "50", "75", "80", "90", "100", "120", "Auto"};
        for (String scale : scale_str) {
            choice_scale.add(scale);
        }

        choice_scale.select("Auto");
        button_control.setEnabled(false);
        button_share.setEnabled(false);
        setLocationRelativeTo(null);
        label_status.setVisible(false);

        this.setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                /*if (javax.swing.JOptionPane.showConfirmDialog(runUI, 
            "Are you sure to close this window?", "Really Closing?", 
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE) == javax.swing.JOptionPane.YES_OPTION){
            
            System.exit(0);
        }*/
                closeVNCAll();
                System.exit(0);
            }
        });
        this.setVisible(true);
    }

    private void runStart() {
        if (button_start.getLabel().equals("Disconnect")) {
            runDisconnect();

        } else if (button_start.getLabel().equals("Start")) {

            if (text_user.getText().length() > 20) {
                text_user.setText(text_user.getText().substring(0, 20));
            }
            zMyInfo = new MyInfo(text_user.getText(), choice_MyIP.getSelectedItem(), DEFAULTPORT);
            ppm = new PendingPeerManager(this);

            serverSocket = getServerSocket(zMyInfo.getPort());
            apm = new ActivePeerManager(zMyInfo, this, ppm);
            plistener = new P2PListener(ppm, serverSocket);
            plistener.start();
            button_join.setLabel("Disconnect");
            button_start.setLabel("Disconnect");
            
            choice_MyIP.setEnabled(false);
            text_user.setEnabled(false);
            text_serverIP.setEnabled(false);
            label_status.setText("");
            button_share.setEnabled(true);
            showWho();
        }

    }

    private void runJoin() {
        if (button_join.getLabel().equals("Disconnect")) {
            runDisconnect();

        } else if (button_join.getLabel().equals("Join")) {
            if (text_serverIP.getText().trim().equals("")) {
                javax.swing.JOptionPane.showMessageDialog(this, "Please fill the remote IP address", "ERROR", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (text_user.getText().length() > 20) {
                text_user.setText(text_user.getText().substring(0, 20));
            }
            zMyInfo = new MyInfo(text_user.getText(), choice_MyIP.getSelectedItem(), DEFAULTPORT);
            ppm = new PendingPeerManager(this);

            ppm.addNewPeer(new PeerInfo(null, text_serverIP.getText(), DEFAULTPORT));

            serverSocket = getServerSocket(zMyInfo.getPort());
            apm = new ActivePeerManager(zMyInfo, this, ppm);
            plistener = new P2PListener(ppm, serverSocket);
            plistener.start();
            button_join.setLabel("Disconnect");
            button_start.setLabel("Disconnect");
            button_join.setEnabled(false);
            button_start.setEnabled(false);
            choice_MyIP.setEnabled(false);
            text_user.setEnabled(false);
            text_serverIP.setEnabled(false);
            label_status.setText("");
            showWho();
        }

    }

    private void runDisconnect() {

        choice_MyIP.setEnabled(true);
        text_user.setEnabled(true);
        text_serverIP.setEnabled(true);
        button_join.setEnabled(true);
        button_start.setEnabled(true);
        button_join.setLabel("Join");
        button_start.setLabel("Start");
        button_control.setLabel("Control");
        button_control.setEnabled(false);
        button_share.setEnabled(false);
        button_share.setLabel("Share");

        plistener = null;
        if (zActivePeerManager != null) {
            zActivePeerManager.clearActivePeer();
        }
        zActivePeerManager = null;

        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }

        } catch (Exception e) {
            //e.printStackTrace();
            java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.StringWriter());
             e.printStackTrace(pw);
             javax.swing.JOptionPane.showMessageDialog(null,pw.toString() , "Exception", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        closeVNCAll();

        zPeersList.removeAll();

    }

    private void runViewerControl() {

        if (vnc_viewer != null) {
            vnc_viewer.close();
        }
        vnc_viewer = new RunVNCViewer(vnc_server_dest);
        vnc_viewer.setControl();

        vnc_viewer.start();

    }

    private void closeVNCAll() {

        if (vnc_viewer != null) {
            vnc_viewer.close();
            vnc_viewer = null;
        }
        if (vnc_server != null) {
            vnc_server.close();
            vnc_server = null;
        }
        choice_scale.select("Auto");
    }

    private void handleTVNVIEW(PeerInfo pPeerInfo, String pMessage) {
        String[] tokens = pMessage.split("=");
        String cmd = tokens[0];
        String value = tokens[1];
        String vnccmd;
        int yesno;
        if (cmd.equals("TVNVIEW_CALL_CTL")) {
            if (!pPeerInfo.equals(zMyInfo) && vnc_server_dest.equals(zMyInfo.getAddresses())) {
                yesno = javax.swing.JOptionPane.showConfirmDialog(this, pPeerInfo.toListString() + " requests control, accept?", "Request Control", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);

                if (yesno == javax.swing.JOptionPane.YES_OPTION) {
                    vnccmd = "TVNVIEW_ACPT_CTL=" + pPeerInfo.getAddresses();
                    handleCHAT(vnccmd);
                    showCHAT(zMyInfo, vnccmd);
                } else {
                    vnccmd = "TVNVIEW_REJT_CTL=" + pPeerInfo.getAddresses();
                    handleCHAT(vnccmd);
                    showCHAT(zMyInfo, vnccmd);

                }
            }

        }
        ////////////////////////////////////////
        if (cmd.equals("TVNVIEW_ACPT_CTL")) {
            if (pPeerInfo.getAddresses().equals(vnc_server_dest) && zMyInfo.getAddresses().equals(value)) {
                runViewerControl();
                button_control.setLabel("Close");
                button_control.setEnabled(true);
            }
        }
        ///////////////////////////////////////
        if (cmd.equals("TVNVIEW_REJT_CTL")) {
            if (pPeerInfo.getAddresses().equals(vnc_server_dest) && zMyInfo.getAddresses().equals(value)) {
                button_control.setEnabled(true);
            }
        }
    }

    private void handleTVNSERVER(PeerInfo pPeerInfo, String pMessage) {

        String dest = pMessage.replace("TVNSERVER=", "").trim();
        vnc_server_dest = dest.equals("null") ? null : dest;
        if (pPeerInfo.equals(zMyInfo)) {
            if (vnc_server_dest == null) {
                if (vnc_server != null) {
                    vnc_server.close();
                    vnc_server = null;
                }

            } else {
                if (vnc_server == null) {
                    vnc_server = new RunVNCServer();
                    vnc_server.start();
                } else if (vnc_server.isAlive() == false) {
                    vnc_server.close();
                    vnc_server = new RunVNCServer();
                    vnc_server.start();
                }
            }

        } else {

            if (vnc_server_dest == null) {
                if (vnc_viewer != null) {
                    vnc_viewer.close();
                    button_share.setEnabled(true);
                    choice_scale.select("Auto");

                }
            } else {
                if (vnc_viewer != null) {
                    vnc_viewer.close();
                }

                vnc_viewer = new RunVNCViewer(vnc_server_dest);
                vnc_viewer.setViewonly();
                vnc_viewer.start();
                button_control.setEnabled(true);
                button_share.setEnabled(false);
                choice_scale.select("Auto");
            }

        }

    }

    ///////////////////////////////////////////////////////////
    // implements UserDialog
    ///////////////////////////////////////////////////////////
    public void showHELO(PeerInfo pPeerInfo) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        showWho();
    }

    private synchronized void showWho() {
        zPeersList.removeAll();
        if (button_join.getLabel().equals("Disconnect")) {
            zPeersList.add(zMyInfo.toListString(), 0);

            PeerInfo[] peers = getPeerInfos();
            for (int i = 0; i < peers.length; i++) {
                zPeersList.add(peers[i].toListString(), i + 1);
            }

        }
    }

    private PeerInfo[] getPeerInfos() {
        return (zActivePeerManager != null) ? zActivePeerManager.getPeerInfos() : new PeerInfo[0]; // builder pattern
    }

    public void showNAME(PeerInfo pPeerInfo) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        showWho();
    }

    public void showPMSG(PeerInfo pPeerInfo, String pMessage) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        IllegalArgument.ifNull("Message", pMessage);
        UserDialogPrivateMessageAWT subWindow = getPrivateMessageWindow(pPeerInfo);
        if (subWindow != null) {
            subWindow.send(pPeerInfo.getChatName() + ": " + pMessage);
        } else {
            send("Private Message From (" + pPeerInfo.getID() + " " + pPeerInfo.getChatName() + "): " + pMessage);
        }
    }

    private UserDialogPrivateMessageAWT getPrivateMessageWindow(PeerInfo pPeerInfo) {
        return (UserDialogPrivateMessageAWT) zPrivateMessagersMap.get(pPeerInfo);
    }

    private void send(String pMessage) {

        label_status.setText(pMessage);
    }

    public void showCHAT(PeerInfo pPeerInfo, String pMessage) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        IllegalArgument.ifNull("Message", pMessage);
        send(pPeerInfo.getID() + " " + pPeerInfo.getChatName() + ": " + pMessage);

        if (pMessage.contains("TVNSERVER")) {
            handleTVNSERVER(pPeerInfo, pMessage);
        }
        if (pMessage.contains("TVNVIEW")) {
            handleTVNVIEW(pPeerInfo, pMessage);
        }

    }

    public void showConnect(PeerInfo pPeerInfo) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        showWho();
        button_join.setEnabled(true);
        button_start.setEnabled(true);
        button_share.setEnabled(true);
    }

    public void showDisconnect(PeerInfo pPeerInfo) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        UserDialogPrivateMessageAWT subWindow = getPrivateMessageWindow(pPeerInfo);
        if (subWindow != null) {
            unregisterPrivateMessager(pPeerInfo);
            subWindow.dispose();
        }
        showWho();
        
    }

    public void unregisterPrivateMessager(PeerInfo pPeerInfo) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        zPrivateMessagersMap.remove(pPeerInfo);
    }

    public void showConnectFailed(PeerInfo pPeerInfo) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        send("Unable to Connect to: " + pPeerInfo.toString());
        runDisconnect();
    }

    public void showUnrecognized(PeerInfo pPeerInfo, String pBadMessage) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        IllegalArgument.ifNull("BadMessage", pBadMessage);
        send("Unrecognized Command from (" + pPeerInfo.getID() + " " + pPeerInfo.getChatName() + "): " + pBadMessage);
    }

    public void showStreamsFailed(PeerInfo pPeerInfo) {
        IllegalArgument.ifNull("PeerInfo", pPeerInfo);
        send("Unable to Set up I/O Streams with: " + pPeerInfo.toString());
    }

    public void setActivePeerManager(ActivePeerManager pActivePeerManager) {
        if (pActivePeerManager != null) {
            zActivePeerManager = pActivePeerManager;
        }
    }

    public void setPendingPeerManager(PendingPeerManager pPendingPeerManager) {
        if (pPendingPeerManager != null) {
            zPendingPeerManager = pPendingPeerManager;
        }
    }

    private void handleCHAT(String pLine) {
        if (zActivePeerManager == null) // builder pattern
        {
            send("No Peer Manager!");
        } else {
            zActivePeerManager.sendToAllCHAT(pLine);
            send(zMyInfo.getChatName() + ": " + pLine);
        }
    }
    ///////////////////////////////////////////////////////////
    // End of implements UserDialog
    ///////////////////////////////////////////////////////////

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        button_share = new java.awt.Button();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        label4 = new java.awt.Label();
        text_serverIP = new java.awt.TextField();
        label_Name = new java.awt.Label();
        text_user = new java.awt.TextField();
        choice_MyIP = new java.awt.Choice();
        button_control = new java.awt.Button();
        button_join = new java.awt.Button();
        zPeersList = new java.awt.List(10,false);
        label3 = new java.awt.Label();
        button_about = new java.awt.Button();
        label_status = new java.awt.Label();
        label6 = new java.awt.Label();
        choice_scale = new java.awt.Choice();
        jLabel1 = new javax.swing.JLabel();
        button_start = new java.awt.Button();
        label5 = new java.awt.Label();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        button_share.setLabel("Share");
        button_share.setMinimumSize(new java.awt.Dimension(56, 26));
        button_share.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_shareActionPerformed(evt);
            }
        });

        label1.setText("Remote Control");

        label2.setText("Screen Sharing");

        label4.setText("My IP Address:");

        text_serverIP.setPreferredSize(new java.awt.Dimension(82, 22));
        text_serverIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                text_serverIPKeyPressed(evt);
            }
        });

        label_Name.setText("Name:");

        text_user.setText(ThisMachine.getOSUserName());

        button_control.setLabel("Control");
        button_control.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_controlActionPerformed(evt);
            }
        });

        button_join.setLabel("Join");
        button_join.setMinimumSize(new java.awt.Dimension(56, 26));
        button_join.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_joinActionPerformed(evt);
            }
        });

        label3.setText("START or JOIN a meeting");

        button_about.setLabel("About");
        button_about.setMinimumSize(new java.awt.Dimension(56, 26));
        button_about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_aboutActionPerformed(evt);
            }
        });

        label_status.setForeground(new java.awt.Color(255, 0, 0));

        label6.setText("Scale: %");

        choice_scale.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                choice_scaleItemStateChanged(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/yfc/yfmeeting/logo.png"))); // NOI18N

        button_start.setLabel("Start");
        button_start.setMinimumSize(new java.awt.Dimension(56, 26));
        button_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_startActionPerformed(evt);
            }
        });

        label5.setText("IP address:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(zPeersList, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(button_about, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(label1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(label2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(button_share, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_control, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(choice_scale, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(button_join, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(text_serverIP, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                                        .addComponent(button_start, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(label_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(choice_MyIP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(text_user, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))))))
                        .addGap(0, 13, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(choice_MyIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(text_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_start, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(text_serverIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_join, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(button_share, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addComponent(button_control, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(choice_scale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_about, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addComponent(zPeersList, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(label_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        button_share.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_shareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_shareActionPerformed
        // TODO add your handling code here:
        if (button_join.getLabel().equals("Start")) {
            return;
        }

        String vnccmd;
        if (button_share.getLabel().equals("Share")) {

            button_share.setLabel("Stop");

            vnccmd = "TVNSERVER=" + zMyInfo.getAddresses();
            handleCHAT(vnccmd);
            showCHAT(zMyInfo, vnccmd);
        } else {

            button_control.setVisible(true);
            button_share.setLabel("Share");
            vnccmd = "TVNSERVER=null";
            handleCHAT(vnccmd);
            showCHAT(zMyInfo, vnccmd);
        }


    }//GEN-LAST:event_button_shareActionPerformed

    private void button_joinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_joinActionPerformed
        // TODO add your handling code here:
        runJoin();
    }//GEN-LAST:event_button_joinActionPerformed

    private void button_controlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_controlActionPerformed
        // TODO add your handling code here:
        if (button_control.getLabel().equals("Control")) {
            String vnccmd = "TVNVIEW_CALL_CTL=" + zMyInfo.getAddresses();
            handleCHAT(vnccmd);
            showCHAT(zMyInfo, vnccmd);
            button_control.setEnabled(false);

        } else if (button_control.getLabel().equals("Close")) {

            button_control.setLabel("Control");
            if (vnc_viewer != null) {
                vnc_viewer.close();
            }
            vnc_viewer = new RunVNCViewer(vnc_server_dest);
            vnc_viewer.setViewonly();
            vnc_viewer.start();
            String vnccmd = "TVNVIEW_STOP_CTL=" + zMyInfo.getAddresses();
            handleCHAT(vnccmd);
            showCHAT(zMyInfo, vnccmd);
        }


    }//GEN-LAST:event_button_controlActionPerformed

    private void text_serverIPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_serverIPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            runJoin();
        }

    }//GEN-LAST:event_text_serverIPKeyPressed

    private void button_aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_aboutActionPerformed
        // TODO add your handling code here:
        //new AboutMe().setVisible(true);

        javax.swing.JTextArea textarea = new javax.swing.JTextArea("YFMeeting is copyright 2017 Chang Yen-Fu under MIT License.\nAny suggestion or bug report please contact me via email or github.\n\nE-mail: s9011025@gmail.com\n\nGithub: https://github.com/s9011025/YFMeeting\n\nVersion: "+YFMeeting.getTitle());
        textarea.setEditable(false);
        javax.swing.JOptionPane optionPane = new javax.swing.JOptionPane();
        optionPane.setMessage(textarea);
        optionPane.setMessageType(javax.swing.JOptionPane.INFORMATION_MESSAGE);
        javax.swing.JDialog dialog = optionPane.createDialog(null, "About");
        dialog.setVisible(true);

    }//GEN-LAST:event_button_aboutActionPerformed

    private void choice_scaleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_choice_scaleItemStateChanged
        // TODO add your handling code here:
        if (vnc_viewer != null && vnc_server_dest != null) {
            vnc_viewer.close();
            vnc_viewer = new RunVNCViewer(vnc_server_dest);
            vnc_viewer.setScale(choice_scale.getSelectedItem().toLowerCase());
            vnc_viewer.start();
        }

    }//GEN-LAST:event_choice_scaleItemStateChanged

    private void button_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_startActionPerformed
        // TODO add your handling code here:
        runStart();
    }//GEN-LAST:event_button_startActionPerformed

    /**
     * @param args the command line arguments
     */
    //public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     *//*
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(YFUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(YFUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(YFUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(YFUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }*/
    //</editor-fold>

    /* Create and display the form */
 /*      java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new YFUI().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button_about;
    private java.awt.Button button_control;
    private java.awt.Button button_join;
    private java.awt.Button button_share;
    private java.awt.Button button_start;
    private java.awt.Choice choice_MyIP;
    private java.awt.Choice choice_scale;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private java.awt.Label label6;
    private java.awt.Label label_Name;
    private java.awt.Label label_status;
    private java.awt.TextField text_serverIP;
    private java.awt.TextField text_user;
    private java.awt.List zPeersList;
    // End of variables declaration//GEN-END:variables
}
