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
class RunVNCServer extends Thread {

    private String dir = System.getProperty("user.dir");
    private String exe_tvnserver = "TightVNC/tvnserver.exe";
    private String exe_params = "-run";
    private Process process = null;

    public RunVNCServer() {
    }

    public void run() {
        try {
            Runtime runTime = Runtime.getRuntime();

            //System.out.println("Server command: " + exe_tvnserver + " " + exe_params);
            process = runTime.exec(exe_tvnserver + " " + exe_params);
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
