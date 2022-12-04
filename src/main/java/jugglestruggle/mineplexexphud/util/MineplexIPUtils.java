/*
 * MineplexExpHud: A mod which tracks the current
 * EXP the user has on the Mineplex server.
 * Copyright (C) 2022  JuggleStruggle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 *  <https://www.gnu.org/licenses/>.
 */

package jugglestruggle.mineplexexphud.util;


import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MineplexIPUtils
{
    public static final String[] MINEPLEX_SUBDOMAINS = {
        "clans", "us", "eu", "beta", "hub"
    };
    
    
    // Accepts mineplex.com subdomains
    public static final Pattern MINEPLEX_DNS_PATTERN = Pattern.compile("([a-zA-Z]+)[.]mineplex.com");
    // Only accept 173.236.67.XXX (the X ranging from 2 to 3 numbers)
    public static final Pattern MINEPLEX_IP_PATTERN = Pattern.compile("173.236.67[.]([0-9]{2,3})");
    
    public static boolean isValidMineplexIpOrDns(String ip)
    {
        // First and foremost, check for emptiness / nulls
        if (ip == null || ip.isEmpty())
            return false;
    
        // Second, lowercase the IP to avoid having to ignore casing
        ip = ip.toLowerCase(Locale.ROOT);
        
        // Third, check if there are any ports provided
        String[] ipAndPorts = ip.split(":", 1);
        
        if (ipAndPorts.length >= 2)
        {
            // If so, IP is assumed to be index 0, whether as port being 1...
            ip = ipAndPorts[0];
        }
        
        // Then check if the IP is just mineplex.com
        if (ip.equals("mineplex.com"))
            return true;
    
        Matcher m = MINEPLEX_DNS_PATTERN.matcher(ip);
        
        if (m.find())
        {
            String subdomain = m.group(1);
    
            for (String knownSubdomain : MINEPLEX_SUBDOMAINS)
                if (subdomain.equals(knownSubdomain))
                    return true;
        }
        else
        {
            m = MINEPLEX_IP_PATTERN.matcher(ip);
    
            if (m.find())
            {
                try
                {
                    int address = Integer.parseInt(m.group(1));
                    
                    if (address >= 11 && address <= 38)
                       return true;
                }
                catch (NumberFormatException n)
                {
                    // ignore
                }
            }
        }
        
        return false;
    }
    
}
