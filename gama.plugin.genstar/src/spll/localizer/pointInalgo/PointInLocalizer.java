package spll.localizer.pointInalgo;

import java.util.List;

import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.GamaPoint;
import gama.api.types.geometry.IShape;

public interface PointInLocalizer {

	public GamaPoint pointIn(IScope scope, IShape geom);

	public List<GamaPoint> pointIn(IScope scope,IShape geom, int nb);
	
}
