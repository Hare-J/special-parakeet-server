
// PROG1415 Test 1 - Hands On
// James Hare

package prog1415;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {

	//streams to read and write with client instances
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	boolean go = false;
	
	private String login = null;
		
	public String getLoginName() {
		return this.login;
	}
	
	public Client(Socket socket) {
		try {
			//create IO streams
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			
			getLogin();
						
			//add client to the server's client list
			TCPServer.clients.add(this);
			TCPServer.output.append("Client " + login + " connected...\n");
			//start a thread to read incoming client messages
			this.go = true;
			Thread thread = new Thread(this);
			thread.start();
		} catch (IOException e) {}
	}
	
//	@Override
//	public void finalize() {
//		try {
//			TCPServer.output.append("Client " + login + " disconnected...\n");
//			TCPServer.messages.add(new Message("<server>", login + " has left..."));
//		}
//		catch (Exception e) {
//			TCPServer.output.append("Error trying to destruct " + login);
//		}
//	}
	
	private void getLogin() {
		try {
			this.login = in.readObject().toString();
		}
		catch (Exception e) {
			System.out.println("Error");
		}
	}
	
	@Override
	public void run() {
		while(go) {
			try {
				//wait until a message comes from the client
				Object obj = in.readObject();
				//add valid message to the queue
				if(obj instanceof String) {

					if (obj.toString().length() > 0 && obj.toString().charAt(0) != '/') {	

							TCPServer.messages.add(new Message(login, obj.toString()));
					}
					else {
						TCPServer.commands.add(new Message(login, obj.toString()));
					}
				}
			} catch (Exception e) {
				//remove from the client list if streams are broken
				TCPServer.clients.remove(this);
			} 
		}
	}
}
