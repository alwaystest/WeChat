package com.software.eric.server.multisocket.net;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import com.software.eric.server.multisocket.event.MultiSocketEvent;
import com.software.eric.server.multisocket.event.MultiSocketListener;
import com.software.eric.server.multisocket.event.MultiSocketSupport;
import com.software.eric.wechat.model.Msg;

/**
 * socket class. for handle socket. pass event to support. connect, read, send,
 * close
 *
 */

public class MultiSocket extends Thread {
	private Socket socket;
	private Properties serverProperties;
	private Properties clientProperties;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private MultiSocketSupport support;

	private String username;

	/**
	 * servers
	 * 
	 * @param socket
	 * @param support
	 */
	public MultiSocket(Socket socket, MultiSocketSupport support) {
		super();
		this.socket = socket;
		this.support = support;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		setDaemon(true);
		start();

	}

	/**
	 * client
	 * 
	 * @param serverConfig
	 * @param clientConfig
	 */
	public MultiSocket(String serverConfig, String clientConfig)
			throws IOException {
		support = new MultiSocketSupport(this);
		// read config file
		if (clientConfig == null) {
			serverProperties = new Properties();
			try {
				serverProperties.loadFromXML(new FileInputStream(serverConfig));
			} catch (IOException e) {
				throw new IOException("加载服务器配置文件失败。", e);
			}
		} else {
			clientProperties = new Properties();
			try {
				clientProperties.loadFromXML(new FileInputStream(clientConfig));
			} catch (IOException e) {
				throw new IOException("加载客户端配置文件失败。", e);
			}
		}
	}

	public void connect() throws IOException {
		if (clientProperties == null) {
			String serverIp = serverProperties.getProperty("serverIp");
			int serverPort = Integer.parseInt(serverProperties
					.getProperty("serverPort"));
			try {
				socket = new Socket(serverIp, serverPort);
			} catch (IOException e) {
				throw new IOException("无法连接到服务器。", e);
			}
		} else {
			String serverIp = serverProperties.getProperty("serverIp");
			int serverPort = Integer.parseInt(serverProperties
					.getProperty("serverPort"));
			String clientIp = clientProperties.getProperty("clientIp");
			int clientPort = Integer.parseInt(clientProperties
					.getProperty("clientPort"));
			try {
				socket = new Socket(InetAddress.getByName(serverIp),
						serverPort, InetAddress.getByName(clientIp), clientPort);
			} catch (IOException e) {
				throw new IOException("无法连接到服务器。", e);
			}
		}
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());

		support.fireMultiSocketEvent(this, MultiSocketEvent.EventType.CONNECT,
				null);

		setDaemon(true);
		start();

	}

	/**
	 * client
	 * 
	 * @param serverConfig
	 */
	public MultiSocket(String serverConfig) throws IOException {
		this(serverConfig, null);
	}

	public void run() {

		while (true) {
			try {
				Msg packet = (Msg) in.readObject();
				support.fireMultiSocketEvent(this,
						MultiSocketEvent.EventType.READ, packet);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				support.fireMultiSocketEvent(this,
						MultiSocketEvent.EventType.CLOSE, null);
				break;
			}

		}
	}

	public void sendPacket(Msg packet) {
		try {
			out.writeObject(packet);
			out.flush();
			support.fireMultiSocketEvent(this,
					MultiSocketEvent.EventType.WRITE, packet);
		} catch (IOException e) {
			support.fireMultiSocketEvent(this,
					MultiSocketEvent.EventType.CLOSE, null);
		}
	}

	public void close() {
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public void addMultiSocketListener(MultiSocketListener l) {
		support.addMultiSocketListener(l);
	}

	public void removeMultiSocketListener(MultiSocketListener l) {
		support.removeMultiSocketListener(l);
	}

	public void addMultiSocketListener(MultiSocketListener l,
			MultiSocketEvent.EventType type) {
		support.addMultiSocketListener(l, type);
	}

	public void removeMultiSocketListener(MultiSocketListener l,
			MultiSocketEvent.EventType type) {
		support.removeMultiSocketListener(l, type);
	}
}
