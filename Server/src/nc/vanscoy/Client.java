package nc.vanscoy;

import java.awt.List;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import nc.sharedInstafeedClasses.ContentRequest;
import nc.sharedInstafeedClasses.Rating;
import nc.sharedInstafeedClasses.RequestType;

public class Client implements Runnable {

	//streams to read and write with client instances
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	boolean go = false;
	int maxRatingsReurn = 25;

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
					TCPServer.output.append("New rating from: " + newRating.UserName + "\n for: " + newRating.RatedBusiness.BusinessName + " recieved\n\n.");
				}
				
				if(obj instanceof ContentRequest) {
					
					ContentRequest cr = (ContentRequest) obj;
					
					ArrayList<Rating> returnRatings = new ArrayList<Rating>();
					
					if(cr.requestType == RequestType.MY_RATINGS) {
						
						TCPServer.output.append("Request for My Ratings recieved from: " + cr.userName + ".\n");
						
						returnRatings.clear();
						
//						TCPServer.output.append("Number of ratings: " + TCPServer.ratings.size() + "\n\n");
						
						for(int i = 0; i < TCPServer.ratings.size() && returnRatings.size() <= maxRatingsReurn; i++) {
//							
//							TCPServer.output.append("Compairing: " + TCPServer.ratings.get(i).UserName + "\n to:" + cr.userName + "\n");
							
							if(TCPServer.ratings.get(i).UserName.equals(cr.userName)) {
								returnRatings.add(TCPServer.ratings.get(i));
							}
						}
						
						try {
							this.out.writeObject(returnRatings);
							out.flush();

							TCPServer.output.append("Request for My Ratings sent (" + returnRatings.size() + " of " + TCPServer.ratings.size() + ").\n");
							
						} catch (Exception e) {
							TCPServer.output.append("Error sending response to client...\n");
						}
						
					}
					
					if(cr.requestType == RequestType.BUSINESS_RATINGS) {
						
						TCPServer.output.append("Request for Business Ratings recieved.\n");
						
						returnRatings.clear();
						
						for(int i = 0; i < TCPServer.ratings.size() && returnRatings.size() <= maxRatingsReurn; i++) {
							if(TCPServer.ratings.get(i).RatedBusiness.BusinessName.equals(cr.business.BusinessName) 
									&& TCPServer.ratings.get(i).RatedBusiness.Address.equals(cr.business.Address)) {
								returnRatings.add(TCPServer.ratings.get(i));
							}
						}
						
						try {
							
							
							
							this.out.writeObject(returnRatings);
							out.flush();
							
							TCPServer.output.append("Request for Business Ratings sent.\n");
							
						} catch (Exception e) {
							TCPServer.output.append("Error sending response to client...\n");
						}
					}
					
//					try {
//						String receivedFeedback = "Content request recived";
//						this.out.writeObject(receivedFeedback);
//						out.flush();
//						TCPServer.output.append("Request for content recieved and sent.\n");
//						
//					} catch (Exception e) {
//						TCPServer.output.append("Error sending response to client...\n");
//					}
				}
								
			} catch (Exception e) {
				//remove from the client list if streams are broken
				TCPServer.clients.remove(this);
			} 
		}
	}
		
}
