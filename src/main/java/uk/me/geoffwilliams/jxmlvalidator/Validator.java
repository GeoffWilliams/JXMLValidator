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
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Perform the validation
 * @author Geoff Williams
 */
public class Validator {
    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public int validate(String uri) {
        int status;
        try {
            String filename;

            if (uri.contains("://")) {
                // file is a URL - download to temporary file...
                FileDownloader fileDownloader = new FileDownloader();
                filename = fileDownloader.downloadFile(uri);   
            } else {
                logger.debug("processing local file...");
                filename = uri;
            }
            status = process(filename);
        } catch (ParserConfigurationException ex) {
            status = ValidationErrorHandler.STATUS_EXCEPTION;
            logger.error("Parser Configuration error: {}", ex.getMessage());
        } catch (SAXException ex) {
            status = ValidationErrorHandler.STATUS_EXCEPTION;
            logger.error("SAX error: {}", ex.getMessage());
        } catch (IOException ex) {
            status = ValidationErrorHandler.STATUS_EXCEPTION;
            logger.error("IO error: {}", ex.getMessage());
        }
        return status;
    }
    
    private int process(String filename) throws ParserConfigurationException, SAXException, IOException {
        logger.info("Starting validating on: {}", filename);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // validation and namespaces ON
        dbf.setNamespaceAware(true);
        dbf.setValidating(true);
        dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        DocumentBuilder db = dbf.newDocumentBuilder(); 
        ValidationErrorHandler errorHandler = new ValidationErrorHandler();
        db.setErrorHandler(errorHandler);
        Document doc = db.parse(new File(filename));

        if (errorHandler.isValid()) {
            logger.info("***** File {} is VALID XML! :-D *****", filename);
        } else {
            logger.error("**** File {} is INVALID XML :`( *****", filename);
            logger.info("Error report:");
            reportToLogger("Warnings", errorHandler.getWarning());
            reportToLogger("Errors", errorHandler.getError());
            reportToLogger("Fatals", errorHandler.getFatal());
        }
        return errorHandler.exitStatus();
    }
    
    private void reportToLogger(String classifier, List<String> messages) {
        logger.info(classifier + ":");
        for (String message: messages) {
            logger.info(message);
        }
    }

}
