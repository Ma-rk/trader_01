package trader_01;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import sub.OrderInfoEty;

public class OrderIssuer implements Runnable {

	@Override
	public void run() {
		Socket socket;
System.out.println("Socket socket;");
		try {
			socket = new Socket(Launcher.TARGET_IP, Launcher.TARGET_PORT);
			System.out.println("socket = new Socket(Launcher.TARGET_IP, Launcher.TARGET_PORT);");
			OutputStream os = socket.getOutputStream();
			System.out.println("OutputStream os = socket.getOutputStream();");
			DataOutputStream dos = new DataOutputStream(os);
			System.out.println("DataOutputStream dos = new DataOutputStream(os);");
			while (true) {
				System.out.println("whlie");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				OrderInfoEty oie = Launcher.ORDER_INFO_Q.poll();
				if (oie != null) {
					int orderNo = getOrderNo();
					String orderMsg = "," + Integer.toString(orderNo) + "," + oie.getAction() + "," + oie.getCode()
							+ "," + oie.getBuySell() + "," + oie.getQty() + "," + oie.getPrice() + ","
							+ oie.getFeedId();

					int orderMsgLength = orderMsg.length();
					if (orderMsgLength <= 97)
						orderMsg = (orderMsgLength + 2) + orderMsg;
					else
						orderMsg = (orderMsgLength + 3) + orderMsg;
					dos.writeUTF(orderMsg);
					System.out.println("order sent: " + oie.toString());
					System.out.println("order sent: " + orderMsg);
				}
			}
		} catch (IOException e) {
			System.out.println("socket error... output socket...");
			e.printStackTrace();
		}
	}

	private synchronized int getOrderNo() {
		return Launcher.ORDER_NO++;
	}
}
