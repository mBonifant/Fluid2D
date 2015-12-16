package Fluids;

public interface Liquid {
	public float getViscosity();

	public float getDensity();

	public float getOmega();

	public int getTemperature();

	public void setTemperature(int t);
}
