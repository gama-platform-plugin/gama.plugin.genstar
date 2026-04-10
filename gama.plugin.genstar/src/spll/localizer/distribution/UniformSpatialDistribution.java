package spll.localizer.distribution;

import gama.api.kernel.agent.IAgent;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.IShape;
import gama.api.types.list.IList;

/**
 * Uniform Spatial Distribution: each candidate has the same probability to be chosen 
 * 
 * @author patricktaillandier
 *
 * @param <N>
 */
public class UniformSpatialDistribution<N extends Number, E extends IShape> implements ISpatialDistribution<IShape> {
	
	private IList<? extends IShape> candidates;

	@Override
	public IShape getCandidate(IScope scope, IAgent entity, IList<? extends IShape>  candidates) {
		return candidates.anyValue(scope);
	}

	@Override
	public IShape getCandidate(IScope scope, IAgent entity) {
		if(this.candidates == null || this.candidates.isEmpty())
			throw new NullPointerException("No candidates have been setp - use ISpatialDistribution.setCandidates(List) first");
		return candidates.anyValue(scope);
	}

	@Override
	public void setCandidate(IList<? extends IShape>  candidates) {
		this.candidates = candidates;
	}

	@Override
	public IList<IShape> getCandidates(IScope scope) {
		return (IList<IShape>) candidates.copy(scope);
	}

	@Override
	public void removeNest(IShape n) {
		candidates.remove(n);
	}
	
}
