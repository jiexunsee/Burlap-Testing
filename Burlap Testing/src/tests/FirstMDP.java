package tests;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.*;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.core.TerminalFunction;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.oomdp.statehashing.DiscretizingHashableStateFactory;
import burlap.oomdp.statehashing.*;

public class FirstMDP {

	GraphDefinedDomain					gdd;
    Domain								domain;
    State								initialState;
    RewardFunction						rf;
    TerminalFunction					tf;
    DiscretizingHashableStateFactory	hashFactory;
    int									numStates;
    
	
	public FirstMDP(double p1, double p2, double p3, double p4) {
		numStates = 6;
		this.gdd = new GraphDefinedDomain();
		
		//actions from initial state 0
		((GraphDefinedDomain) this.gdd).setTransition(0, 0, 1, 1.0);
		((GraphDefinedDomain) this.gdd).setTransition(0, 1, 2, 1.0);
		((GraphDefinedDomain) this.gdd).setTransition(0, 2, 3, 1.0);
		((GraphDefinedDomain) this.gdd).setTransition(1 ,0 ,1 ,1.0);
        ((GraphDefinedDomain) this.gdd).setTransition(2, 0, 4, 1.0);
        ((GraphDefinedDomain) this.gdd).setTransition(4, 0, 2, 1.0);
        ((GraphDefinedDomain) this.gdd).setTransition(3, 0, 5, 1.0);
        ((GraphDefinedDomain) this.gdd).setTransition(5, 0, 5, 1.0);
		
		this.domain = gdd.generateDomain();
		this.initialState = GraphDefinedDomain.getState(this.domain, 0);
		this.rf = new FourParamRF(p1,p2,p3,p4);
		this.tf = new NullTermination();
		
		this.hashFactory = new DiscretizingHashableStateFactory(0); //not sure what this does
	}
	
	public static class FourParamRF implements RewardFunction {
		double p1;
		double p2;
		double p3;
		double p4;
		
		public FourParamRF(double p1, double p2, double p3, double p4) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
		}
		
		@Override
		// TODO:
		// Override the reward method to match the reward scheme from the state diagram.
		// See the documentation for the RewardFunction interface for the proper signature.
		// You may find the getNodeId method from GraphDefinedDomain class helpful.
		public double reward(State s, GroundedAction a, State sprime) {
			int sid = GraphDefinedDomain.getNodeId(s);
			if (sid==1) return p1;
			else if (sid==2) return p2;
			else if (sid==5) return p4;
			else if (sid==4) return p3;
			else return 0;
		}
    }
	
	public Domain getDomain() {
        return this.domain;
    }
	
	private ValueIteration computeValue(double gamma) {
		double maxDelta = 0.0001;
		int maxIterations = 1000;
		ValueIteration vi = new ValueIteration(this.domain, this.rf, this.tf, gamma, this.hashFactory, maxDelta, maxIterations);
		vi.planFromState(this.initialState);
		return vi;
	}
	
	public String bestFirstMethod(double gamma) {
		ValueIteration vi = computeValue(gamma);
		
		State s1 = GraphDefinedDomain.getState(this.domain, 1);
		State s2 = GraphDefinedDomain.getState(this.domain, 2);
		State s3 = GraphDefinedDomain.getState(this.domain, 3);
		
		double v1 = vi.value(s1);
		double v2 = vi.value(s2);
		double v3 = vi.value(s3);
		
		if (v1>=v2 && v1>=v3) return "action A";
		else if (v2>=v3 && v2>v1) return "action B";
		else return "action C";
	}
	
	public static void main(String[] args) {
        // You can add test code here that will be executed when you click "Test Run".
    	FirstMDP fmdp = new FirstMDP(5,5,6,7);
    	System.out.println("Best first method is: "+fmdp.bestFirstMethod(0.8)); //cool, gives best method
    	
    	
    	//how to print the dimensions of an array.
    	int array[][][] = new int[3][4][5];
    	System.out.println(array.length+","+array[0].length+","+array[0][0].length);
    }
}
