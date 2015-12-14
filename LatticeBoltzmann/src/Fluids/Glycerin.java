package Fluids;

/**
 * Defines the properties of water
 * 
 * @author bonifantmc //TODO add in more values of viscosity at varying
 *         temperatures
 */
public class Glycerin implements Liquid {
	/** A Map of the kinematic viscosity of waters temperature increases */
	
	public static float[] viscosityFromTemp = { -1,//0C
			-1,//10C
			
			0.0011186f,//20C....
			0.00047553f,//30C
			0.00022580f,//40C
			0.00011771f,//50C
			0.00066431f,//60C
			0.000040136f,//70C
			0.000025721f,//80C
			0.000017351f,//90C
			0.000012242f,//100C
			0.0000089861f,//110C
			0.0000068313f,//120C
			0.0000053575f,//130C
			0.0000043203f,//140C
			0.0000035721f,//150C
			0.0000030206f,//160C
			0.0000026067f,//170C
			0.0000022914f,//180C
			0.0000020483f,//190C
			0.0000018592f,//200C
			0.0000017114f,//210C
			0.0000015957f,//220C
			0.0000015054f,//230C
			0.0000014358f,//240C
			0.0000013833f,//250C
			0.0000013451f,//260C
			0.0000013194f//270C
	};


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
	public Glycerin(int t) {
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
		this.density = 1.26f;
		this.viscosity = (float) (viscosityFromTemp[t / 10]*Math.pow(10, 6));
		this.omega = 1f / (3f * this.viscosity + 0.5f);
		System.out.println(this.viscosity);

	}

}
