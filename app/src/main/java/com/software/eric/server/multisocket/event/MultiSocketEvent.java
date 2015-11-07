package com.software.eric.server.multisocket.event;

import java.util.EventObject;

import com.software.eric.wechat.model.Msg;
import com.software.eric.server.multisocket.net.MultiSocket;

public class MultiSocketEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private MultiSocket socket;
	private EventType type;
	private Msg packet;
	


	
	/**
	 * @param source
	 * @param socket
	 * @param type
	 * @param packet
	 */
	public MultiSocketEvent(Object source, MultiSocket socket, EventType type,
			Msg packet) {
		super(source);
		this.socket = socket;
		this.type = type;
		this.packet = packet;
	}




	public enum EventType{
		ACCEPT,CONNECT,READ,WRITE,CLOSE,ALL;
	}




	/**
	 * @return the socket
	 */
	public MultiSocket getSocket() {
		return socket;
	}




	/**
	 * @return the type
	 */
	public EventType getType() {
		return type;
	}




	/**
	 * @return the packet
	 */
	public Msg getPacket() {
		return packet;
	}
	
	

}
