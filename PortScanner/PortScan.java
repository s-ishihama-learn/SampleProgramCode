import java.net.InetSocketAddress;
import java.net.Socket;

public class PortScan {
	public static void main(String[] args) {
		String hostName = "localhost";
		int port = 80;

		if (args.length > 1) {
			hostName = args[0];
			port = Integer.parseInt(args[1]);
		}

		try {
			Socket sock = new Socket();
			InetSocketAddress address = new InetSocketAddress(hostName, port);
			sock.connect(address, 2000);
			System.out.println("[" + port + "] --> OK\n");
			sock.close();
		} catch (Exception e) {
			System.out.println("[" + port + "] --> NG\n");
		}
	}
}
