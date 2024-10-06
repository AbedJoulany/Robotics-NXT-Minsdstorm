package ex2.robot;
import lejos.geom.*;

import java.util.TimerTask;

import ex2.DebugNotes;
import ex2.NavigationMap;
import ex2.robot.IRobotControl.State;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.util.Delay;
import lejos.robotics.localization.MCLParticle;
import lejos.robotics.localization.MCLParticleSet ;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.navigation.NavigationListener;

import lejos.nxt.LCD;
import java.util.Timer;
import java.util.TimerTask;

import lejos.nxt.Sound;
import java.io.File;

public class NavigationController extends MappingController implements NavigationListener{
	
	NavigationMap map = new NavigationMap();
	MCLPoseProvider mcl;
	Navigator navigator;
	State returnToState;
	private Timer timer;
	
	@Override


	
	public void run(State startState) {
		map.init();
		map.displayMap();	
		
		//mcl = this.robot().getMclPoseProvider(map.getLineMap());
		//mcl.setDebug(true);
		this.setState(startState);
		this.robot().resetSpeed();
		
		DebugNotes.msg("exploring");
		explore();
		DebugNotes.msg("nvigating");
		this.robot().resetSpeed();
		this.setState(State.ON_SIDE);
		robot().driveForward();	
		
		/*
		DebugNotes.msg("navigating");
		navigator =this.robot().getNavigator(mcl);
		
		//TODO: change to mcl - since it is not working now
		//this.robot().getPoseProvider().setPose(new Pose(-60,49,180));
		
		//Pose i = mcl.getEstimatedPose();		
		//DebugNotes.msgWithWait("ip:"+ Math.round(i.getX()) + "," + Math.round(i.getY()));
		//DebugNotes.msgWithWait("iph:"+ Math.round(i.getHeading()));
		
		this.robot().getPoseProvider().setPose(mcl.getEstimatedPose());
		
		navigator =this.robot().getNavigator(this.robot().getPoseProvider());
		navigator.addNavigationListener(this);
			
		
		Waypoint w = this.map.getWayPoint(NavigationMap.FIRST_LINE);
		navigateTo(State.NAVIGATING_FLINE, w);
		*/
	}
	public void explore() {
		
		this.robot().goToWall();				
	}
	
	private void prepareToNavigate() {
		robot().stopMoving();
			
		Delay.msDelay(100);							
		float distToWAll = this.robot().getObstacleDistance();
		
		Pose initialPose = this.map.getInitialPose();
			
		initialPose.setLocation(initialPose.getX()+8, distToWAll+6);
		initialPose.setHeading(180);
		
		DebugNotes.msg("stopRangeProvider");
		this.robot().stopRangeProvider();
		
		 	
		LCD.clear();
		LCD.drawString("inp X "+ initialPose.getX(), 0, 2);
		LCD.drawString("inp Y "+ initialPose.getY(), 0, 3);
		LCD.drawString("inp H "+ initialPose.getHeading(), 0, 4);
		
		
		this.robot().getPoseProvider().setPose(initialPose);	
		this.setState(State.PENDING_NAVIGATION_START_COMMAND);
		 
		//Sound.playSample(new File("ready.wav"), 70);
		DebugNotes.msg("waiting for command...");
		
		timer= new Timer();
		TimerTask task = new TimerTask() {	           
	        public void run() {	
	        	Waypoint w = map.getTargeWaypoint();
				LCD.drawString("dst "+ w.getX() +"," +w.getY(), 0, 4);
	        	navigateTo(State.NAVIGATING_CENTER, w);
	        }
	    };
	    timer.schedule(task,30000);
	    
	    
	}
	public void explore_DEP() {
		mcl.getPose();		
		//DebugNotes.log("hge: " + this.hasGoodPoseEstimation());
		while (!this.hasGoodPoseEstimation(20)) {		/// TODO: change to 5	
			if (this.state == State.LOCATE_POSITION) {
				this.robot().randomStep();				
				while (mcl.isBusy()) {
					DebugNotes.log("mcl is busy!");
					Thread.yield();
				}
				DebugNotes.msg("calculating...");
				Pose p = mcl.getPose();				
				DebugNotes.msg("esPos:" +p );
				
			} else {
				return;
			}
		}
		this.robot().stopMoving();
		
	}
	
	public void navigateTo(State nState, Waypoint w) {
		//Pose p = mcl.getPose();	
		//DebugNotes.log("pos:("+p.getX() + ","+ p.getY()+") h:"+ p.getHeading());
		
		this.setState(nState);
		//this.fixHeading();
			
		DebugNotes.msg("Nav to "+ w.getX() + "," + w.getY());
		this.robot().resetSpeed();
		if (navigator==null) {
			navigator =this.robot().getNavigator(this.robot().getPoseProvider());
			navigator.addNavigationListener(this);
		}
		
		navigator.goTo(w);
		
		
	}
	
