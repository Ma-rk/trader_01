package trader_01;

import sub.OrderInfoEty;
import sub.TradeInfoEty;

public class PriceInfoHandler implements Runnable {
	private String stockName;
	private int stockQty;
	private int idxPointer = 0;
	private double tradePrice[] = new double[Launcher.NUM_OF_BIG_SAMPLE];
	private int tradeQty[] = new int[Launcher.NUM_OF_BIG_SAMPLE];
	private double multiplied[] = new double[Launcher.NUM_OF_BIG_SAMPLE];

	public PriceInfoHandler(String stockName) {
		this.stockName = stockName;
	}

	@Override
	public void run() {
		while (true) {
			TradeInfoEty tiEty = Launcher.TRADE_INFO_Q_HASH.get(stockName).poll();
			if (tiEty != null) {
				OrderInfoEty oie = null;
				if (tiEty.getCurrentPrice() < 1.0d || tiEty.getCurrentPrice() > 2.0d) {
					// 최근 거래가격이 1.0 미만이거나 2.0 초과이면 다음을 수행하여 보유수량을 0으로 맞춘다
					// (이 때 거래 내역 수집은 하지 않는다)
					// - 현재 보유수량이 양수이면 매도가격 1에 전량을 매도
					// - 현재 보유수량이 음수이면 매수가격 1에 (현재수량 * -1)만큼을 매수
					int tradeQty = 0;
					if (stockQty > 0)
						tradeQty = stockQty;
					else if (stockQty < 0)
						tradeQty = stockQty * -1;

					oie = createSellInfo("NEW", stockName, tradeQty, 1, tiEty.getTradeId());
					Launcher.ORDER_INFO_Q.add(oie);
					System.out.println("put data to ORDER_INFO_Q!! at if");
					stockQty = 0;
				} else {
					// 가격이 1.0 이상 2.0 이하의 범위이면 거래 내역을 수집하고 다음을 수행한다
					// (거래 내역은 최근 15개만 수집하고 15개 초과 내역은 버린다)
					// 최근 1~5번째 거래내역과 1~15번째 거래내역의 가중평균을 계산한다
					// 1~5번의 가중평균이 1~15번의 가중평균 초과시 매수가격 1에 1개를 BUY
					// 1~5번의 가중평균이 1~15번의 가중평균 이하시 매도가격 1에 1개를 SELL
					collectTradeInfoHistory(tiEty);

					if (getWeightedAvg5() > getWeightedAvg15()) {
						// 1~5번의 가중평균이 1~15번의 가중평균 초과시 매수가격 1에 1개를 BUY
						oie = createBuyInfo("NEW", stockName, 1, 1, tiEty.getTradeId());
						Launcher.ORDER_INFO_Q.add(oie);
						System.out.println("put data to ORDER_INFO_Q!! at if if");
						stockQty++;
					} else {
						// 1~5번의 가중평균이 1~15번의 가중평균 이하시 매도가격 1에 1개를 SELL
						oie = createSellInfo("NEW", stockName, 1, 1, tiEty.getTradeId());
						Launcher.ORDER_INFO_Q.add(oie);
						System.out.println("put data to ORDER_INFO_Q!! at if else");
						stockQty--;
					}
				}
			}
		}
	}

	// 가장 오래된 거래내역을 버리고 최근 거래내역을 수집한다
	private void collectTradeInfoHistory(TradeInfoEty tiEty) {
		tradePrice[idxPointer] = tiEty.getCurrentPrice();
		tradeQty[idxPointer] = tiEty.getTradeQty();
		multiplied[idxPointer] = tradePrice[idxPointer] * tradeQty[idxPointer];

		if (idxPointer == Launcher.NUM_OF_BIG_SAMPLE - 1)
			idxPointer = 0;
		else
			idxPointer++;
	}

	private double getWeightedAvg5() {
		// 최근 1~5개 거래내역의 가중평균을 계산한다
		double weightSum = 0.0d;
		int tradeQtySum = 0;
		int tempIdx = idxPointer;
		for (int i = 0; i < Launcher.NUM_OF_SMALL_SAMPLE; i++) {
			if (tempIdx == Launcher.NUM_OF_BIG_SAMPLE) {
				tempIdx = 0;
			}
			weightSum += multiplied[tempIdx];
			tradeQtySum += tradeQty[tempIdx];
			tempIdx++;
		}
		return weightSum / tradeQtySum;

	}

	private double getWeightedAvg15() {
		// 최근 1~15개 거래내역의 가중평균을 계산한다
		double weightSum = 0.0d;
		int tradeQtySum = 0;
		for (double d : multiplied)
			weightSum += d;
		for (double d : tradeQty)
			tradeQtySum += d;
		return weightSum / tradeQtySum;
	}

	private OrderInfoEty createBuyInfo(String action, String code, int qty, double price, int feedId) {
		OrderInfoEty buyInfo = createOrderInfo(action, code, "BUY", qty, price, feedId);
		return buyInfo;
	}

	private OrderInfoEty createSellInfo(String action, String code, int qty, double price, int feedId) {
		OrderInfoEty sellInfo = createOrderInfo(action, code, "SELL", qty, price, feedId);
		return sellInfo;
	}

	private OrderInfoEty createOrderInfo(String action, String code, String buySell, int qty, double price,
			int feedId) {
		// Length 051 tcp 二쇰Ц �뙣�궥�쓽 珥� 湲몄씠 (Length �빆紐� �룷�븿)
		// OrderNumber 0000123456 "二쇰Ц踰덊샇, 100,000�뿉�꽌 �떆�옉�븯�뿬 臾댁“嫄� 1�떇
		// 利앷��떆耳쒖빞 �븯硫�, 以묐났��
		// �뿀�슜�릺吏� �븡怨�, �닚�꽌媛� ��由щ㈃ �봽濡쒓렇�옩�� 醫낅즺�맂�떎"
		// Action NEW �떊洹� 二쇰Ц�� NEW
		// Code KR4201K82650 二쇰Ц�븯�젮�뒗 醫낅ぉ�쓽 12�옄由� 醫낅ぉ 肄붾뱶
		// BuySell BUY 留ㅼ닔 二쇰Ц�� BUY, 留ㅻ룄 二쇰Ц�� SELL �엯�젰
		// Qty 10 二쇰Ц �닔�웾 �엯�젰
		// Price 0.17 二쇰Ц 媛�寃� �엯�젰
		// FeedId 47783451 �쁽�옱 二쇰Ц�쓣 諛쒖깮�떆�궓 �떆�꽭 id 媛믪쓣 �엯�젰�븳�떎( int value
		// �엫)
		OrderInfoEty orderInfo = new OrderInfoEty(action, code, buySell, qty, price, feedId);
		return orderInfo;
	}
}
