package com.software.eric.server.multisocket.event;

import java.util.EventListener;

public interface MultiSocketListener extends EventListener {
	void accept(MultiSocketEvent e);
	void connect(MultiSocketEvent e);
	void read(MultiSocketEvent e);
	void write(MultiSocketEvent e);
	void close(MultiSocketEvent e);
}
