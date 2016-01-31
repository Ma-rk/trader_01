package sub;

import java.net.DatagramPacket;

public class TradeInfoEty {
	int tradeId;
	String stockName;
	double currentPrice;
	int tradeQty;

	public TradeInfoEty(int tradeId, String stockName, double currentPrice, int tradeQty) {
		this.tradeId = tradeId;
		this.stockName = stockName;
		this.currentPrice = currentPrice;
		this.tradeQty = tradeQty;
	}
	
	public TradeInfoEty(DatagramPacket inPacket) {
		this.tradeId=Util.getPriceId(inPacket);
		this.stockName = Util.getStockName(inPacket);
		this.currentPrice = Util.getTradePrice(inPacket);
		this.tradeQty = Util.getTradeQty(inPacket);
	}

	public int getTradeId() {
		return tradeId;
	}

	public String getStockName() {
		return stockName;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public int getTradeQty() {
		return tradeQty;
	}

	@Override
	public String toString() {
		return "TradeInfoEty [tradeId=" + tradeId + ", stockName=" + stockName + ", currentPrice=" + currentPrice
				+ ", tradeQty=" + tradeQty + "]";
	}
}
