/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.fourthline.cling.demo.osgi.device.light.variables;

import org.fourthline.cling.demo.osgi.device.light.model.Dimmer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPLocalStateVariable;
import org.osgi.service.upnp.UPnPService;
import org.fourthline.cling.demo.osgi.device.light.Activator;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Bruce Green
 */
public class LoadLevelStatusStateVariable implements Observer, UPnPLocalStateVariable {

    final static String UPNP_EVENT_TOPIC = "org/osgi/service/upnp/UPnPEvent";

    private UPnPDevice device;
    private UPnPService service;
    private Dimmer dimmer;

    public LoadLevelStatusStateVariable(UPnPDevice device, UPnPService service, Dimmer dimmer) {
        this.device = device;
        this.service = service;
        this.dimmer = dimmer;

        dimmer.addObserver(this);
    }

    @Override
    public String getName() {
        return "LoadLevelStatus";
    }

    @Override
    public Class getJavaDataType() {
        return Integer.class;
    }

    @Override
    public String getUPnPDataType() {
        return UPnPLocalStateVariable.TYPE_UI1;
    }

    @Override
    public Object getDefaultValue() {
        return 0;
    }

    @Override
    public String[] getAllowedValues() {
        return null;
    }

    @Override
    public Number getMinimum() {
        return 0;
    }

    @Override
    public Number getMaximum() {
        return 100;
    }

    @Override
    public Number getStep() {
        return null;
    }

    @Override
    public boolean sendsEvents() {
        return true;
    }

    @Override
    public Object getCurrentValue() {
        return dimmer.getLevel();
    }

    public void setCurrentValue(Integer value) {
        dimmer.setLevel(value);
    }

    @Override
    public void update(Observable o, Object arg) {
        EventAdmin eventAdmin = Activator.getPlugin().getEventAdmin();
        if (eventAdmin != null) {
            Dictionary<String, Object> values = new Hashtable<String, Object>();
            values.put(getName(), getCurrentValue());

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(UPnPDevice.UDN, device.getDescriptions(null).get(UPnPDevice.UDN));
            properties.put(UPnPService.ID, service.getId());
            properties.put("upnp.events", values);

            eventAdmin.sendEvent(new Event(UPNP_EVENT_TOPIC, properties));
        }
    }
}
