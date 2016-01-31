package sub;

public class OrderInfoEty {
	int length;
	int orderNumber;
	String action;
	String code;
	String buySell;
	int qty;
	double price;
	int feedId;

	public OrderInfoEty(String action, String code, String buySell, int qty, double price, int feedId) {
		this.action = action;
		this.code = code;
		this.buySell = buySell;
		this.qty = qty;
		this.price = price;
		this.feedId = feedId;
	}

	public int getLength() {
		return length;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public String getAction() {
		return action;
	}

	public String getCode() {
		return code;
	}

	public String getBuySell() {
		return buySell;
	}

	public int getQty() {
		return qty;
	}

	public double getPrice() {
		return price;
	}

	public int getFeedId() {
		return feedId;
	}
}
