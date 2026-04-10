/*******************************************************************************************************
 *
 * GenerateStatement.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.statement;

import java.util.List;
import java.util.Map;

import espacedev.gaml.extensions.genstar.generator.IGenstarGenerator;
import espacedev.gaml.extensions.genstar.statement.GenerateStatement.GenerateValidator;
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
import gama.api.compilation.descriptions.IExperimentDescription;
import gama.api.compilation.descriptions.IModelDescription;
import gama.api.compilation.descriptions.ISpeciesDescription;
import gama.api.compilation.descriptions.IStatementDescription;
import gama.api.compilation.descriptions.ITypeDescription;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.expressions.IExpression;
import gama.api.gaml.symbols.Arguments;
import gama.api.gaml.symbols.ISymbol;
import gama.api.gaml.types.Cast;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.Types;
import gama.api.kernel.agent.IAgent;
import gama.api.kernel.agent.IPopulation;
import gama.api.runtime.scope.IScope;
import gama.api.types.list.GamaListFactory;
import gama.api.types.list.IList;
import gama.gaml.statements.CreateStatement;
import gama.gaml.statements.RemoteSequence;
import one.util.streamex.StreamEx;

/**
 * The Class GenerateStatement.
 */
@symbol (
		name = GenerateStatement.GENERATE, //IKeyword.GENERATE,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		breakable = true,
		continuable = true,
		with_args = true,
		category = { IOperatorCategory.GENSTAR },
		concept = { IConcept.SPECIES },
		remote_context = true)
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets (
		value = { @facet (
						name = IKeyword.SPECIES,
						type = { IType.SPECIES, IType.AGENT },
						optional = true,
						doc = @doc ("The species of the agents to be created.")),
				@facet (
						name = IKeyword.FROM,
						type = IType.NONE,
						optional = false,
						doc = @doc (
								value = """
										To specify the input data used to inform the generation process. Various data input can be used:
										 * list of csv_file: can be aggregated or micro data
										 * matrix: describe the joint distribution of two attributes
										 * genstar generator: a dedicated gaml type to enclose various genstar options all in one""")),
				@facet (
						/*
						 * make those attributes like in csv map to directly recognize species' attributes rather than
						 * use string with potential mispells
						 */
						name = GenStarConstant.GSATTRIBUTES,
						type = { IType.MAP },
						optional = false,
						doc = @doc ("To specify the explicit link between agent attributes and file based attributes")),
				@facet (
						name = IKeyword.NUMBER,
						type = IType.INT,
						optional = true,
						doc = @doc (
								value = """
										To specify the number of created agents interpreted as an int value.
										If facet is ommited or value is 0 or less, generator will treat data used in the 'from' facet as contingencies
										(i.e. a count of entities) and infer a number to generate (if distribution is used, then only one entity will be created""")),
				@facet (
						name = GenStarConstant.GSGENERATOR,
						type = { IType.STRING },
						optional = true,
						doc = @doc ("To specify the type of generator you want to use: as of now there is only DS (or DirectSampling) available")) },
		omissible = IKeyword.SPECIES)

@doc (
		value = "Allows to create a synthetic population of agent from a set of given rules",
		usages = { @usage (
				value = "The synthax to create a minimal synthetic population from aggregated file is:",
				examples = { @example (
						value = """
								generate species:people number: 10000
								from:[csv_file("../includes/Age & Sexe-Tableau 1.csv",";")]
								attributes:["Age"::["Moins de 5 ans", "5 à 9 ans", "10 à 14 ans", "15 à 19 ans", "20 à 24 ans",
								"25 à 29 ans", "30 à 34 ans", "35 à 39 ans", "40 à 44 ans", "45 à 49 ans",
								"50 à 54 ans", "55 à 59 ans", "60 à 64 ans", "65 à 69 ans", "70 à 74 ans", "75 à 79 ans",
								"80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus"],
								"Sexe"::["Hommes", "Femmes"]];""",
						isExecutable = false) }) })
@validator (GenerateValidator.class)
public class GenerateStatement extends CreateStatement {//extends AbstractStatementSequence implements IStatement.WithArgs {

	/** From former Gama plugin within Gama */ 
	public final static String GENERATE = "generate";
	
	/** The init. */
	private Arguments init;

	/** The algorithm. */
	private final IExpression from;
	
	/** The number. */
	private final IExpression number;
	
	/** The attributes. */
	private final IExpression attributes;
	
	/** The algorithm. */
	private final IExpression algorithm;

	/** The returns. */
	private final String returns;

	/** The sequence. */
	private final RemoteSequence sequence;

