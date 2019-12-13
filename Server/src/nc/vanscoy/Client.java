package nc.vanscoy;

import java.awt.List;
import java.io.*;
import java.net.Socket;

import nc.sharedInstafeedClasses.ContentRequest;
import nc.sharedInstafeedClasses.Rating;

public class Client implements Runnable {

	//streams to read and write with client instances
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	boolean go = false;

	public Client(Socket socket) {
		try {
			//create IO streams
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
//			
			String test = "Initial message from server";
			
			out.writeObject(test);
			out.flush();
			
			//add client to the server's client list
			TCPServer.clients.add(this);
			TCPServer.output.append("Client " + TCPServer.clients.size() + " connected...\n");
			//start a thread to read incoming client messages
			this.go = true;
			Thread thread = new Thread(this);
			thread.start();
		} catch (IOException e) {}
	}
	
	@Override
	public void run() {
		
		while(go) {
			try {
								
				Object obj = in.readObject();
				

//				if(obj instanceof String) {
//					TCPServer.messages.add(obj.toString());
//				}
				
				if(obj instanceof Rating) {
					
					Rating newRating = (Rating)obj;
					TCPServer.ratings.add(newRating);
					TCPServer.output.append("New rating for " + newRating.RatedBusiness.BusinessName + " recieved\n.");
				}
				
				if(obj instanceof ContentRequest) {
					try {
						String test = "Content request recived";
						this.out.writeObject(test);
						out.flush();
						TCPServer.output.append("Request for content recieved and sent.\n");
						
					} catch (Exception e) {
						TCPServer.output.append("Error sending response to client...\n");
					}
				}
								
			} catch (Exception e) {
				//remove from the client list if streams are broken
				TCPServer.clients.remove(this);
			} 
		}
	}
		
}
