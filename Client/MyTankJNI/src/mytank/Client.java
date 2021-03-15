package mytank;

import java.io.*;
import java.net.*;
 
public class Client {
	String username;
	int clientID;
	String hostname = null;
	int port = 0;
	ObjectOutputStream out = null;
	ObjectInputStream in = null;
	Socket socket = null;
    private final static byte[] INIT = {0x7, 0x1};
    private final static byte[] LOG_SUCCESS = {0x7, 0x2};

	byte SOCK_REQ_DO_INIT_CONNECTION = 0;
	byte SOCK_REQ_CHECK_IS_ALLOWED_TO_ACT = 1;
	byte SOCK_REQ_GET_CURRENT_POSITOIN = 2;
	byte SOCK_REQ_GET_MAP = 3;
	byte SOCK_REQ_DO_MOVE = 4;
	byte SOCK_REQ_DO_SHOOT = 5;
	byte SOCK_REQ_CHECK_IS_GAME_STARTED = 6;

	final byte SOCK_DATA_INIT_CONNECTION = 0;
	final byte SOCK_DATA_LOGIN_SUCCESS = 1;
	final byte SOCK_DATA_ALLOWED_TO_ACT = 1;
	final byte SOCK_DATA_GAME_IS_STARTED = 1;
	
	/**
	 * 
	 * @param host : host of the server (localhost or IP)
	 * @param iport : the port used by the server
	 * @param name : your name
	 * @param id : 0 for your tank and 1 for the opponent tank
	 */
	Client(String host, int iport, String name, int id) {
		hostname = host;
		port = iport;
		username = name;
		clientID = id;
	}
 
	public void connectToServer() throws IOException {
		Object obj = null;
		
		SocketLogic sockLog = new SocketLogic();
 
		try {
			socket = new Socket(hostname, port);
 
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			
			// receive init command
			obj = in.readObject();
			// sending init command
			sockLog.processData(obj, out);
				
			obj = in.readObject();
			sockLog.processData(obj, out);
			
		} catch (UnknownHostException e) {
			System.err.println("Cannot find the host: " + hostname);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't read/write from the connection: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) { 
				e.printStackTrace();
		}
	}
	
	public void disconnectFromServer() throws Exception {
		System.out.println("[SOCKET] CLOSE THE CONNECTION");
		out.close();
		in.close();
		socket.close();
	}


	public byte[][] getMap() throws Exception {
		out.writeObject(new byte[]{SOCK_REQ_GET_MAP});

		byte obj[] = (byte[])in.readObject();
		int col = obj[0];
		int row = obj[1];
		int offset = 2;
		byte[][] mapData = new byte[row][col];

		for(int i = 0; i < obj.length - offset; i++){
			mapData[i/row][i%col] = obj[i+offset];
		}

		return mapData;
	}

	public boolean isGameStarted() throws Exception {
		out.writeObject(new byte[]{SOCK_REQ_CHECK_IS_GAME_STARTED});

		byte obj[] = (byte[]) in.readObject();
		if(obj[0] == SOCK_REQ_CHECK_IS_GAME_STARTED  && obj[1] == SOCK_DATA_GAME_IS_STARTED)
			return true;

		return false;
	}

	public byte[] getCurrentPosition() throws Exception{
		out.writeObject(new byte[]{SOCK_REQ_GET_CURRENT_POSITOIN});

		byte obj[] = (byte[]) in.readObject();
		if(obj[0] == SOCK_REQ_GET_CURRENT_POSITOIN){
			return new byte[] {obj[1], obj[2]};
		}

		return new byte[] {-1, -1};
	}
	
	/**
	 * 
	 * @param dir : direction of the moving     
	 * byte DIR_UP = 0;
	 * byte DIR_RIGHT = 1;
	 * byte DIR_DOWN = 2;
	 * byte DIR_LEFT = 3;
	 * @throws IOException
	 */
	public void move(byte dir) throws IOException {
		if(dir >= 0)
			out.writeObject(new byte[] {SOCK_REQ_DO_MOVE, dir});
	}

	/**
	 * 
	 * @param dir : direction of the shooting     
	 * byte DIR_UP = 0;
	 * byte DIR_RIGHT = 1;
	 * byte DIR_DOWN = 2;
	 * byte DIR_LEFT = 3;
	 * @throws IOException
	 */
	public void shoot(byte dir) throws IOException {
		if(dir >= 0)
			out.writeObject(new byte[] {SOCK_REQ_DO_SHOOT, dir});
	}

	public class SocketLogic {
		public void processData(Object obj, ObjectOutputStream out) {
			byte[] data = (byte[]) obj;

			if (data[0] == SOCK_REQ_DO_INIT_CONNECTION) {
				switch (data[1]) {
					case SOCK_DATA_INIT_CONNECTION: {
						//INIT state from server
						System.out.println("[SOCKET] INIT FROM SERVER REICIEVED");

						byte dataToSend[] = {SOCK_REQ_DO_INIT_CONNECTION, (byte)clientID};//addArray(SEND_USER, (byte)clientID);
						byte dataLoc[] = addArray(dataToSend, username.getBytes());

						try {
							System.out.println("[SOCKET] SENDING LOGIN INFO as \"" + username + "\"");
							out.writeObject(dataLoc);
						} catch (Exception e) {}
					} break;
					case SOCK_DATA_LOGIN_SUCCESS: {
						System.out.println("[SOCKET] LOGIN SUCCESS");
					} break;
				}
			}
		}
	}
	
	private byte[] addArray(byte[] a, byte[] b) {
		byte[] dataRet = new byte[a.length + b.length];
		for (int i = 0; i < dataRet.length; i++) {
			if (i < a.length)
				dataRet[i] = a[i];
			else
				dataRet[i] = b[i - a.length];
		}
		return dataRet;
	}
}