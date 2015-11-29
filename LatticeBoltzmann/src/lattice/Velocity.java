package lattice;

/**
 * Basis Vector Velocities
 * 
 * @author bonifantmc
 *
 */
public class Velocity {

	/**
	 * 
	 */
	// Standard bases vectors ei, i=1-27.

	// flat world
	/** Center */
	public final static double[] C = { 0, 0, 0 };
	/** North */
	public final static double[] N = { 1, 0, 0 };
	/** South */
	public final static double[] S = { -1, 0, 0 };
	/** East */
	public final static double[] E = { 0, 1, 0 };
	/** West */
	public final static double[] W = { 0, -1, 0 };
	/** North East */
	public final static double[] NE = { 1, 1, 0 };
	/** South West */
	public final static double[] SW = { -1, -1, 0 };
	/** North West */
	public final static double[] NW = { 1, -1, 0 };
	/** South East */
	public final static double[] SE = { -1, 1, 0 };
	// third dimension add the ups and downs
	/** Up */
	public final static double[] U = { 0, 0, 1 };
	/** Down */
	public final static double[] D = { 0, 0, -1 };
	// ups
	/** North Up */
	public final static double[] NU = { 1, 0, 1 };
	/** South Up */
	public final static double[] SU = { -1, 0, 1 }; // south up e13
	/** East Up */
	public final static double[] EU = { 0, 1, 1 }; // east up e14
	/** West Up */
	public final static double[] WU = { 0, -1, 1 }; // west up e15
	/** North East Up */
	public final static double[] NEU = { 1, 1, 1 }; // north east up e16
	/** South West Up */
	public final static double[] SWU = { -1, -1, 1 }; // south west up e17
	/** North West Up */
	public final static double[] NWU = { 1, -1, 1 }; // north west up e18
	/** South East Up */
	public final static double[] SEU = { -1, 1, 1 }; // south east up e19
	// downs
	/** north Down */
	public final static double[] ND = { 1, 0, -1 }; // north down e20
	/** South Down */
	public final static double[] SD = { -1, 0, -1 }; // south down e21
	/** East Down */
	public final static double[] ED = { 0, 1, -1 };// east down e22
	/** West Down */
	public final static double[] WD = { 0, -1, -1 }; // west down e23
	/** North East Down */
	public final static double[] NED = { 1, 1, -1 }; // north east down e24
	/** South West Down */
	public final static double[] SWD = { -1, -1, -1 }; // south west down e25
	/** North West Down */
	public final static double[] NWD = { 1, -1, -1 }; // north west down e26
	/** South East Down */
	public final static double[] SED = { -1, 1, -1 };

	/** D2Q9 */                        //0, 2, 1, 4, 3, 6,  5,  8,  7 
									   //0,  1, 2, 3, 4, 5,  6,  7,  8
	public final static double[][] e9 = { C, N, S, E, W, NW, SE, NE, SW };
	/** D3Q15 */
	public final static double[][] e15 = { C, N, S, E, W, U, D, NEU, SWD, NWU,
			SED, SEU, NWD, SWU, NED };
	/** D3Q19 */
	public final static double[][] e19 = { C, N, S, E, W, U, D, NW, SE, NE, SW,
			NU, SD, SU, ND, EU, WD, WU, ED };
	/** D3Q27 */
	public final static double[][] e27 = { C, N, S, E, W, U, D, NW, SE, NE, SW,
			NU, SD, SU, ND, EU, WD, WU, ED, NEU, SWD, NWU, SED, SEU, NWD, SWU,
			NED };

	// an array pointing to where each base vector's opposite is
	/** D2Q9 opposites */
	public final static int[] o9 = { 0, 2, 1, 4, 3, 6, 5, 8, 7 };
	/** D3Q15 opposites */
	public final static int[] o15 = { 0, 2, 1, 4, 3, 6, 5, 8, 7, 10, 9, 12, 11,
			14, 13 };
	/** D3Q19 opposites */
	public final static int[] o19 = { 0, 2, 1, 4, 3, 6, 5, 8, 7, 10, 9, 12, 11,
			14, 13, 16, 15, 18, 17 };
	/** D3Q27 opposites */
	public final static int[] o27 = { 0, 2, 1, 4, 3, 6, 5, 8, 7, 10, 9, 12, 11,
			14, 13, 16, 15, 18, 17, 20, 19, 22, 21, 24, 23, 26, 25 };

	public final static double[] w9 = { 4f / 9f, 
		1f / 9f, 1f / 9f, 1f / 9f,	1f / 9f,
		1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f };
	public final static double[] w15 = { 2f / 9f, 1f / 9f, 1f / 9f, 1f / 9f,
			1f / 9f, 1f / 9f, 1f / 9f, 1f / 72f, 1f / 72f, 1f / 72f, 1f / 72f,
			1f / 72f, 1f / 72f, 1f / 72f, 1f / 72f };
	public final static double[] w19 = { 1f / 3f, 1f / 18f, 1f / 18f, 1f / 18f,
			1f / 18f, 1f / 18f, 1f / 18f, 1f / 36f, 1f / 36f, 1f / 36f,
			1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f,
			1f / 36f, 1f / 36f, 1f / 36f };
	public final static double[] w27 = { 8f / 27f, 2f / 27f, 2f / 27f, 2f / 27f,
			2f / 27f, 2f / 27f, 2f / 27f, 1f / 216f, 1f / 216f, 1f / 216f,
			1f / 216f, 1f / 216f, 1f / 216f, 1f / 216f, 1f / 216f, 1f / 54f,
			1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f,
			1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f };

}
