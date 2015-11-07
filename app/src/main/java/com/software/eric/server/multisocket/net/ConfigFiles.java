package com.software.eric.server.multisocket.net;
/**
 * define xml files' location
 * in these files, server IP and Port are defined.
 * if it's necessary, use client.xml to define client IP and Port.
 *
 */
public interface ConfigFiles {
	String SERVER_CONFIG = "config/server.xml";
	String CLIENT_CONFIG = "config/client.xml";
}
