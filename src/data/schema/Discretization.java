package data.schema;

import java.util.List;

public interface Discretization {
	List<Double> compute(int discretizationN) throws Exception;
}
