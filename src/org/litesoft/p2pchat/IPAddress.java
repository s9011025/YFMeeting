/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
//package net.gqu.utils;
package org.litesoft.p2pchat;


import java.util.StringTokenizer;

/**
 * TCP/IP Address Utility Class
 *
 * @author gkspencer
 */
public class IPAddress {

    /**
     * Check if the specified address is within the required subnet
     *
     * @param ipaddr String
     * @param subnet String
     * @param mask String
     * @return boolean
     */
    public final static boolean isInSubnet(String ipaddr, String subnet, String mask) {

        //  Convert the addresses to integer values
        int ipaddrInt = parseNumericAddress(ipaddr);
        if (ipaddrInt == 0) {
            return false;
        }

        int subnetInt = parseNumericAddress(subnet);
        if (subnetInt == 0) {
            return false;
        }

        int maskInt = parseNumericAddress(mask);
        if (maskInt == 0) {
            return false;
        }

        //  Check if the address is part of the subnet
        if ((ipaddrInt & maskInt) == subnetInt) {
            return true;
        }
        return false;
    }

    /**
     * Check if the specified address is a valid numeric TCP/IP address and
     * return as an integer value
     *
     * @param ipaddr String
     * @return int
     */
    public final static int parseNumericAddress(String ipaddr) {

        //  Check if the string is valid
        if (ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15) {
            return 0;
        }

        //  Check the address string, should be n.n.n.n format
        StringTokenizer token = new StringTokenizer(ipaddr, ".");
        if (token.countTokens() != 4) {
            return 0;
        }

        int ipInt = 0;

        while (token.hasMoreTokens()) {

            //  Get the current token and convert to an integer value
            String ipNum = token.nextToken();

            try {

                //  Validate the current address part
                int ipVal = Integer.valueOf(ipNum).intValue();
                if (ipVal < 0 || ipVal > 255) {
                    return 0;
                }

                //  Add to the integer address
                ipInt = (ipInt << 8) + ipVal;
            } catch (NumberFormatException ex) {
                return 0;
            }
        }

        //  Return the integer address
        return ipInt;
    }

}
