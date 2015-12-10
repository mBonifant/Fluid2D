package Fluids;

/**
 * Defines the properties of water
 * 
 * @author bonifantmc //TODO add in more values of viscosity at varying
 *         temperatures
 */
public class Water implements Liquid {
	/** A Map of the kinematic viscosity of waters temperature increases */
	public static double[] viscosityFromTemp = { 1.787, 1.519, 1.307, 1.004,
			0.801, 0.658, 0.553, 0.475, 0.413, 0.365, 0.1 };
	/** A Map of the density of water as temperature increases */
	public static double[] densityFromTemp = {};

	/** water temperature */
	private int temperature;
	/** water density */
	private double density;
	/** water viscosity (how well it flows) */
	private double viscosity;
	/** time constant related to viscosity by omega= 1/(3*viscoity+0.5) */
	private double omega;

	@Override
	public double getViscosity() {
		return this.viscosity;
	}

	@Override
	public double getDensity() {
		return this.density;
	}

	/**
	 * Define the properties of water at a given temperature (pressue is assumed
	 * 1atm).
	 * 
	 * @param t
	 *            the temperature of the water
	 */
	public Water(int t) {
		this.setTemperature(t);
	}

	@Override
	public double getOmega() {
		return this.omega;
	}

	@Override
	public int getTemperature() {
		return this.temperature;
	}

	@Override
	public synchronized void setTemperature(int t) {
		this.temperature = t;
		this.density = 1;
		this.viscosity = viscosityFromTemp[t / 10];
		this.omega = 1f / (3f * this.viscosity + 0.5f);
		System.out.println(this.viscosity);

	}

}
