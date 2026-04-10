package spll.localizer.pointInalgo;

import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.GamaPoint;
import gama.api.types.geometry.IShape;
import gama.api.types.list.GamaListFactory;
import gama.api.types.list.IList;

public class CentroidPointinLocalizer implements PointInLocalizer{

	@Override
	public GamaPoint pointIn(IScope scope, IShape geom) {
		return (GamaPoint) geom.getLocation().copy(scope);
	}

	@Override
	public IList<GamaPoint> pointIn(IScope scope, IShape geom, int nb) {
		IList<GamaPoint> points = GamaListFactory.create();
		for (int i = 0; i < nb; i++)
			points.add(pointIn(scope, geom));
		return points;
	}


	
}
