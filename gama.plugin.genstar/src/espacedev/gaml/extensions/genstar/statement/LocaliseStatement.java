package espacedev.gaml.extensions.genstar.statement;

import espacedev.gaml.extensions.genstar.localisation.IGenstarLocaliser;
import espacedev.gaml.extensions.genstar.statement.LocaliseStatement.LocaliseValidator;
import espacedev.gaml.extensions.genstar.utils.GenStarConstant;
import espacedev.gaml.extensions.genstar.utils.GenStarGamaUtils;
import gama.annotations.doc;
import gama.annotations.example;
import gama.annotations.facet;
import gama.annotations.facets;
import gama.annotations.inside;
import gama.annotations.symbol;
import gama.annotations.usage;
import gama.annotations.constants.IKeyword;
import gama.annotations.support.IConcept;
import gama.annotations.support.IOperatorCategory;
import gama.annotations.support.ISymbolKind;
import gama.api.annotations.validator;
import gama.api.compilation.descriptions.IDescription;
import gama.api.compilation.descriptions.IDescriptionValidator;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.expressions.IExpression;
import gama.api.gaml.statements.AbstractStatementSequence;
import gama.api.gaml.statements.IStatement.WithArgs;
import gama.api.gaml.symbols.Arguments;
import gama.api.gaml.types.Cast;
import gama.api.gaml.types.Types;
import gama.api.gaml.types.IType;
import gama.api.kernel.agent.IAgent;
import gama.api.kernel.agent.IPopulation;
import gama.api.kernel.species.ISpecies;
import gama.api.runtime.scope.IScope;
import gama.api.types.list.GamaListFactory;
import gama.api.types.list.IList;
import gama.api.types.misc.IContainer;
import gama.api.compilation.descriptions.IStatementDescription;
import gama.gaml.statements.RemoteSequence;

@symbol (
		name = GenStarConstant.GSLOCALISE,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		breakable = true,
		continuable = true,
		with_args = true,
		category = { IOperatorCategory.GENSTAR },
		concept = { IConcept.AGENT_LOCATION, IConcept.SPECIES },
		remote_context = true)
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets (
		value = { @facet (
						name = IKeyword.SPECIES,
						type = { IType.SPECIES, IType.AGENT, IType.CONTAINER },
						optional = false,
						doc = @doc ("The species of the agents to be localised.")),
				@facet (
						name = GenStarConstant.NESTS,
						type = {IType.CONTAINER, IType.SPECIES},
						optional = false,
						doc = @doc (
								value = """
										To specify the input data used to inform the localisation process. Various data input can be used: <br/>
										 * a list of geometry <br/>
										 * a shapefile or a rasterfile
										 """)),
				@facet (
						name = GenStarConstant.MAXDISTLOCALIZATIONCST,
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = """
										The maximal distance acceptable for the relaxation of the localization constraint
										 """)),
				@facet (
						name = GenStarConstant.STEPISTLOCALIZATIONCST,
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = """
										The distance added at each step for the relaxation of the localization constraint
										 """)),
				@facet (
						name = GenStarConstant.NESTATTRIBUTES,
						type = IType.STRING,
						optional = true,
						doc = @doc (
								value = """
										To specify the attribute of the nest
										 """)),
			
				@facet (
						name = GenStarConstant.DISTRIBUTION,
						type = IType.STRING,
						optional = true,
						doc = @doc (
								value = """
										The type of distribution to use ('area', 'uniform',)
										 """)),
				@facet (
						name = GenStarConstant.MINDIST,
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = """
										The min distance to the nest geometry
										 """)),
				@facet (
						name = GenStarConstant.MAXDIST,
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = """
										The max distance to the nest geometry
										 """)),
			
			
			@facet (
						name = GenStarConstant.MATCHER,
						type = IType.MAP,
						optional = true,
						doc = @doc (
								value = """
										To specify the matcher, map with three elements: 
										'entities': list of entities with the ; 
										'data_id': name of the property that contains the id of the census spatial areas in the shapefile;
										'pop_id': name of the property that contains the id of the census spatial areas in the population
										 """)),
			@facet (
					name = GenStarConstant.MAPPER,
					type = IType.MAP,
					optional = true,
					doc = @doc (
							value = """
									To specify the mapper, map with three elements: 
									'entities': list of entities with the ; 
									'data_id': name of the property that contains the id of the census spatial areas in the shapefile;
									 """)),
			@facet (
					name = GenStarConstant.CONSTRAINTS,
					type = IType.LIST,
					optional = true,
					doc = @doc (
							value = """
									To specify the constraints, a list of maps, each map representing a constraint with its own parameters
									 """)),
		
				@facet (
						name = GenStarConstant.GSFEATURE,
						type = { IType.STRING },
						optional = true,
						doc = @doc ("To specify the attribute of a geometry to be used to inform localisation process")) })
@doc (
		value = "Allows to localise a set of agent within a given set of geometries",
		usages = { @usage (
				value = "The synthax to monitor the simplest localisation process",
				examples = { @example (
						value = """
								localise species:people from:[shape_file("../includes/minimalexample.shp")];
								""",
						isExecutable = false) }) })
@validator (LocaliseValidator.class)
public class LocaliseStatement extends AbstractStatementSequence implements WithArgs {

	/** The init. */
	private Arguments init;
	
	// ----------------
	
	/** The nests. */
	private final IExpression nests;
	

	/** The nest attibute. */
	private final IExpression nestAttribute;
	
	/** The matcher. */
	private final IExpression matcher;
	
	/** The mapper. */
	private final IExpression mapper;
	
	/** The distribution. */
	private final IExpression distribution;
	
	/** The constraints. */
	private final IExpression constraints;
	
