package trader_01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import sub.OrderInfoEty;
import sub.TradeInfoEty;
import sub.Util;

public class Launcher {

	public static HashMap<String, Queue<TradeInfoEty>> TRADE_INFO_Q_HASH;
	public static Queue<OrderInfoEty> ORDER_INFO_Q = new LinkedList<OrderInfoEty>();
	public static int ORDER_NO = 100000;
	public static final int NUM_OF_BIG_SAMPLE = 15;
	public static final int NUM_OF_SMALL_SAMPLE = 5;

	public static final String TARGET_IP = "127.0.0.1";
	public static final int TARGET_PORT = 34567;
	public static void main(String[] args) {
		setupProgram();
		runProgram();
	}

	private static void setupProgram() {
		Setup setup = new Setup();
		String[] stockNameList = Util.readStockNameList("src/resources/stock_names.txt");
		TRADE_INFO_Q_HASH = setup.generateTradeInfoQueueHash(stockNameList);
	}

	private static void runProgram() {
		List<Thread> threadList = new ArrayList<Thread>();
		threadList.add(new Thread(new TradeInfoReceiver(20001, "s_1"), "TradeInfoReceiver_1"));
		// threadList.add(new Thread(new TradeInfoReceiver(20002, "s_2")));
		// threadList.add(new Thread(new TradeInfoReceiver(20003, "s_3")));
		// threadList.add(new Thread(new TradeInfoReceiver(20004, "s_4")));
		// threadList.add(new Thread(new TradeInfoReceiver(20005, "s_5")));

		threadList.add(new Thread(new OrderIssuer(), "OrderIssuer_1"));

		for (Thread t : threadList)
			t.start();

		try {
			for (Thread t : threadList)
				t.join();
		} catch (InterruptedException e) {
			System.out.println("Thread join Interrupted!!!");
			e.printStackTrace();
		}

	}

}
