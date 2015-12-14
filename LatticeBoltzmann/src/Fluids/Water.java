package Fluids;

/**
 * Defines the properties of water
 * 
 * @author bonifantmc //TODO add in more values of viscosity at varying
 *         temperatures
 */
public class Water implements Liquid {
	/** A Map of the kinematic viscosity of waters temperature increases */
	public static float[] viscosityFromTemp = { 1.7918065f, 1.3062651526112f,1.0034053420487f,
			0.8008271785568f, 0.65809618509849f, 0.553468039063689f,
			0.47436738184881f, 0.41307954234706f, 0.36463671901831f, 0.32570900878262f,0.29688726487272f};


	/** water temperature */
	private int temperature;
	/** water density */
	private float density;
	/** water viscosity (how well it flows) */
	private float viscosity;
	/** time constant related to viscosity by omega= 1/(3*viscoity+0.5) */
	private float omega;

	@Override
	public float getViscosity() {
		return this.viscosity;
	}

	@Override
	public float getDensity() {
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
	public float getOmega() {
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
