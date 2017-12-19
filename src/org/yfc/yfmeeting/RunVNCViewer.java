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



/**
 *
 * @author Chang,Yen-Fu
 */
class RunVNCViewer extends Thread {

    private String dir = System.getProperty("user.dir");
    private String exe_tvnviewer = "TightVNC/tvnviewer.exe";
    private String ipaddr;
    private String option_viewonly = "-viewonly=yes";
    private Process process = null;
    private String password = "-password=1875";
    private static int vncport = 5987;
    private String scale = "-scale=auto";
    private String misc_options = "-showcontrols=no -encoding=hextile -compressionlevel=1 -useclipboard=no";

    /*public RunVNCViewer() {

    }*/
    public RunVNCViewer(String zipaddr) {
        ipaddr = zipaddr;

    }

    public void setScale(String s){
    
        scale = "-scale="+s;
    
    }
    
    public void setViewonly() {

        option_viewonly = "-viewonly=yes";

    }

    public void setControl() {

        option_viewonly = "-viewonly=no";
        password = "-password=1873";

    }

    public void run() {

        //System.out.println("Run VNCViewer");

        try {
            Runtime runTime = Runtime.getRuntime();
            String vnccmd = exe_tvnviewer + " " + option_viewonly + " " + ipaddr + ":" + vncport + " "+scale +" " + password+" "+misc_options;
            //System.out.println("View command: " + vnccmd);
            process = runTime.exec(vnccmd);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        if (process != null) {
            process.destroy();
        }

    }
}
