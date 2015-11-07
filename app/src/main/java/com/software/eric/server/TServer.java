import com.software.eric.wechat.model.Msg;

import com.software.eric.server.multisocket.event.MultiSocketAdapter;
import com.software.eric.server.multisocket.event.MultiSocketEvent;
import com.software.eric.server.multisocket.net.ConfigFiles;
import com.software.eric.server.multisocket.net.MultiServerSocket;
import com.software.eric.server.multisocket.net.MultiSocket;

/**
 * Created by Mzz on 2015/11/4.
 */
public class TServer {
	public static void main(String[] args) throws Exception {
        try{
        	final MultiServerSocket server = new MultiServerSocket(
					ConfigFiles.SERVER_CONFIG);
        	server.addMultiSocketListener(new MultiSocketAdapter() {

				@Override
				public void accept(MultiSocketEvent e) {
					//send user login msg to others.
					String username = e.getSocket().getUsername();
					server.sendRemoveParamMessage(username,e.getPacket());
					//send userList.
					server.sendMessage(username, Msg.createOnlineListPacket(server.getOnlineUserList()));
					System.out.println(username+"\t"+"login");
				}

				@Override
				public void read(MultiSocketEvent e) {
					Msg packet = e.getPacket();
					System.out.println(packet.getMsg());
					if(packet.getMsgType() == Msg.SEND_MESSAGE){
						//send msg to others.
						server.sendRemoveParamMessage(e.getSocket().getUsername(), packet);
					}
				}

				@Override
				public void close(MultiSocketEvent e) {
					MultiSocket clientSocket = e.getSocket();
					String username = clientSocket.getUsername();
					System.out.println(username + "\t" + "log out");
					server.removeUser(username);//call close()
					// 向在线用户发送-用户离开packet
					server.sendMessage(null, Msg.createLogoutPacket(username));
				}
        		
			});
        	server.start();
        }catch (Exception e){
        	e.printStackTrace();
        }
    }
}
