package ex2.robot;

import java.util.ArrayList;

import ex2.DebugNotes;
import lejos.nxt.SensorPortListener;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

public class RangeProvider  extends Thread {
	private UltrasonicSensor ultrasonicSensor;
	private ArrayList<SensorPortListener> listeners;

	private volatile int lastDistance = 0;	
	private volatile int currentDistance = 0;
	
	private volatile boolean running = true;
	
	public RangeProvider(UltrasonicSensor ultrasonicSensor)  {
		super();
		this.ultrasonicSensor = ultrasonicSensor;
		
		this.listeners = new ArrayList<SensorPortListener>();
	}
	
	public void addListener(SensorPortListener listener) {
		this.listeners.add(listener);
	}
	
	
	
	public void run() {
		this.ultrasonicSensor.continuous();
		
		running=true;
		lastDistance = ultrasonicSensor.getDistance();
		while (running) {
			int dist = ultrasonicSensor.getDistance();		
			currentDistance = dist;
			for (SensorPortListener listener : this.listeners) {
				listener.stateChanged(null, lastDistance, currentDistance);
			}	
		
			lastDistance=currentDistance;
		
		}	
		this.ultrasonicSensor.reset();
		this.ultrasonicSensor.off();
	}

	
	
	public synchronized void stop() {
		running=false;
		
	}
	public int getDistance() {
		return ultrasonicSensor.getDistance();
	}
	
	
	
	
}
