package sub;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;

public class Util {
	
	public static int getPriceId(DatagramPacket inPacket) {
		int i = 0;
		byte[] data = inPacket.getData();

		for (i = 0; i < data.length; i++)
			if (data[i] == 44)
				break;

		return Integer.parseInt(new String(data, 0, i));
	}

	public static String getStockName(DatagramPacket inPacket) {
		int startAt = 0;
		int i = 0;
		int commaCount = 0;
		byte[] data = inPacket.getData();

		for (i = 0; i < data.length; i++) {
			if (data[i] == 44) {
				commaCount++;
				if (commaCount == 9)
					startAt = i;
				if (commaCount == 10) {
					break;
				}
			}
		}
		return new String(data, startAt + 1, i - startAt - 1);
	}

	public static double getTradePrice(DatagramPacket inPacket) {
		int startAt = 0;
		int i = 0;
		int commaCount = 0;
		byte[] data = inPacket.getData();

		for (i = 0; i < data.length; i++) {
			if (data[i] == 44) {
				commaCount++;
				if (commaCount == 12)
					startAt = i;
				if (commaCount == 13) {
					break;
				}
			}
		}
		return Double.parseDouble(new String(data, startAt + 1, i - startAt - 1));
	}

	public static int getTradeQty(DatagramPacket inPacket) {
		int startAt = 0;
		int i = 0;
		int commaCount = 0;
		byte[] data = inPacket.getData();

		for (i = 0; i < data.length; i++) {
			if (data[i] == 44) {
				commaCount++;
				if (commaCount == 13)
					startAt = i;
				if (commaCount == 14) {
					break;
				}
			}
		}
		return Integer.parseInt(new String(data, startAt + 1, i - startAt - 1));
	}

	public static String[] readStockNameList(String filePath) {
		String[] result = null;
		String thisLine = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(filePath));

			while ((thisLine = br.readLine()) != null) {
				System.out.println(thisLine);
				sb.append(thisLine);
				sb.append(",");
			}
			result = sb.toString().split(",");

		} catch (FileNotFoundException e) {
			System.out.println("FILE READ ERROR!!! File [" + filePath + "] does not exist!!!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("FILE READ ERROR!!! Error occured when read [" + filePath + "]");
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
