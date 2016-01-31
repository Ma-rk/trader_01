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
	public static final int DEFAULT_NUM_OF_STOCK = 1100;
	public static final int NUM_OF_BIG_SAMPLE = 15;
	public static final int NUM_OF_SMALL_SAMPLE = 5;

	public static final String TARGET_IP = "127.0.0.1";
	public static final int TARGET_PORT = 34567;

	public static String[] STOCK_NAME_LIST = Util.readStockNameList("src/resources/stock_names_2.txt");;
	public static int[] TRADE_INFO_RECEIVER_PORT_NUMBER_LIST = { 20001, 20002, 20003, 20004, 20005 };
	public static String[] TRADE_INFO_RECEIVER_THREAD_NAME_LIST = { "TradeInfoReceiver_1", "TradeInfoReceiver_2",
			"TradeInfoReceiver_3", "TradeInfoReceiver_4", "TradeInfoReceiver_5" };

	public static void main(String[] args) {
		setupProgram();
		runProgram();
	}

	private static void setupProgram() {
		Setup setup = new Setup();
		TRADE_INFO_Q_HASH = setup.generateTradeInfoQueueHash(STOCK_NAME_LIST);
	}

	private static void runProgram() {
		List<Thread> threadList = new ArrayList<Thread>();
		for (int i = 0; i < TRADE_INFO_RECEIVER_PORT_NUMBER_LIST.length; i++) {
			threadList.add(new Thread(new TradeInfoReceiver(TRADE_INFO_RECEIVER_PORT_NUMBER_LIST[i],
					TRADE_INFO_RECEIVER_THREAD_NAME_LIST[i]), TRADE_INFO_RECEIVER_THREAD_NAME_LIST[i]));
		}

		for (String stockName : STOCK_NAME_LIST) {
			threadList.add(new Thread(new PriceInfoHandler(stockName), "th_" + stockName));
		}

		threadList.add(new Thread(new OrderIssuer(), "OrderIssuer"));

		for (Thread t : threadList) {

			t.start();
			System.out.println("thread [" + t.getName() + "] started.");
		}

		try {
			for (Thread t : threadList)
				t.join();
		} catch (InterruptedException e) {
			System.out.println("Thread join Interrupted!!!");
			e.printStackTrace();
		}
	}
}
