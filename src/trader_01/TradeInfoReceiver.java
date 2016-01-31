package trader_01;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import sub.TradeInfoEty;

public class TradeInfoReceiver implements Runnable{
	int portNumber;
	String socketName = "";

	public TradeInfoReceiver(int portNumber, String socketName) {
		this.portNumber = portNumber;
		this.socketName = socketName;
	}

	@SuppressWarnings("resource")
	public void run() {
		DatagramSocket socket = null;
		DatagramPacket inPacket;
		byte[] inMsg = new byte[300];

		try {
			socket = new DatagramSocket(portNumber);
		} catch (SocketException e) {
			System.out.println("[socket = new DatagramSocket(portNumber)] failed with [" + socketName + "]");
			e.printStackTrace();
		}

		while (true) {
			// create packet to store the received data
			inPacket = new DatagramPacket(inMsg, inMsg.length);

			try {
				// store the data in the packet
				socket.receive(inPacket);
			} catch (IOException e) {
				System.out.println("[socket.receive(inPacket)] failed with [" + socketName + "]");
				e.printStackTrace();
			}

			TradeInfoEty trEty = new TradeInfoEty(inPacket);
			
			Launcher.TRADE_INFO_Q_HASH.get(trEty.getStockName()).add(trEty);
		}
	}
}
