package mining;
import data.network.*;
/**
 * 
 * 
 * @author Annalisa
 *http://www.saedsayad.com/model_evaluation_r.htm
 */
public abstract  class EvaluationMeasure {
	protected Network n;
	
	EvaluationMeasure(Network n)
	{
		this.n=n;
	}
	
public 	abstract double compute();
}
