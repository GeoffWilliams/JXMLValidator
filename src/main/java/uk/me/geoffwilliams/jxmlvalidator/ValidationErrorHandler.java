/*
 * JXMLValidator -- command line XML tool to validate files to schema
 * Copyright (C) 2013  Geoff Williams<geoff@geoffwilliams.me.uk>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package uk.me.geoffwilliams.jxmlvalidator;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Keeps track of validation errors detected
 * @author Geoff Williams
 */
public class ValidationErrorHandler implements ErrorHandler {
     
    private List<String> fatal = new ArrayList<String>();
    private List<String> error = new ArrayList<String>();
    private List<String> warning = new ArrayList<String>();
    

    private String getParseExceptionInfo(SAXParseException spe) {
        String systemId = spe.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }

        String info = "URI=" + systemId + " Line=" + spe.getLineNumber() +
                      ": " + spe.getMessage();
        return info;
    }

    public void warning(SAXParseException spe) throws SAXException {
        warning.add(getParseExceptionInfo(spe));
    }
        
    public void error(SAXParseException spe) throws SAXException {
        error.add(getParseExceptionInfo(spe));
    }

    public void fatalError(SAXParseException spe) throws SAXException {
        fatal.add(getParseExceptionInfo(spe));
    }

    public List<String> getFatal() {
        return fatal;
    }

    public List<String> getError() {
        return error;
    }

    public List<String> getWarning() {
        return warning;
    }
    
    public boolean isValid() {
        return fatal.size() + error.size() + warning.size() == 0;
    }
    
    
}

