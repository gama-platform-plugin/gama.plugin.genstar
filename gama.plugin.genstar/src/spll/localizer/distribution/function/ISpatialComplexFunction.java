package spll.localizer.distribution.function;

import java.util.function.BiFunction;

import gama.api.kernel.agent.IAgent;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.IShape;
import gama.api.types.list.IList;
/**
 * TODO javadoc
 * 
 * @author kevinchapuis
 *
 * @param <N>
 */
public interface ISpatialComplexFunction<N extends Number> extends BiFunction<IAgent,IShape, N> {

	/**
	 * TODO: javadoc
	 * 
	 * @param entities
	 * @param candidates
	 */
	public void updateFunctionState(IScope scope, IList<IAgent> entities, IList<IShape> candidates);
	
	/**
	 * TODO
	 */
	public void clear();
	
}
