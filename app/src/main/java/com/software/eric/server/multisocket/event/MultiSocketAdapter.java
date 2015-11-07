package com.software.eric.server.multisocket.event;

public abstract class MultiSocketAdapter implements MultiSocketListener {

	/* (non-Javadoc)
	 * @see com.software.eric.server.multisocket.event.MultiSocketListener#accept(com.software.eric.server.multisocket.event.MultiSocketEvent)
	 */
	@Override
	public void accept(MultiSocketEvent e) {

	}

	/* (non-Javadoc)
	 * @see com.software.eric.server.multisocket.event.MultiSocketListener#connect(com.software.eric.server.multisocket.event.MultiSocketEvent)
	 */
	@Override
	public void connect(MultiSocketEvent e) {

	}

	/* (non-Javadoc)
	 * @see com.software.eric.server.multisocket.event.MultiSocketListener#read(com.software.eric.server.multisocket.event.MultiSocketEvent)
	 */
	@Override
	public void read(MultiSocketEvent e) {

	}

	/* (non-Javadoc)
	 * @see com.software.eric.server.multisocket.event.MultiSocketListener#write(com.software.eric.server.multisocket.event.MultiSocketEvent)
	 */
	@Override
	public void write(MultiSocketEvent e) {

	}

	/* (non-Javadoc)
	 * @see com.software.eric.server.multisocket.event.MultiSocketListener#close(com.software.eric.server.multisocket.event.MultiSocketEvent)
	 */
	@Override
	public void close(MultiSocketEvent e) {

	}

}
