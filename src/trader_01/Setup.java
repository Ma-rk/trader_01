package trader_01;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import sub.TradeInfoEty;

public class Setup {
	public HashMap<String, Queue<TradeInfoEty>> generateTradeInfoQueueHash(String[] stockNameList) {
		HashMap<String, Queue<TradeInfoEty>> h = new HashMap<String, Queue<TradeInfoEty>>(1100);
		for (String stockName : stockNameList) {
			if (h.get(stockName) == null)
				h.put(stockName, new LinkedList<TradeInfoEty>());
		}
		return h;
	}
}
