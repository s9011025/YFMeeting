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

import java.net.ServerSocket;
import org.litesoft.p2pchat.AbstractP2PChat;
import org.litesoft.p2pchat.MyInfo;
import org.litesoft.p2pchat.PendingPeerManager;
import org.litesoft.p2pchat.UserDialog;



/**
 *
 * @author Chang,Yen-Fu
 */
public class YFMeeting extends AbstractP2PChat {
    public static String VERSION ="2018.r3.4";
    public static int DEFAULTPORT=11581;

    protected static String getTitle()
    {
        return "YFMeeting ver " + VERSION;
    }
    protected UserDialog getUserDialog(MyInfo pMyInfo) {
        //return new UserDialogAWT( getTitle() , pMyInfo );
        return new YFUI(getTitle(), pMyInfo);
    }

    public static void main(String[] args) {
        //new YFMeeting().init();
               
        new YFMeeting().getUserDialog(null);
    }



}

class P2PListener extends Thread {

    private PendingPeerManager ppm;
    private ServerSocket serverSocket;
    public P2PListener(PendingPeerManager zppm,ServerSocket zserverSocket) {
        
        ppm=zppm;
        serverSocket=zserverSocket;
    }

    public void run() {

        try {

            while (true) {
                ppm.addNewPeer(serverSocket.accept());
            }
        } catch (Exception e) {
            //e.printStackTrace();
            //System.exit( 1 );
        }

    }

}
