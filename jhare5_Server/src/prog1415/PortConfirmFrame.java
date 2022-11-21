
// PROG1415 Test 1 - Hands On
// James Hare

package prog1415;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class PortConfirmFrame extends JFrame {
	
	private int port = readPort();
	
	private PortConfirmFrame frame;
	JTextField input = new JTextField(15);
	JButton submit = new JButton("Connect");
	JButton exit = new JButton(" Exit ");
	JTextArea output = new JTextArea(200,200);

	JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));

	//int accountNum = txtAccount.getText();

	public PortConfirmFrame() 
	{
		this.setTitle("Confirm Port");
		this.setBounds(100, 100, 300, 150);
		Container con = this.getContentPane();
		south.add(exit);
		north.add(input);
		south.add(submit);
		con.add(north,BorderLayout.NORTH);
		con.add(output);
		con.add(south, BorderLayout.SOUTH);
		frame = this;
		
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(input.getText().length() > 0) {
					new ConfirmPort(port, frame);
				}
			}
		});
		
		input.setText(Integer.toString(port));
		output.setEditable(false);
		output.setText("The requested port is " + port + "... \nWould you like to connect now or change ports?");
		exit.addActionListener(new Exit());

		this.setVisible(true);
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
}


class ConfirmPort implements ActionListener
{
	private PortConfirmFrame frame;
	private int port;

	// constructor
	public ConfirmPort(int port, PortConfirmFrame frame)
	{
		this.frame = frame;
		this.port = Integer.parseInt(frame.input.getText());
		try {
			writePort();
		}
		catch(Exception e) {
			System.out.println("An IO error occurred");
		}
	}

	public void writePort() throws IOException {
		try {
		String file = "src/prog1415/resources/Requested_Port.txt";
		FileWriter fw = new FileWriter(file, false);
		BufferedWriter writer = new BufferedWriter(fw);
		
		writer.write(Integer.toString(this.port));
		writer.close();
		frame.output.setText("Launching server on port " + this.port + "...");
		
		frame.setVisible(false);
		
		if (this.port >= 2000)
			new TCPServer(this.port);
		else
			new TCPServer(8000);
		}
		catch (IOException e) {
			frame.setVisible(false);
			new TCPServer(8000);
			System.out.println("An IO error occurred");
		}
	}
	
	// event
	@Override
	public void actionPerformed(ActionEvent arg0)
	{	
		try {
		writePort();
		}
		catch (IOException e) {
			System.out.println("An error occurred while attempting to overwrite the requested port number...");
		}
		
	}
}

class Exit implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		System.exit(0);
	}
}