package eu.tomylobo.abstraction.block;

public class BlockState {
	private final int type;
	private final int data;

	public BlockState(int type, int data) {
		this.type = type;
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public int getData() {
		return data;
	}
}
