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
		try {
			socket = new Socket(Launcher.TARGET_IP, Launcher.TARGET_PORT);
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			while (true) {
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
					System.out.println("order sent: " + oie.getCode());
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
