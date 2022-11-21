
// PROG1415 Test 1 - Hands On
// James Hare

package prog1415;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.lang.Object;


//a java server program
public class TCPServer extends JFrame implements Runnable, WindowListener {

	private static final long serialVersionUID = 1L;

	//Server
	private ServerSocket server;
	private int port = readPort();

	private boolean go = false;

	static JTextArea output = new JTextArea("Ready...\n");
	static List<Client> clients = new ArrayList<Client>();
	static List<Message> messages = new ArrayList<Message>();
	
	// TODO: Create a new thread for looping through messages that are commands.
	static List<Message> commands = new ArrayList<Message>();

	public	TCPServer(int port) {
		this.port = port;

		//build interface		
		output.setEditable(false);
		this.getContentPane().add(new JScrollPane(output));
		this.setTitle("Server");
		this.setBounds(100,100,300,500);
		this.setVisible(true);
		this.addWindowListener(this);

		//start server main thread
		Thread acceptThread = new Thread(this);
		acceptThread.setPriority(Thread.MAX_PRIORITY);
		acceptThread.start();

		this.setVisible(true);
	}

	@Override
	public void run() {
		try {
			//construct the server
			server = new ServerSocket(port);
			//start additional server threads
			go = true;
			new BroadCast().start();
			new ProcessCommand().start();
		} catch (Exception e) {
			output.append("Server launch failed...\n");
			return;
		}

		//loop to receive client connections
		int count = 1;
		while(go) {
			try {
				output.append("Waiting...\n");
				Socket socket = server.accept();
				//start a new process for each client
				Client client = new Client(socket);
				// show new clients a list of currently connected users
				client.out.writeObject("\nFor list of commands, type: \"/help\" \n\nCONNECTED USERS:");
				for (Client c : clients){
					client.out.writeObject("-- " + c.getLoginName());
					client.out.flush();
				}
				client.out.writeObject("");

				for (Client c : clients) {
					c.out.writeObject(client.getLoginName() + " has joined the server...");
				}
				client.out.writeObject(" ");
			} catch (IOException e) {
				output.append("Attempt to connect " + count + " failed...\n");
				if(count++ <= 3) 
					continue;
				else
					break;
			}
		}
		output.append("Server is no longer accepting clients...\n");
	}

	public int readPort() {
		try {
			String file ="src/prog1415/resources/Requested_Port.txt";

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			reader.close();
			return Integer.parseInt(line);
		}
		catch (Exception e) {
			return 8000;
		}
	}

	//a server thread to broadcast text messages to all connected clients
	class BroadCast extends Thread {
		
		@Override
		public void run() {	

			output.append("Broadcasting enabled...\nLaunching server on port " + port + "...\n");
			while(go) {
				//allow other threads to run
				Thread.yield();

				//check for command messages
				if(commands.size() > 0 && clients.size() > 0) {
					try {
						currentThread().wait();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				//check for clients and messages
				if(messages.size() > 0 && clients.size() > 0) {
					Message msg = messages.get(0);

					if (msg.IsWhisper() == false && msg.IsHelp() == false && msg.RequestUsers() == false)
					{
						for(int x=0;x<clients.size();x++)
							try {
								clients.get(x).out.writeObject(msg.toString());	
							}
						catch (IOException e) {
							output.append("Error writing to client...\n");
							clients.remove(x);
						}
					}
					
					messages.remove(0);
				}
			}
		}	
	}
	
	class ProcessCommand extends Thread {
		@Override
		public void run() {
			while(go) {
				Thread.yield();
				if (commands.size() > 0 && clients.size() > 0) {
					Message msg = commands.get(0);
					
					if (msg.IsWhisper() == true) {
						for(int x=0;x<clients.size();x++)
							try {
								if (clients.get(x).getLoginName().compareTo(msg.getTarget()) == 0 
										&& clients.get(x).getLoginName().length() == msg.getTarget().length()) {
									clients.get(x).out.writeObject(msg.getUser() + " <whisper> - " + msg.getWhisper());
								}
								else if (clients.get(x).getLoginName().compareTo(msg.getUser()) == 0
										&& clients.get(x).getLoginName().length() == msg.getTarget().length()) {
									clients.get(x).out.writeObject(msg.getUser() + " <whisper> " + msg.getTarget() + " - " + msg.getWhisper());
								}
								else {
									System.out.println("No match...");
								}
							}
						catch (IOException e) {
							output.append("Error writing to client...\n CLIENT REMOVED\n");
							clients.remove(x);
						}
					}
					else if (msg.IsHelp() == true) {
						for(int x=0;x<clients.size();x++)
							try {
								if (clients.get(x).getLoginName().compareTo(msg.getUser()) == 0) {
									clients.get(x).out.writeObject(msg.getHelp());
								}
								else
									System.out.println("No match...");
							}
						catch (IOException e) {
							output.append("Error writing to client...\nCLIENT REMOVED\n");
							clients.remove(x);
						}
					}
					else if (msg.RequestUsers() == true) {
						for(int x=0;x<clients.size();x++)
							try {
								if (clients.get(x).getLoginName().compareTo(msg.getUser()) == 0) {
									clients.get(x).out.writeObject("\nCONNECTED USERS:");
									for (Client c : clients) {
										clients.get(x).out.writeObject("-- " + c.getLoginName());
									}
								}
								else
									continue;
							}
						catch (IOException e) {
							output.append("Error writing to client...\nCLIENT REMOVED\n");
							clients.remove(x);
						}
					}
					
					commands.remove(0);
				}
			}
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		this.go = false;
		for(int x=0;x<clients.size();x++)
			clients.get(x).go = false;
		try {
			server.close();
		} catch (IOException e) {}
		System.exit(0);

	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	public static void main(String[] args) {
		new PortConfirmFrame();
	}
}

