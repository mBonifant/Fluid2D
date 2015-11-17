package lattice;

/**
 * Lattice Boltzmann can be implemented with each cell having either 9, 15, 19,
 * or 27 microscopic velocities. This class implements inherent properties about
 * those velocities, mainly their size, the directions, their weights, and
 * opposite direction mappings
 * 
 * @author bonifantmc
 *
 */

//TODO double check the weight vectors which may be wrong
public enum Q {
	/***/
	nine(9, // size
			new double[] { 4f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f }, // weights
			Velocity.o9, // opposites
			Velocity.e9), // velocities
			/***/
	fifteen(15, // size
			new double[] { 2f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 72f, 1f / 72f, 1f / 72f,
					1f / 72f, 1f / 72f, 1f / 72f, 1f / 72f, 1f / 72f }, // weights
			Velocity.o15, // opposites
			Velocity.e15), // velocities
			/***/
	nineteen(19, // size
			new double[] { 1f / 3f, 1f / 18f, 1f / 18f, 1f / 18f, 1f / 18f, 1f / 18f, 1f / 18f, 1f / 36f, 1f / 36f,
					1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f, 1f / 36f,
					1f / 36f }, // weights
			Velocity.o19, // opposites
			Velocity.e19), // velocities
			/***/
	twentySeven(27, // size
			new double[] { 8f / 27f, 2f / 27f, 2f / 27f, 2f / 27f, 2f / 27f, 2f / 27f, 2f / 27f, 1f / 216f, 1f / 216f,
					1f / 216f, 1f / 216f, 1f / 216f, 1f / 216f, 1f / 216f, 1f / 216f, 1f / 54f, 1f / 54f, 1f / 54f,
					1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f, 1f / 54f }, // weights
			Velocity.o27, // opposites
			Velocity.e27);// velocities

	/** weighting of the velocity per direction */
	public final double[] weights;
	/** element i gives the index of the opposite velocity of i */
	public final int[] opposites;
	/** list of 3-vectors, giving the direction of each velocity */
	public final byte[][] velocities;
	/** the number of velocities the given Q flows in */
	public final int size;

	/**
	 * 
	 * @param i
	 *            size of the cell
	 * @param w
	 *            weights of each velocity
	 * @param o
	 *            index of opposite direction velocity
	 * @param v
	 *            direction of velocity
	 */
	private Q(int i, double[] w, int[] o, byte[][] v) {
		this.weights = w;
		this.opposites = o;
		this.velocities = v;
		this.size = i;
	}

	/**
	 * 
	 * @param i
	 *            9, 15, 19, or 27
	 * @return nine, fifteen, nineteen, or twentySeven, the possible Q's of the
	 *         lattice
	 */
	public static Q getQ(int i) {
		switch (i) {
		case 9:
			return nine;
		case 15:
			return fifteen;
		case 19:
			return nineteen;
		case 27:
			return twentySeven;
		default:
			System.err.print("Error, Q can only be 9,15,19, or 27");
			System.exit(-1);
		}
		return null;
	}
}
