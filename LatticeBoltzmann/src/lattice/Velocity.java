package lattice;

/**
 * Basis Vector Velocities
 * 
 * @author bonifantmc
 *
 */
public class Velocity {

	/** four nineths */
	public final static double frac4in9 = 4.0 / 9.0;
	/** one nineth */
	public final static double frac1in9 = 1.0 / 9.0;
	/** one thirty-sixth */
	public final static double frac1in36 = 1.0 / 36.0;

	// flat world
	/** Center */
	public final static int[] C = { 0, 0, 0 };
	/** North */
	public final static int[] N = { 1, 0, 0 };
	/** South */
	public final static int[] S = { -1, 0, 0 };
	/** East */
	public final static int[] E = { 0, 1, 0 };
	/** West */
	public final static int[] W = { 0, -1, 0 };
	/** North East */
	public final static int[] NE = { 1, 1, 0 };
	/** South West */
	public final static int[] SW = { -1, -1, 0 };
	/** North West */
	public final static int[] NW = { 1, -1, 0 };
	/** South East */
	public final static int[] SE = { -1, 1, 0 };
	// third dimension add the ups and downs
	/** Up */
	public final static int[] U = { 0, 0, 1 };
	/** Down */
	public final static int[] D = { 0, 0, -1 };
	// ups
	/** North Up */
	public final static int[] NU = { 1, 0, 1 };
	/** South Up */
	public final static int[] SU = { -1, 0, 1 }; // south up e13
	/** East Up */
	public final static int[] EU = { 0, 1, 1 }; // east up e14
	/** West Up */
	public final static int[] WU = { 0, -1, 1 }; // west up e15
	/** North East Up */
	public final static int[] NEU = { 1, 1, 1 }; // north east up e16
	/** South West Up */
	public final static int[] SWU = { -1, -1, 1 }; // south west up e17
	/** North West Up */
	public final static int[] NWU = { 1, -1, 1 }; // north west up e18
	/** South East Up */
	public final static int[] SEU = { -1, 1, 1 }; // south east up e19
	// downs
	/** north Down */
	public final static int[] ND = { 1, 0, -1 }; // north down e20
	/** South Down */
	public final static int[] SD = { -1, 0, -1 }; // south down e21
	/** East Down */
	public final static int[] ED = { 0, 1, -1 };// east down e22
	/** West Down */
	public final static int[] WD = { 0, -1, -1 }; // west down e23
	/** North East Down */
	public final static int[] NED = { 1, 1, -1 }; // north east down e24
	/** South West Down */
	public final static int[] SWD = { -1, -1, -1 }; // south west down e25
	/** North West Down */
	public final static int[] NWD = { 1, -1, -1 }; // north west down e26
	/** South East Down */
	public final static int[] SED = { -1, 1, -1 };

	/** D2Q9 */
	// 0, 2, 1, 4, 3, 6, 5, 8, 7
	// 0, 1, 2, 3, 4, 5, 6, 7, 8
	public final static int[][] e9 = { C, N, S, E, W, NW, SE, NE, SW };
	/** D3Q15 */
	public final static int[][] e15 = { C, N, S, E, W, U, D, NEU, SWD, NWU,
			SED, SEU, NWD, SWU, NED };
	/** D3Q19 */
	public final static int[][] e19 = { C, N, S, E, W, U, D, NW, SE, NE, SW,
			NU, SD, SU, ND, EU, WD, WU, ED };
	/** D3Q27 */
	public final static int[][] e27 = { C, N, S, E, W, U, D, NW, SE, NE, SW,
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

	public final static double[] w9 = { frac4in9, frac1in9, frac1in9, frac1in9,
			frac1in9, frac1in36, frac1in36, frac1in36, frac1in36 };
	public final static double[] w15 = { 2f / 9f, 1f / 9f, 1f / 9f, 1f / 9f,
			1f / 9f, 1f / 9f, 1f / 9f, 1f / 72f, 1f / 72f, 1f / 72f, 1f / 72f,
			1f / 72f, 1f / 72f, 1f / 72f, 1f / 72f };
	public final static double[] w19 = { 1f / 3f, 1f / 18f, 1f / 18f, 1f / 18f,
			1f / 18f, 1f / 18f, 1f / 18f, 1f / 36f, 1f / 36f, 1f / 36f,
			1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f,
			1f / 36f, 1f / 36f, 1f / 36f };
	public final static double[] w27 = { 8f / 27f, 2f / 27f, 2f / 27f,
			2f / 27f, 2f / 27f, 2f / 27f, 2f / 27f, 1f / 216f, 1f / 216f,
			1f / 216f, 1f / 216f, 1f / 216f, 1f / 216f, 1f / 216f, 1f / 216f,
			1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f,
			1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f };

}
