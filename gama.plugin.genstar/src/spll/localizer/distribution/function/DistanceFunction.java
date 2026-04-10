package spll.localizer.distribution.function;

import gama.api.kernel.agent.IAgent;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.IShape;
import gama.api.types.list.IList;

public class DistanceFunction implements ISpatialComplexFunction<Double> {

	@Override
	public Double apply(IAgent entity, IShape geom) {
		return geom.euclidianDistanceTo(entity.getLocation());
	}

	@Override
	public void updateFunctionState(IScope scope, IList<IAgent> entities,
			IList<IShape> candidates) {
		
		
	}

	@Override
	public void clear() {
		
		
	}

}
