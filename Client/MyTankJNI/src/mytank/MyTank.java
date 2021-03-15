package mytank;

import java.io.*;

public class MyTank {
	static boolean gameRun = true;
	
	// your dll (put the dll on src folder)
	static {
	  System.loadLibrary("MyTankDll");
	}
	
	// Declare a native method sayHello() that receives nothing and returns void
	public native byte[] doSomething(int x, int y, byte[][] map);

	public static void main(String args[]){
		
		MyTank myTank = new MyTank();
		
		try {
			Client client = new Client("localhost", 9021, "Your Name", 0);
			client.connectToServer();
			DataInputStream inputLine = new DataInputStream(new BufferedInputStream(System.in));
			
			while (gameRun) {
				if(client.isGameStarted())
				{
					// get the data from server
					byte[][] map = client.getMap();
					int x = client.getCurrentPosition()[0];
					int y = client.getCurrentPosition()[1];
					
					// get the commands from C++
					byte[] command = myTank.doSomething(x, y, map);
					
					// send commands to server
					client.move(command[0]);
					client.shoot(command[1]);
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			System.out.println("Something falied: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
