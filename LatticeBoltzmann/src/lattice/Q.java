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

// TODO double check the weight vectors which may be wrong
public enum Q {
	/***/
	nine(9, Velocity.w9, Velocity.o9, Velocity.e9),
	/***/
	fifteen(15, Velocity.w15, Velocity.o15, Velocity.e15),
	/***/
	nineteen(19, Velocity.w19, Velocity.o19, Velocity.e19),
	/***/
	twentySeven(27, Velocity.w27, Velocity.o27, Velocity.e27);

	/** weighting of the velocity per direction */
	public final double[] weights;
	/** element i gives the index of the opposite velocity of i */
	public final int[] opposites;
	/** list of 3-vectors, giving the direction of each velocity */
	public final double[][] velocities;
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
	private Q(int i, double[] w, int[] o, double[][] v) {
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
