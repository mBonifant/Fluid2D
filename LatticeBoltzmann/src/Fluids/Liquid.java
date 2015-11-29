package Fluids;

public interface Liquid {
	public double getViscosity();

	public double getDensity();

	public double getOmega();

	public int getTemperature();

	public void setTemperature(int t);
}
