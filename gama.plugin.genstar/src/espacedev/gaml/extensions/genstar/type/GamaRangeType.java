/*******************************************************************************************************
 *
 * GamaRangeType.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.type;

import java.util.Arrays;

import gama.annotations.doc;
import gama.annotations.type;
import gama.annotations.support.IConcept;
import gama.api.gaml.types.Cast;
import gama.api.gaml.types.GamaType;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.ITypesManager;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.GamaPoint;
import gama.api.types.list.GamaList;
import gama.api.types.pair.GamaPair;

/**
 * The Class GamaRangeType.
 */
@type (
		name = "gen_range",
		id = GamaRangeType.RANGETYPE_ID,
		wraps = { GamaRange.class },
		concept = { IConcept.TYPE },
		doc = @doc ("The range type defined in the genstar plugin"))
public class GamaRangeType extends GamaType<GamaRange> {

	/** The Constant id. */
	public static final int RANGETYPE_ID = IType.BEGINNING_OF_CUSTOM_TYPES + 3524246;

	public GamaRangeType(final ITypesManager tm) { super(tm); }

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	@SuppressWarnings ({ "rawtypes" })
	@doc ("Cast a point (i.e. from x to y), a pair (i.e. from key to value), "
			+ "a list (i.e. from list[0] to list[1]) or a string into a GamaRange "
			+ "(i.e. spliting using \"->\", \":\", \";\", \"|\" or \" \")")
	public GamaRange cast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj instanceof GamaRange gr) return gr;
		if (obj instanceof GamaPoint p) return new GamaRange(p.x, p.y);
		if (obj instanceof GamaPair p) return new GamaRange(Cast.asFloat(scope, p.key()), Cast.asFloat(scope, p.value()));
		if (obj instanceof GamaList list) {
			if (list.size() == 2)
				return new GamaRange(Cast.asFloat(scope, list.get(0)), Cast.asFloat(scope, list.get(1)));
			return null;
		}
		if (obj instanceof String s) {
			for (String spliter : Arrays.asList("->", ":", ";", "|", " ")) {
				String[] list = s.split(spliter);
				if (list.length == 2) return new GamaRange(Cast.asFloat(scope, list[0]), Cast.asFloat(scope, list[1]));
			}
		}
		return null;
	}

	@Override
	public GamaRange getDefault() { return null; }

}