	/** The species. */
	private final IExpression species;
	
	/** The feature within a geometry to custom the localisation process **/
	private final IExpression feature;
	
	/** Min dist to the geometry of the nest. */
	private final IExpression minDist;
	
	/** Max dist to the geometry of the nest. */
	private final IExpression maxDist;
	
	/** Max dist for the localization constraint. */
	private final IExpression maxDistLocCst ;
	
	/** Step dist for the localization constraint. */
	private final IExpression stepDistLocCst;
	
	
	// -----------------
	
	/** The sequence. */
	private final RemoteSequence sequence;
	
	public LocaliseStatement(IDescription desc) {
		super(desc);
		this.nests = getFacet(GenStarConstant.NESTS);
		this.species = getFacet(IKeyword.SPECIES);
		this.feature = getFacet(GenStarConstant.GSFEATURE);
		this.matcher = getFacet(GenStarConstant.MATCHER);
		this.mapper = getFacet(GenStarConstant.MAPPER);
		this.nestAttribute = getFacet(GenStarConstant.NESTATTRIBUTES);
		this.distribution = getFacet(GenStarConstant.DISTRIBUTION);
		this.minDist = getFacet(GenStarConstant.MINDIST);
		this.maxDist = getFacet(GenStarConstant.MAXDIST);
		this.constraints = getFacet(GenStarConstant.CONSTRAINTS);
		this.maxDistLocCst = getFacet(GenStarConstant.MAXDISTLOCALIZATIONCST);
		this.stepDistLocCst = getFacet(GenStarConstant.STEPISTLOCALIZATIONCST);
		
		sequence = new RemoteSequence(description);
		sequence.setName("commands for the localisation");
		setName(GenStarConstant.GSLOCALISE);
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public IContainer<?, IAgent> privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// Just retrieve the population of agent to be localised
		// TODO : see if we can transfer this to any geometry
		IContainer<?, IAgent> pop = null;
		Object obj = species.value(scope);
		
		if (obj instanceof ISpecies) {
			pop = (IPopulation) ((ISpecies) obj).getPopulation(scope);
		} if (obj instanceof IAgent) {
			IAgent ag = Cast.asAgent(scope, obj);
			pop = GamaListFactory.create();
			((IList) pop).add(ag);
		} else {
			pop = (IList) Types.LIST.cast(scope, obj, null, false);
		}
		/*ISpecies s = Cast.asSpecies(scope, species.value(scope));
		if (s == null) {// A last attempt in order to fix #2466
				final String potentialSpeciesName = species.getDenotedType().getSpeciesName();
				if (potentialSpeciesName != null) { s = scope.getModel().getSpecies(potentialSpeciesName); }
			}
			if (s == null) throw GamaRuntimeException.error(
					"No population of " + species.serialize(false) + " is accessible in the context of " + scope.getAgent() + ".",
					scope);
			pop = scope.getAgent().getPopulationFor(s);
			
		}*/
		
		// TODO select among several localiser when they will be defined
		IGenstarLocaliser genl = GenStarGamaUtils.getGamaLocaliser()[0]; 
		
//		StreamEx.of(GenStarGamaUtils.getGamaLocaliser()).findFirst(g -> g.sourceMatch(scope, source))
//				.orElseThrow(IllegalArgumentException::new).generate(scope, inits, max, source, attributes.value(scope),
//						algorithm == null ? null : algorithm.value(scope), init, this);

		// TODO fill in the proper set of attributes to localise population
		genl.localise(scope, pop, nests.value(scope), this);
		
		return pop;
	}
	
	@Override
	public void setFormalArgs(Arguments args) { init = args; }

	
	
	public IExpression getDistribution() {
		return distribution;
	}

	public IExpression getMatcher() {
		return matcher;
	}

	public IExpression getMapper() {
		return mapper;
	}

	public IExpression getMinDist() {
		return minDist;
	}

	public IExpression getMaxDist() {
		return maxDist;
	}

	public IExpression getNestAttribute() {
		return nestAttribute;
	}


	public IExpression getMaxDistLocCst() {
		return maxDistLocCst;
	}

	public IExpression getStepDistLocCst() {
		return stepDistLocCst;
	}

	public IExpression getConstraints() {
		return constraints;
	}




	public static class LocaliseValidator implements IDescriptionValidator<IStatementDescription> {

		@Override
		public void validate(IStatementDescription description) {
			
			// ****** 
			// Minimal check on species - not taking into account all the specific cases copy/past from CreateStatement 
			
		/*	final IExpression species = description.getFacetExpr(SPECIES);
			// If the species cannot be determined, issue an error and leave validation
			if (species == null) {
				description.error("The species is not found", UNKNOWN_SPECIES, SPECIES);
				return;
			}

			final SpeciesDescription sd = species.getGamlType().getDenotedSpecies();
			if (sd == null) {
				description.error("The species to instantiate cannot be determined", UNKNOWN_SPECIES, SPECIES,
						species.getName());
				return;
			}
			
			// ******
			// Check on the various possible cases of From input types
			final IExpression exp = description.getFacetExpr(FROM);
			if (exp != null) {
				final IType type = exp.getGamlType();
				boolean found = false;
				if (type == Types.LIST) {
					// TODO verify if the list is actually a list of geometries
					found = true;
				}
				if (type == Types.FILE) {
					// TODO verify if the file is actually a shape or raster file
					found = true;
				}
				if (!found) {
					description.warning(
							"Facet 'from' expects an expression with one of the following types: " 
									+ Arrays.asList(Types.SPECIES,Types.LIST,Types.FILE),
							WRONG_TYPE, FROM);
					
				}
			}*/
			
		}
		
	}
	
}
