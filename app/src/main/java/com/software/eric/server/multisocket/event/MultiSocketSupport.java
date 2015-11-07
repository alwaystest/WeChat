package com.software.eric.server.multisocket.event;

import com.software.eric.wechat.model.Msg;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.software.eric.server.multisocket.net.MultiSocket;

public class MultiSocketSupport {
	private Object source;
	private ConcurrentHashMap<MultiSocketEvent.EventType, CopyOnWriteArrayList<MultiSocketListener>> listeners;

	/**
	 * @param source
	 */
	public MultiSocketSupport(Object source) {
		super();
		this.source = source;
		listeners = new ConcurrentHashMap<>();
		for (MultiSocketEvent.EventType type : MultiSocketEvent.EventType
				.values()) {
			listeners
					.put(type, new CopyOnWriteArrayList<MultiSocketListener>());
		}
	}

	/**
	 * 添加 - 全局事件监听器
	 */
	public void addMultiSocketListener(MultiSocketListener l) {
		listeners.get(MultiSocketEvent.EventType.ALL).add(l);
	}

	public void removeMultiSocketListener(MultiSocketListener l) {
		listeners.get(MultiSocketEvent.EventType.ALL).remove(l);
	}

	/**
	 * 添加 - 单事件类型监听器
	 * 
	 * @param l
	 * @param type
	 */

	public void addMultiSocketListener(MultiSocketListener l,
			MultiSocketEvent.EventType type) {
		listeners.get(type).add(l);
	}

	public void removeMultiSocketListener(MultiSocketListener l,
			MultiSocketEvent.EventType type) {
		listeners.get(type).remove(l);
	}

	public void fireMultiSocketEvent(MultiSocket socket,
			MultiSocketEvent.EventType type, Msg packet) {
		this.fireMultiSocketEvent(new MultiSocketEvent(source, socket, type,
				packet));
	}

	public void fireMultiSocketEvent(MultiSocketEvent e) {
		/*
		 * 向所有全局事件监听器分发事件
		 */
		Iterator<MultiSocketListener> allIter = listeners.get(MultiSocketEvent.EventType.ALL).iterator();
		while(allIter.hasNext()){
			fireEvent(e, allIter.next());
		}
		/*
		 * 向单事件类型监听器分发事件
		 */
		Iterator<MultiSocketListener> iter = listeners.get(e.getType()).iterator();
		while(iter.hasNext()){
			fireEvent(e, iter.next());
		}
	}

	/**
	 * 事件分发，将参数事件分发给参数监听器
	 * 
	 * @param e
	 * @param l
	 */
	private void fireEvent(MultiSocketEvent e, MultiSocketListener l) {
		switch (e.getType()) {
		case ACCEPT:
			l.accept(e);
			break;
		case CONNECT:
			l.connect(e);
			break;
		case READ:
			l.read(e);
			break;
		case WRITE:
			l.write(e);
			break;
		case CLOSE:
			l.close(e);
			break;
		default:
			break;
		}
	}
}
