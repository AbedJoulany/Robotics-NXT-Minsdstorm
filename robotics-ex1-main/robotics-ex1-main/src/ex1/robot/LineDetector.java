package ex1.robot;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.util.Delay;
public class LineDetector {	
	LightSensor sensor;
	private double lineValue;
	private double requiredValue;
	private double emptyValue;

	
	
	public LineDetector(LightSensor sensor) {		
		lineValue = 318.0;
		emptyValue = 567.0;
		this.requiredValue =  Math.abs(emptyValue+lineValue)/2;
		this.sensor = sensor;
		this.setLightOff();				
	}
	
	public void clibrateRequiredValue() {	
		this.requiredValue =  Math.abs(emptyValue+lineValue)/2;
		/*
		this.setLightOn();
		
		//this.requiredValue = getAverageValue();	
		this.setLightOff();
		*/
	}
	
	public void clibrateLineValue() {	
		this.setLightOn();
		this.lineValue = getAverageValue();	
		this.setLightOff();
	}
	
	public void clibrateBackgroundValue() {
		this.setLightOn();
		this.emptyValue = getAverageValue();
		this.setLightOff();
	}
	
	public double getError() {
		//double reqPercentage = (this.requiredValue - this.lineValue)/ (this.emptyValue-this.lineValue);
		return 100 * (double)((this.sensor.getNormalizedLightValue() - this.requiredValue) / (this.emptyValue-this.requiredValue)) ;//- reqPercentage;		
	}
	
	public double getLineValue() {
		return lineValue;
	}
	
	public double getRequiredValue() {
		return requiredValue;
	}

	public double getEmptyValue() {
		return emptyValue;
	}

	private double getAverageValue() {
		double sampleCount = 40;
		double average = 0;	
				
		for (int i = 0;  i < sampleCount ; i++) {
			int val = this.sensor.getNormalizedLightValue();
			LCD.drawInt(val, 0, 5);
			average +=  val;			
			Delay.msDelay(50);
		}
		return average/sampleCount;
	}
	
	private double getMaxValue() {
		double sampleCount = 20;
		double max = 0;	
				
		for (int i = 0;  i < sampleCount ; i++) {
			int val = this.sensor.getNormalizedLightValue();
			LCD.drawInt(val, 0, 5);
			if (max< val) {
				max=val;
			}
			Delay.msDelay(50);
		}
		return max;
	}
	
	
	private double getMinValue() {
		double sampleCount = 20;
		double min = 1024;	
				
		for (int i = 0;  i < sampleCount ; i++) {
			int val = this.sensor.getNormalizedLightValue();
			LCD.drawInt(val, 0, 5);
			if (min> val) {
				min=val;
			}
			Delay.msDelay(50);
		}
		return min;
	}
	
	public void setLightOn() {
		if (!this.sensor.isFloodlightOn()) {
			this.sensor.setFloodlight(true);
		}	 
	}
	
	public void setLightOff() {
		if (this.sensor.isFloodlightOn()) {
			this.sensor.setFloodlight(false);
		}	 
	}
}
