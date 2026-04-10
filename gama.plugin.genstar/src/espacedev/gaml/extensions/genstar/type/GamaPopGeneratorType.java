/*******************************************************************************************************
 *
 * GamaPopGeneratorType.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package espacedev.gaml.extensions.genstar.type;

import gama.annotations.doc;
import gama.annotations.type;
import gama.annotations.support.IConcept;
import gama.annotations.support.ISymbolKind;
import gama.api.gaml.types.GamaType;
import gama.api.gaml.types.ITypesManager;
import gama.api.runtime.scope.IScope;

/**
 * The Class GamaPopGeneratorType.
 */
@type (
		name = GamaPopGeneratorType.GENERATORNAME,
		id = 938373948,
		wraps = { GamaPopGenerator.class },
		kind = ISymbolKind.REGULAR,
		concept = { IConcept.TYPE },
		doc = { @doc ("Represents a population generator that can be used to create agents") })
public class GamaPopGeneratorType extends GamaType<GamaPopGenerator> {

	public static final String GENERATORNAME = "gen_population_generator";
	public static final int GENERATOR_TYPE_ID = 938373948;

	public GamaPopGeneratorType(final ITypesManager tm) { super(tm); }

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	@doc ("Cast any gaml variable into a GamaPopGenerator used in generation process")
	public GamaPopGenerator cast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj instanceof GamaPopGenerator gpg) return gpg;
		return new GamaPopGenerator();
	}

	@Override
	public GamaPopGenerator getDefault() { return null; }

}
