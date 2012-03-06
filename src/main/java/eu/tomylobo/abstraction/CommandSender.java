package eu.tomylobo.abstraction;

public interface CommandSender {
	void sendMessage(String message);
	String getName();
	boolean hasPermission(String permission);
}
