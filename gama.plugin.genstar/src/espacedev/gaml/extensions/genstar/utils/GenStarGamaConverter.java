package espacedev.gaml.extensions.genstar.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;
import core.util.exception.GSIllegalRangedData;
import gama.annotations.constants.IKeyword;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.Types;
import gama.api.kernel.agent.IAgent;
import gama.api.runtime.scope.IScope;
import gama.api.types.list.GamaListFactory;
import gama.api.types.list.IList;
import gama.api.compilation.descriptions.ITypeDescription;
import gama.api.compilation.descriptions.IVariableDescription;
import gospl.GosplEntity;
/**
 * 
 * Meant to be a class with utils methods to convert Genstar object into Gama object
 * 
 * @author kevinchapuis
 *
 */
public class GenStarGamaConverter {
	
	/**
	 * 
	 * @param scope
	 * @param agents
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public static Set<Attribute<? extends IValue>> convertAttributesFromGamlToGenstar(
			IScope scope, IList<? extends IAgent> agents) throws GSIllegalRangedData {
		
		Set<Attribute<? extends IValue>> mySet = new HashSet<>();
		final AttributeFactory gaf = AttributeFactory.getFactory();
		
		final Set<String> NON_SAVEABLE_ATTRIBUTE_NAMES = new HashSet<>(Arrays.asList(IKeyword.PEERS,
				IKeyword.LOCATION, IKeyword.HOST, IKeyword.AGENTS, IKeyword.MEMBERS, IKeyword.SHAPE));
		final ITypeDescription species = agents.getGamlType().getContentType().getSpecies();

		for (IVariableDescription vd : species.getAttributes()) {
			if (NON_SAVEABLE_ATTRIBUTE_NAMES.contains(vd.getName())) { continue; }
			Attribute<? extends IValue> att = gaf.createAttribute(vd.getName(), getType(vd.getGamlType()),
					agents.stream(scope).map((IAgent a) -> a.getDirectVarValue(scope, vd.getName()).toString()).toList());
			mySet.add(att);
		}
		
		return mySet;
	}
	
	@SuppressWarnings("rawtypes")
	public static GSEnumDataType getType(IType gamaType) {
		if (gamaType == Types.INT) { return GSEnumDataType.Integer; }
		else if (gamaType == Types.FLOAT) {return GSEnumDataType.Continue;}
		else if (gamaType == Types.BOOL) {return GSEnumDataType.Boolean;}
		return GSEnumDataType.Nominal;
	}
	
	// ============================================== //
	
	// MAIN CONVERTION TOOLS BETWEEN GAMA AND GENSTAR //
	
	/**
	 * 
	 * @param agent
	 * @return
	 * @throws GSIllegalRangedData 
	 */
	public static GosplEntity convertToGospl(IAgent agent) throws GSIllegalRangedData {
		GosplEntity ge = new GosplEntity();
		Set<Attribute<? extends IValue>> atts = convertAttributesFromGamlToGenstar(agent.getScope(), 
				GamaListFactory.createWithoutCasting(Types.AGENT, agent)); 
		
		return ge;
	}
	
	
}