	/**
	 * Instantiates a new generate statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public GenerateStatement(final IDescription desc) {
		super(desc);
		returns = getLiteral(IKeyword.RETURN);
		from = getFacet(IKeyword.FROM);
		number = getFacet(IKeyword.NUMBER);

		attributes = getFacet(GenStarConstant.GSATTRIBUTES);
		algorithm = getFacet(GenStarConstant.GSGENERATOR);

		sequence = new RemoteSequence(description);
		sequence.setName("commands of generate ");
		setName(GENERATE);
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public IList<? extends IAgent> privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// First, we compute the number of agents to create
		final Integer max = number == null ? null : Cast.asInt(scope, number.value(scope));
		if (from == null && max != null && max <= 0) return GamaListFactory.create(Types.NO_TYPE);

		// Next, we compute the species to instantiate
		final IPopulation pop = super.findPopulation(scope);
		// A check is made in order to address issues #2621 and #2611
		if (pop == null || pop.getSpecies() == null)
			throw GamaRuntimeException.error("Impossible to determine the species of the agents to generate", scope);
		checkPopulationValidity(pop, scope);

		// We grab whatever initial data are input
		final List<Map<String, Object>> inits = GamaListFactory.create(Types.MAP, max == null ? 10 : max);
		final Object source = from.value(scope);

		// Only one generator according to data input type (type of the source Object)
		StreamEx.of(GenStarGamaUtils.getGamaGenerator()).findFirst(g -> g.sourceMatch(scope, source))
				.orElseThrow(IllegalArgumentException::new).generate(scope, inits, max, source, attributes.value(scope),
						algorithm == null ? null : algorithm.value(scope), init, this);

		// and we create and return the agent(s)
		final IList<? extends IAgent> agents = pop.createAgents(scope, inits.size(), inits, false, false, sequence);
		if (returns != null) { scope.setVarValue(returns, agents); }
		return agents;
	}

	/**
	 * make the validator coherent with 'must contains' facets
	 *
	 * @author kevinchapuis
	 *
	 */
	public static class GenerateValidator implements IDescriptionValidator<IStatementDescription> {

		@SuppressWarnings ({ "unchecked", "rawtypes" })
		@Override
		public void validate(final IStatementDescription description) {
			final IExpression species = description.getFacetExpr(SPECIES);
			// If the species cannot be determined, issue an error and leave validation
			if (species == null) {
				description.error("The species is not found", UNKNOWN_SPECIES, SPECIES);
				return;
			}

			final ITypeDescription sd = species.getGamlType().getDenotedSpecies();
			if (!(sd instanceof ISpeciesDescription spec)) {
				description.error("The species to instantiate cannot be determined", UNKNOWN_SPECIES, SPECIES,
						species.getName());
				return;
			}

			if (species instanceof IExpression.Species) {
				final boolean abs = spec.isAbstract();
				final boolean mir = spec.isMirror();
				final boolean gri = spec.isGrid();
				if (abs || mir || gri) {
					final String p = abs ? "abstract" : mir ? "a mirror" : gri ? "a grid" : "";
					description.error(spec.getName() + " is " + p + " and cannot be instantiated", WRONG_TYPE, SPECIES);
					return;
				}
			} else if (!(sd instanceof IModelDescription)) {
				description.info(
						"The actual species will be determined at runtime. This can lead to errors if it cannot be instantiated",
						WRONG_TYPE, SPECIES);
			}

			final ITypeDescription callerSpecies = description.getTypeContext();
			if (sd instanceof IModelDescription && !(callerSpecies instanceof IExperimentDescription)) {
				description.error("Simulations can only be created within experiments", WRONG_CONTEXT, SPECIES);
				return;
			}

			final ISpeciesDescription macro = spec.getMacroSpecies();
			if (macro == null && !(sd instanceof IModelDescription)) {
				description.error("The macro-species of " + species + " cannot be determined");
				return;
				// hqnghi special case : create instances of model from model
			}
			if (macro instanceof IModelDescription && callerSpecies instanceof IModelDescription) {
				// end-hqnghi
			} else if (callerSpecies instanceof ISpeciesDescription callerSpeciesDesc && callerSpeciesDesc != macro
					&& !callerSpeciesDesc.hasMacroSpecies(macro) && !callerSpeciesDesc.hasParent(macro)) {
				description.error(
						"No instance of " + macro.getName() + " available for creating instances of " + sd.getName());
				return;
			}

			final IExpression exp = description.getFacetExpr(FROM);
			if (exp != null) {
				final IType type = exp.getGamlType();

				if (type.id() != 938373948) {
					boolean found = false;
					List<IType> types = StreamEx.of(GenStarGamaUtils.getGamaGenerator())
							.map(IGenstarGenerator::sourceType).toList();
					for (final IType genType : types) {
						found = genType.isAssignableFrom(type);
						if (found) { break; }
					}
					if (type == Types.MATRIX) {
						// TODO verify that x,y matrix match possible attributes values
					}
					if (!found) {
						description.warning(
								"Facet 'from' expects an expression with one of the following types: " + types,
								WRONG_TYPE, FROM);
					}
				}
			}
			
			final Arguments facets = description.getPassedArgs();
			facets.forEachFacet((s, e) -> {
				boolean error = !sd.isExperiment() && !sd.hasAttribute(s);
				if (error) {
					description.error("Attribute " + s + " is not defined in species " + species.getName(), UNKNOWN_VAR);
				}
				return !error;
			});

		}

	}
	
	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence.setChildren(com);
	}

}
