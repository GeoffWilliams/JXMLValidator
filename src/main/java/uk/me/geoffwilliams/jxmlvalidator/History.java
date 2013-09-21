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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author geoff
 */
public class History {

    private List<String> historyList = null;
    private Preferences prefs = Preferences.userNodeForPackage(History.class);
    private static final String PREF_KEY = "historyList";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public History() {
        // load history list from file...
        byte[] historyListBytes = prefs.getByteArray(PREF_KEY, null);
        if (historyListBytes == null) {
            // no history available - first run?
            logger.debug("no history available");
            historyList = new ArrayList<String>();
        } else {
            logger.debug("loading history...");
            try {
                historyList = (List<String>) bytes2Object(historyListBytes);
                logger.debug("...done!");
            } catch (IOException ex) {
                logger.debug("IOException loading history: {}", ex.getMessage());
                historyList = new ArrayList<String>();
            } catch (ClassNotFoundException ex) {
                logger.debug("ClassNotFoundException loading history: {}", ex.getMessage());
                historyList = new ArrayList<String>();
            }
        }
    }

    public List<String> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<String> historyList) {
        this.historyList = historyList;
    }

    public void save(String historyItem) {
        logger.debug("adding item to history '{}'", historyItem);
        boolean duplicate = false;
        for (String item: historyList) {
            if (item.equals(historyItem)) {
                duplicate = true;
                break;
            }
        }
        
        if (! duplicate) {
            historyList.add(0, historyItem);
            save();
        } else {
            logger.debug("duplicate entry: {}", historyItem);
        }
    }
    
    public void clear() {
        historyList.clear();
        save();
    }

    private void save() {
        logger.debug("saving history...");
        try {
            prefs.putByteArray(PREF_KEY, object2Bytes(historyList));
            logger.debug("saved history OK");
        } catch (IOException ex) {
            // error saving history list
            logger.debug("IOException saving history: {}", ex.getMessage());
        }
    }

    private byte[] object2Bytes(Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        return baos.toByteArray();
    }

    private Object bytes2Object(byte raw[]) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(raw);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        return o;
    }
}
