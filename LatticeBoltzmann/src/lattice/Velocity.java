package lattice;

public class Velocity {

	public static double dot(double[] vi, double[] vj) {
		return vi[0] * vj[0] + vi[1] * vj[1] + vi[2] * vj[2];
	}

	public static double dot(double[] vi, byte[] vj) {
		return vi[0] * vj[0] + vi[1] * vj[1] + vi[2] * vj[2];
	}

	public static void multE(double[] u, double[] v, double f) {
		u[0] = v[0] * f;
		u[1] = v[1] * f;
		u[2] = v[2] * f;
	}

	public static void multE(double[] u, byte[] v, double f) {
		u[0] = v[0] * f;
		u[1] = v[1] * f;
		u[2] = v[2] * f;
	}

	public static void add(double[] vi, double[] vj) {
		vi[0] += vj[0];
		vi[1] += vj[1];
		vi[2] += vj[2];
	}

	public static double speedSq(double[] v) {
		return v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
	}

	// Standard bases vectors ei, i=1-27.
	public final static byte[]
	// Standard base vectors in the flat world
	C = { 0, 0, 0 }, // center e1
			N = { 1, 0, 0 }, // north e2
			S = { -1, 0, 0 }, // south e3
			E = { 0, 1, 0 }, // east e4
			W = { 0, -1, 0 }, // west e5
			NE = { 1, 1, 0 }, // north east e6
			SW = { -1, -1, 0 }, // south west e7
			NW = { 1, -1, 0 }, // north west e8
			SE = { -1, 1, 0 }, // south east e9
			// third dimension add the ups and downs
			U = { 0, 0, 1 }, // up e10
			D = { 0, 0, -1 }, // down e11
			// ups
			NU = { 1, 0, 1 }, // north up e12
			SU = { -1, 0, 1 }, // south up e13
			EU = { 0, 1, 1 }, // east up e14
			WU = { 0, -1, 1 }, // west up e15
			NEU = { 1, 1, 1 }, // north east up e16
			SWU = { -1, -1, 1 }, // south west up e17
			NWU = { 1, -1, 1 }, // north west up e18
			SEU = { -1, 1, 1 }, // south east up e19
			// downs
			ND = { 1, 0, -1 }, // north down e20
			SD = { -1, 0, -1 }, // south down e21
			ED = { 0, 1, -1 }, // east down e22
			WD = { 0, -1, -1 }, // west down e23
			NED = { 1, 1, -1 }, // north east down e24
			SWD = { -1, -1, -1 }, // south west down e25
			NWD = { 1, -1, -1 }, // north west down e26
			SED = { -1, 1, -1 };// south east down e27

	// the base vectors used for D2Q9, D3Q15, D3Q19, and D3Q27;
	public final static byte[][] e9 = { C, N, S, E, W, NW, SE, NE, SW };
	public final static byte[][] e15 = { C, N, S, E, W, U, D, NEU, SWD, NWU, SED, SEU, NWD, SWU, NED };
	public final static byte[][] e19 = { C, N, S, E, W, U, D, NW, SE, NE, SW, NU, SD, SU, ND, EU, WD, WU, ED };
	public final static byte[][] e27 = { C, N, S, E, W, U, D, NW, SE, NE, SW, NU, SD, SU, ND, EU, WD, WU, ED, NEU, SWD,
			NWU, SED, SEU, NWD, SWU, NED };
	// an array pointing to where each base vector's opposite is
	public final static int[] o9 = { 0, 2, 1, 4, 3, 6, 5, 8, 7 };
	public final static int[] o15 = { 0, 2, 1, 4, 3, 6, 5, 8, 7, 10, 9, 12, 11, 14, 13 };
	public final static int[] o19 = { 0, 2, 1, 4, 3, 6, 5, 8, 7, 10, 9, 12, 11, 14, 13, 16, 15, 18, 17 };
	public final static int[] o27 = { 0, 2, 1, 4, 3, 6, 5, 8, 7, 10, 9, 12, 11, 14, 13, 16, 15, 18, 17, 20, 19, 22, 21,
			24, 23, 26, 25 };

}
