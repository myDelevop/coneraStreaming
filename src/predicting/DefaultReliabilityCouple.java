package predicting;

import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public class DefaultReliabilityCouple extends ReliabilityCouple {

	DefaultReliabilityCouple(int i, Node n, Neighbourhood nh) {
		super(i, n, nh);
		// TODO Auto-generated constructor stub
	}

	@Override
	void computeReliability() {
		// TODO Auto-generated method stub
		this.reliability=0;
	}

}