	private void fixHeading() {
		
		Pose pose= mcl.getEstimatedPose();
		DebugNotes.msg("fixHeading: "+(pose.getHeading()-90));
		// reduce 90 since the sensor is 90 degrees to  the left		
		mcl.setPose(new Pose(pose.getX(),pose.getY(), pose.getHeading()-90));		
	}
	
   
    private boolean hasGoodPoseEstimation(int range) {
        float xr = mcl.getXRange();
        float yr = mcl.getYRange();        
        LCD.drawString("pRange:" + xr + " " +yr + "              ",0,3);

        return xr < range && yr < range;
    }
    
    
	
	
	@Override
	public void soundDetected() {		
		Waypoint w;
		switch(this.state) {	
			case PENDING_NAVIGATION_START_COMMAND:
				this.timer.cancel();
				
				w = this.map.getTargeWaypoint();
				LCD.drawString("dst "+ w.getX() +"," +w.getY(), 0, 4);
				navigateTo(State.NAVIGATING_CENTER, w);
				break;
				
			case PENDING_FLINE_START_COMMAND:
				this.timer.cancel();
				 w = this.map.getFirstLine();
				 
				 Pose p = navigator.getPoseProvider().getPose();
				 p.setHeading(90);   //make sure it is 90 and not the odometry  value
				 navigator.getPoseProvider().setPose(p);
				LCD.clear();
				LCD.drawString("loc "+ p.getX() +"," +p.getY(), 0, 3);
				LCD.drawString("loch "+ p.getHeading(), 0, 4);
				LCD.drawString("dst "+ w.getX() +"," +w.getY(), 0, 5);
				navigateTo(State.FLINE_END_NAVIGATION, w);
				
			default:
				break;
		}
	}

	



	@Override
	public void lineDetected() {
		switch(this.state) {
		   case ON_SIDE:												
				this.setState(State.NAVIGATING_FLINE);
				break;
			 
			case NAVIGATING_SLINE:				
				this.setState(State.NAVIGATING_SLINE);
				break;
				
		    case SLINE_END_NAVIGATION:
		    	this.setState(State.SLINE_END_NAVIGATION);
		    	break;
			default:
				break;
		}
		
	}


	@Override
	public void endOfLine() {
		switch(this.state) {
			case NAVIGATING_FLINE:
				this.setState(State.NAVIGATING_SLINE);
				this.robot().driveForward();
				break;
				
			case NAVIGATING_SLINE:				
				robot().stopMoving();
				this.robot().detectHeading(60);
				this.prepareToNavigate();
				break;
				
			case FLINE_END_NAVIGATION:
				this.setState(State.SLINE_END_NAVIGATION);
				this.robot().driveForward();
				break;
				
			case SLINE_END_NAVIGATION:
				robot().endNvaigation();
				LCD.clear();
				LCD.drawString("FINISHED !", 0, 3);
				this.setState(State.ON_TARGET);
				break;
			default:
				break;	
		}				
	}

	@Override
	public State getState() {
		return this.state;
	}

	@Override
	public void atWaypoint(Waypoint waypoint, Pose pose, int sequence) {
		DebugNotes.msg ("wp#" +Math.round(waypoint.getX())  + ","+Math.round(waypoint.getY()) );
		DebugNotes.msg ("h#" +Math.round(waypoint.getHeading()) +"/"+ Math.round(pose.getHeading()));
		Waypoint w;
		 
		switch(state) {
			case FLINE_END_NAVIGATION:
				this.robot().driveForward();
				break;
			case NAVIGATING_FLINE:
				
				this.robot().driveForward();
				break;
			
			case NAVIGATING_CENTER:
				this.robot().stopMoving();
				Pose loc = this.robot().checkLocation();
				//DebugNotes.msgWithWait("x:"+loc.getX());
				//DebugNotes.msgWithWait("y:"+loc.getY());
				if (Math.abs(loc.getX()-waypoint.getX())>15 || Math.abs(loc.getY()-waypoint.getY())>15) {
					float angle =loc.angleTo(waypoint.getPose().getLocation())-90;
					float distance = loc.distanceTo(waypoint.getPose().getLocation());
					
					//DebugNotes.msgWithWait("px:" + loc.getX());
					//DebugNotes.msgWithWait("py:"  + loc.getY());
					
					//DebugNotes.msgWithWait("a:" + angle);
					//DebugNotes.msgWithWait("d:" + distance);
					this.robot().rotate(angle);
					this.robot().travel(distance);
					this.robot().rotate(-angle);
					
					loc = waypoint.getPose();
					loc.setHeading(90);
					navigator.getPoseProvider().setPose(loc);
					navigateTo(State.NAVIGATING_CENTER, waypoint);
				} 
			
				//this.robot().travel(-5);
				
				timer= new Timer();
				TimerTask task = new TimerTask() {	           
			        public void run() {				        	
			        	Waypoint w = map.getFirstLine();
						LCD.drawString("dst "+ w.getX() +"," +w.getY(), 0, 4);
			        	navigateTo(State.FLINE_END_NAVIGATION, w);
			        }
			    };
			    timer.schedule(task,30000);
			    
				//Sound.playSample(new File("onTarget.wav"), 70);
				this.setState(State.PENDING_FLINE_START_COMMAND);
				 
				break;
			default:
				//this.robot().stopMoving();
				//DebugNotes.msgWithWait("awp: "+ state );
				break;
		}
	
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pathComplete(Waypoint waypoint, Pose pose, int sequence) {
		//this.robot().stopMoving();
		
	}

	@Override
	public void pathInterrupted(Waypoint waypoint, Pose pose, int sequence) {
		//this.robot().stopMoving();
		
	}

}
