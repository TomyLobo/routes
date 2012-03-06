package eu.tomylobo.routes.trace;

import eu.tomylobo.math.Vector;

public class SignTraceResult extends TraceResult {
	/**
	 * The line index on the sign.
	 */
	public final int index;

	public SignTraceResult(TraceResult traceResult, int index) {
		this(traceResult.t, traceResult.position, traceResult.relativePosition, index);
	}

	public SignTraceResult(double t, Vector position, Vector relativePosition, int index) {
		super(t, position, relativePosition);

		this.index = index;
	}
}
