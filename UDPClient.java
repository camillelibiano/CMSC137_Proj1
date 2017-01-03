import java.net.*;
import java.util.*;
import java.io.*;

public class UDPClient{
	DatagramSocket clientSocket;
	DatagramPacket packet;


	InetAddress ipaddress;

	boolean isConnected = false;

	String status = "";

	byte[] sendData = new byte[1024];

	String messageReceived = "";

	int SYN;
	int ACK;
	int FIN;

	int SYN_NUM = 0;
	int ACK_NUM = 0;

	public UDPClient() throws IOException{
		clientSocket = new DatagramSocket();
		

		ipaddress = InetAddress.getByName("localhost");

		packet = new DatagramPacket(sendData, sendData.length, ipaddress, 10224);
	}

	//connect to server
	public void connectToServer(){
		byte[] receiveD = new byte[1024];

		SYN = 1;
		ACK = 0;
		FIN = 0;


		if(!isConnected){
			System.out.println("CONNECTING TO SERVER....");
			status = "CONNECTING";
			sendData = status.getBytes();
			packet.setData(sendData);
			System.out.println("SENDING DATA TO SERVER: " +status);
			System.out.print("SYN BIT SENT TO SERVER: " +SYN);
			System.out.println("");

			try{
				clientSocket.send(packet);
				packet = new DatagramPacket(receiveD, receiveD.length);
				clientSocket.receive(packet);
			}catch (IOException e){
				e.printStackTrace();
			}

			receiveD = packet.getData();
			String dataReceived = new String(receiveD);
			System.out.println("RECEIVED DATA FROM SERVER: "+dataReceived);

			if(dataReceived.startsWith("SUCCESS")){
				SYN = 0;
				ACK = 1;
				FIN = 0;

				System.out.println("ACK BIT FROM SERVER RECEIVED");
				System.out.println("SYN BIT FROM SERVER RECEIVED");
				System.out.println("");

				status = "";
				status = "ESTABLISHED";
				sendData = status.getBytes();
				packet.setData(sendData);

				System.out.println("SENDING DATA TO SERVER: " +status);
				System.out.println("");
				System.out.print("ACK BIT SENT TO SERVER: " +SYN);

				try {
					clientSocket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				isConnected = true;
				System.out.println("CONNECTION TO SERVER ESTABLISHED!");
			}
		}

	}

	//receivedata from server and send response
	public void receiveData(){
		byte[] msg = new byte[1024];
		String toSend = "";

		while(true){
			try{


				clientSocket.setSoTimeout(1000);
				packet = new DatagramPacket(msg, msg.length);
				
				clientSocket.receive(packet);

				String msgReceived = new String(packet.getData());
				System.out.println("RECEIVING <<< "+msgReceived);

				if(msgReceived.startsWith("SENT")){
					System.out.println("Message from server successfully received!");
					break;
				}else{

					String[] tokens = msgReceived.split("\\|");
					SYN_NUM = Integer.parseInt(tokens[1].trim());
					// System.out.println(SYN_NUM);
					messageReceived += tokens[0];
					System.out.println(messageReceived);

					try{
						ACK_NUM ++;

						toSend = Integer.toString(SYN_NUM);
						sendData = toSend.getBytes();
						packet.setData(sendData);

						System.out.println("SENDING DATA TO SERVER: " +toSend);

						
						clientSocket.send(packet);
						

					}catch(IOException e){
						e.printStackTrace();
					}
				}



			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}


	public static void main (String[] args) throws Exception{
		UDPClient client = new UDPClient();
		client.connectToServer();
		client.receiveData();
	}
}