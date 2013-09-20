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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
/**
 *
 * @author geoff
 */
public class FileDownloader {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Download a file and save it temporarily.
     * @param URL
     * @return Filename of temporary file downloaded
     */
    public String downloadFile(String urlString) throws IOException {
        logger.info("Downloading file from '{}'...", urlString);
        File tempfile = File.createTempFile("jxmlvalidator-temp", ".xml");
        tempfile.deleteOnExit();
        URL url = new URL(urlString);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(tempfile);
        IOUtils.copy(is, os);
        is.close();
        os.close();
        String filename = tempfile.getAbsolutePath();
        logger.debug("...file downloaded to '{}'", filename);
        return filename;
    }
    
}
