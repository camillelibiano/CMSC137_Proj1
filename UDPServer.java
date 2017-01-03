import java.net.*;
import java.util.*;
import java.io.*;

public class UDPServer {
	DatagramSocket serverSocket;
	DatagramPacket packet;

	int SYN;
	int ACK;
	int FIN;

	int SYN_NUM;
	int ACK_NUM;

	String status = "";

	byte[] sendData = new byte[1024];
	byte[] receiveData = new byte[1024];


	double[] dropProbability = {0.00, 0.25, 0.50, 0.75, 100.00};
	String dataToSend = "maria camille libiano";
	
	public UDPServer() throws SocketException{
		serverSocket = new DatagramSocket(10224);
		byte[] sendData = new byte[1024];
		packet = new DatagramPacket(sendData, sendData.length);

		SYN_NUM = 0;
		ACK_NUM = 0;
		SYN     = 0;
		ACK 	= 0;
		FIN 	= 0;
	}


	public void waitConnection(){
		
		System.out.println("Waiting for connections ...");

		try{
			serverSocket.receive(packet);
		}catch(IOException e){
			e.printStackTrace();
		}

		receiveData = packet.getData();

		String dataReceived = new String(receiveData);
		System.out.println("RECEIVED DATA FROM CLIENT: "+dataReceived);

		if(dataReceived.startsWith("CONNECTING")){
			System.out.println("SYN BIT RECEIVED FROM CLIENT");

			ACK = 1;
			SYN = 1;

			status = "";
			status = "SUCCESS";
			sendData = status.getBytes();
			packet.setData(sendData);

			try{
				System.out.println("SENDING DATA TO CLIENT: " +status);
				System.out.println("ACK BIT SENT TO CLIENT");
				System.out.println("SYN BIT SENT TO CLIENT");
				serverSocket.send(packet);
			}catch(IOException e){
				e.printStackTrace();
			}

			try{
				packet = new DatagramPacket (receiveData, receiveData.length);
				serverSocket.receive(packet);
				String newRD = new String(packet.getData());
				System.out.println(newRD);

				if(newRD.startsWith("ESTABLISHED")){
					System.out.println("CONNECTION WITH CLIENT ESTABLISHED!");
					sendData();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}

	}

	//for packet dropping
	public boolean randomDropPackets(double per){
		double rand = Math.random();

		if(per == 0.00){
			return true;
		}else if(per == 0.25){
			 if(rand <= 0.25){
				return true;
			}else{
				return false;
			}	
		}else if(per == 0.50){
			 if(rand <= 0.50){
				return true;
			}else{
				return false;
			}	
		}else if(per == 0.75){
			if(rand <= 0.75){
				return true;
			}else{
				return false;
			}	
		}else if(per == 100.00){
			if(rand == 100.00){
				return true;
			}
		}else{
			return false;
		}
	}

	//send data to client
	public void sendData(){
		while(true){
			sendData = dataToSend.getBytes();
			Random ran = new Random();
			double probability = dropProbability[ran.nextInt(5)];

			String toSend = "";

			if(SYN_NUM < sendData.length){
			
				if(!randomDropPackets(probability)){
					try{
						if(sendData.length >= SYN_NUM){
							byte[] perByte = new byte[1];
							toSend = dataToSend.charAt(SYN_NUM)+"|"+SYN_NUM;

							System.out.println("SENDING >>> "+toSend);
							perByte = toSend.getBytes();	
							packet.setData(perByte);

							serverSocket.send(packet);
						}
						try{
							Thread.sleep(1000);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
						SYN_NUM++;
					}catch (IOException e){
						e.printStackTrace();
					}
				}else{
					System.out.println("PACKET DROPPED");
					System.out.println("");
				}

			}else{
				byte[] sendPerByte = new byte[1];
				toSend = "SENT";
				sendPerByte = toSend.getBytes();
				packet.setData(sendPerByte);
				try{
					serverSocket.send(packet);
				}catch(IOException e){
					e.printStackTrace();
				}
				System.out.println("The message has been sent to the client!");
				break;
			}
		}

	}	

	public static void main (String[] args) throws Exception{
		UDPServer server = new UDPServer();
		server.waitConnection();
	}
}