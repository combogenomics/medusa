
import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import utilities.GexfReader;
import utilities.GexfWriter;
import utilities.N50;

public class Scaffolder {
	public static final int VERSION_MAJOR = 1;
	public static final int VERSION_MINOR = 6;
    public static void main(String[] args) throws Exception {
		new Scaffolder(args);
	}

	public Scaffolder(String[] args) throws Exception {
		runOnTerminal(args);
	}

	@SuppressWarnings("static-access")
	private void runOnTerminal(String[] args) throws Exception {
		Options opts = new Options();

		Option input = OptionBuilder
				.withArgName("<targetGenome>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"REQUIRED PARAMETER;The option *-i* indicates the name of the target genome file.")
				.create("i");
		opts.addOption(input);

		Option output = OptionBuilder
				.withArgName("<outputName>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER; The option *-o* indicates the name of output fasta file.")
				.create("o");
		opts.addOption(output);

		Option f = OptionBuilder
				.withArgName("<draftsFolder>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER; The option *-f* is optional and indicates the path to the comparison drafts folder")
				.create("f");
		opts.addOption(f);

		Option scriptPath = OptionBuilder
				.withArgName("<medusaScriptsFolder>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER; The folder containing the medusa scripts. Default value: medusa_scripts")
				.create("scriptPath");
		opts.addOption(scriptPath);

		Option verbose = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"RECOMMENDED PARAMETER; The option *-v* (recommended) print on console the information given by the package MUMmer. This option is strongly suggested to understand if MUMmer is not running properly.")
				.create("v");
		opts.addOption(verbose);

		Option weightScheme2 = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER;The option *-w2* is optional and allows for a sequence similarity based weighting scheme. Using a different weighting scheme may lead to better results.")
				.create("w2");
		opts.addOption(weightScheme2);

		Option gexf = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER;Conting network and path cover are given in gexf format.")
				.create("gexf");
		opts.addOption(gexf);

                Option threads = OptionBuilder
                                .withArgName("<numberOfThreads>")
                                .hasArgs(1)
                                .withValueSeparator()
                                .withDescription(
                                                "OPTIONAL PARAMETER; The option *-threads* indicates the number of threads to be used with mummer (requires version >= 4.0)")
                                .create("threads");
                opts.addOption(threads);

		Option help = OptionBuilder.withValueSeparator()
				.withDescription("Print this help and exist.").create("h");
		opts.addOption(help);

		Option distances = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER;The option *-d* allows for the estimation of the distance between pairs of contigs based on the reference genome(s): in this case the scaffolded contigs will be separated by a number of N characters equal to this estimate. The estimated distances are also saved in the <targetGenome>_distanceTable file. By default the scaffolded contigs are separated by 100 Ns")
				.create("d");
		opts.addOption(distances);

		Option random = OptionBuilder
				.withArgName("<numberOfRounds>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER;The option *-random* is available (not required). This option allows the user to run a given number of cleaning rounds and keep the best solution. Since the variability is small 5 rounds are usually sufficient to find the best score.")
				.create("random");
		opts.addOption(random);

		Option n50 = OptionBuilder.withArgName("<fastaFile>").hasArgs(1)
				.withValueSeparator()
				.withDescription("OPTIONAL PARAMETER; The option *-n50* allows the calculation of the N50 statistic on a FASTA file. In this case the usage is the following: java -jar medusa.jar -n50 <name_of_the_fasta>. All the other options will be ignored.")
				.create("n50");
		opts.addOption(n50);

		BasicParser bp = new BasicParser();
		try {

			CommandLine cl = bp.parse(opts, args);
			if (cl.hasOption("h")
					|| (!cl.hasOption("i") && !cl.hasOption("n50"))) {
				printHelp(opts);
			}else if (cl.hasOption("n50")) {
				n50avaluation(cl);
			}else if (cl.hasOption("i")) {
				scaffolder(cl);
			}
		} catch (UnrecognizedOptionException uoe) {
			printHelp(opts);
		}
	}

	private void printHelp(Options opts) {
		System.out.println(String.format("Medusa version %d.%d", VERSION_MAJOR,
				VERSION_MINOR));
		HelpFormatter f1 = new HelpFormatter();
		f1.printHelp("java -jar medusa.jar -i inputfile -v",
				"available options: ", opts, "");

	}

	private int computeLenght(ArrayList<String> paths) {
		int l = 0;
		String current;
		for (int i = 0; i < paths.size(); i++) {
			current = paths.get(i);
			String[] currentSplit = current.split("@");
			int le = Integer.parseInt(currentSplit[1].replaceFirst("@", ""));
			l += le;
		}
		return l;
	}

	private void scaffolder(CommandLine cl) throws Exception {

		String input = cl.getOptionValues("i")[0];
		System.out.println("INPUT FILE:" + input);
		int rounds = 1;
		if (cl.getOptionValue("random") != null) {
			rounds = Integer.parseInt(cl.getOptionValue("random"));
			System.out.println("Random rounds:" + rounds);
		}
		String scaffoldsfilename = input + "Scaffold.fasta";
		if (cl.getOptionValue("o") != null) {
			scaffoldsfilename = cl.getOptionValue("o");
		}
		String draftsFolder = "drafts";
		if (cl.getOptionValue("f") != null) {
			draftsFolder = cl.getOptionValue("f");
		}
		String medusaScripts = "medusa_scripts";
		if (cl.getOptionValue("scriptPath") != null) {
			medusaScripts = cl.getOptionValue("scriptPath");
		}
		Boolean distanceEstimation =false;
		if(cl.hasOption("d")){
			distanceEstimation = true;
		}
		String threads = "";
                if (cl.getOptionValue("threads") != null) {
                        threads = cl.getOptionValue("threads");
                }

		/*
		 * #######################################################################
		 * STEP1: Mapping the target contigs onto the comparison genomes using
		 * the MUMmer software.
		 * #######################################################################
		 *
		 */

		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		String line;
		System.out.print("Running MUMmer...");
		Process process = new ProcessBuilder(medusaScripts + "/mmrBatch.sh",
				draftsFolder, input, medusaScripts,threads).start();
		BufferedReader errors = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		if (cl.hasOption("v")) {
			while ((line = errors.readLine()) != null) {
				System.out.println(line);
			}
			if (process.waitFor() != 0) {
				throw new RuntimeException("Error running MUMmer.");
			}
		} else {
			while ((line = errors.readLine()) != null) {
			}
			if (process.waitFor() != 0) {
				throw new RuntimeException("Error running MUMmer.");
			}
		}

		System.out.print("done.\n");


		/*
		 * #######################################################################
		 * STEP2: The adjacencies collected by MUMmer are used by a python script to populate an
		 * undirected weighted graph.
		 * #######################################################################
		 */
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		System.out.print("Building the network...");

		String current = new java.io.File(".").getCanonicalPath();

		if (cl.hasOption("w2")) {// this weight takes in account the quality of the hits.
			process = new ProcessBuilder("python3", medusaScripts
					+ "/netcon_mummer.py", "-f" + current, "-i" + input,
					"-onetwork", "-w").start();
			errors = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			while ((line = errors.readLine()) != null) {
				System.out.println(line);
			}
			if (process.waitFor() != 0) {
				throw new RuntimeException(
						"Error: Network construction failed.");
			}
		} else {// default weight scheme
			process = new ProcessBuilder("python3", medusaScripts
					+ "/netcon_mummer.py", "-f" + current, "-i" + input,
					"-onetwork").start();
			errors = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			while ((line = errors.readLine()) != null) {
				System.out.println(line);
			}
			if (process.waitFor() != 0) {
				throw new RuntimeException(
						"Error: Network construction failed.");
			}
		}
		/*
		 * The network generated by the python script is stored in an object belonging to the class MyGraph.
		 */
		MyGraph graph = GexfReader.read("network");

		// Remove temporary files.
		File network = new File("network");
		network.delete();
		File dir = new File(".");
		for (String address : dir.list((File dir1, String name) -> name.toLowerCase().endsWith(".coords"))) {
			File f = new File(address);
			f.delete();
		}
		for (String address : dir.list((File dir1, String name) -> name.toLowerCase().endsWith(".delta"))) {
			File f = new File(address);
			f.delete();
		}

		if (graph.getEdges().size() == 0) {
			System.out
					.println("SORRY: No information found. Are you sure to have MUMmer packedge location in your PATH? If yes, the chosen drafts genomes don't provide sufficient information for scaffolding the target genome.");
			return;
		}
		System.out.print("done.\n");

		/*
		 * #######################################################################
		 * STEP3: Giving an order to the contigs.
		 * The order is assigned by transform the graph into a disjoint path cover.
		 * This cover is again stored in a MyGraph object.
		 * #######################################################################
		 */

		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		System.out.print("Cleaning the network...");

		double factor = 1;
		for (MyEdge e : graph.getEdges()) {
			factor += e.getWeight();
		}
		MyGraph supportGraph = new MyGraph(graph);// work on a copy of the graph to keep the original network available.
		MyGraph cover = greedyCover(supportGraph, factor);

		/*
		 * #######################################################################
		 * STEP4: Give a orientation to the contigs.
		 * The cover is processed in order to assign an orientation to each contig.
		 * The paths of the cover are traversed and, for each node, a consistent orientation is assigned
		 * to its sequence.
		 * #######################################################################
		 */

		cover.cleanOrinetation();

		/*
		 * RANDOM OPTION: Give a orientation to the contigs.
		 * If the random option is present more then one candidate cover is taken in account.
		 * STEP3 and STEP4 are executed each time, and the best solution is taken.
		 */
		if (rounds > 1) {
			System.out.println("Candidate cover size: "
					+ cover.getEdges().size());
			for (int i = 2; i <= rounds; i++) {
				supportGraph = new MyGraph(graph);
				MyGraph candidateCover = greedyCoverRandom(supportGraph,
						factor);
				candidateCover.cleanOrinetation();
				System.out.println("Candidate cover size: "
						+ candidateCover.getEdges().size());
				if (candidateCover.getEdges().size() > cover.getEdges().size()) {
					cover = candidateCover;
				}
			}
			System.out.println("Best cover size: " + cover.getEdges().size());
		}
		System.out.print("done.\n");


		/*
		 * #######################################################################
		 * FINAL OUTPUT: The paths in the cover can be finally read as scaffolds.
		 * Each node is now associated to a properly oriented sequence.
		 * Default and optional output files are created.
		 * #######################################################################
		 */
		System.out.println("------------------------------------------------------------------------------------------------------------------------");

		/*
		 * DEFAULT OUTPUTS:
		 *
		 * ouptut: A textual summary of the results.
		 * output2: A .fasta file containing a sequence for each scaffold.
		 *
		 */

		LinkedHashMap<String, ProteinSequence> a = FastaReaderHelper.readFastaProteinSequence(new File(input));
		HashMap<String, String> sequences = new HashMap<>();
		for (ProteinSequence s : a.values()) {
			sequences.put(s.getOriginalHeader().split(" ")[0],
					s.getSequenceAsString());
		}

		ArrayList<String> scaffolds = cover.readScaffoldsSeq(input, sequences, distanceEstimation);
		File outputFile2 = new File(scaffoldsfilename);
		PrintWriter writerOutput2 = new PrintWriter(new FileWriter(outputFile2));
		int j = 1;
		for (String s : scaffolds) {
			writerOutput2.println(">Scaffold_" + j);
			writerOutput2.println(s);
			j++;
		}

		for (MyNode n : cover.getNodes()) {
			if (n.getDegree() == 0) {
				writerOutput2.println(">Scaffold_" + j);
				writerOutput2.println(sequences.get(n.getId()));
				j++;
			}
		}

		writerOutput2.flush();
		System.out.println("Scaffolds File saved: " + outputFile2);

		System.out.println("------------------------------------------------------------------------------------------------------------------------");

		File outputFile = new File(input + "_SUMMARY");
		PrintWriter writerOutput = new PrintWriter(new FileWriter(outputFile));
		writerOutput.write("Target genome: " + input + "\n");

		writerOutput.println("\n--------------SCAFFOLDS---------------\n");

		int finalSingletons = (cover.getNodes().size() - cover.notSingletons());
		ArrayList<String> paths = cover.subPaths();
		int totalLength = computeLenght(paths);

		int numberOfScaffolds = paths.size() + finalSingletons;

		System.out.println("Number of scaffolds: " + numberOfScaffolds
				+ " (singletons = " + finalSingletons
				+ ", multi-contig scaffold = " + paths.size() + ") \nfrom "
				+ cover.getNodes().size() + " initial fragments.");
		System.out.println("Total length of the jointed fragments: "
				+ totalLength);
		double in50 = n50avaluation(scaffoldsfilename);

		writerOutput.println("Number of scaffolds: " + numberOfScaffolds
				+ " (singletons = " + finalSingletons
				+ ", multi-contig scaffold = " + paths.size() + ") \nfrom "
				+ cover.getNodes().size() + " initial fragments.");
		writerOutput.println("Total length of the jointed fragments: "
				+ totalLength);
		writerOutput.println("Computing N50 on " + numberOfScaffolds + " sequences.");
		writerOutput.println("N50: " + in50);
		writerOutput.println("----------------------");
		writerOutput.flush();
		System.out.println("Summary File saved: " + outputFile);

		/*
		 * OPTIONAL OUTPUT:
		 * The network and the cover are outputted in .gexf format.
		 */

		if (cl.hasOption("gexf")) {
			System.out
					.println("------------------------------------------------------------------------------------------------------------------------");
			GexfWriter.write(graph, input + "_network.gexf");
			GexfWriter.write(cover, input + "_cover.gexf");
		}
		if (cl.hasOption("d")) {
			System.out
					.println("------------------------------------------------------------------------------------------------------------------------");
			MyGraph.writeDistanceFile(cover, input + "_distanceTable");
		}
	}


	/*
	 * #######################################################################
	 * #######################################################################
	 * ADDITIONAL METHODS.
	 * #######################################################################
	 */

	private MyGraph greedyCoverRandom(MyGraph network, double factor) {
		ArrayList<MyEdge> candidateEdges = new ArrayList<MyEdge>(
				network.getEdges());
		for (MyEdge edge : candidateEdges) {
			double w = edge.getWeight() + factor;
			edge.setWeight(w);

		}
		ArrayList<MyEdge> chosenEdges = new ArrayList<MyEdge>();
		MyGraph cover = new MyGraph(network.getNodes(), chosenEdges);
		for (MyNode n : cover.getNodes()) {
			n.setAdj(new ArrayList<MyNode>());
		}
		HashMap<MyNode, MyNode> twins = new HashMap<MyNode, MyNode>();
		for (MyNode n : network.getNodes()) {
			twins.put(n, n);
		}
		IdComparator comparatorId = new IdComparator();
		weightComparator comparatorW = new weightComparator();
		Collections.sort(candidateEdges, comparatorId);// ID sorting
		Collections.shuffle(candidateEdges);
		Collections.sort(candidateEdges, comparatorW);// weight sorting
		Collections.reverse(candidateEdges);
		while (!candidateEdges.isEmpty()) {
			MyEdge candidate = candidateEdges.get(0);
			MyNode source = candidate.getSource();
			MyNode target = candidate.getTarget();
			if (twins.get(source) == target) {
				candidateEdges.remove(candidate);
			} else {
				cover.addEdge(candidate);
				MyNode ps = twins.get(source);
				MyNode pt = twins.get(target);
				twins.put(ps, pt);
				twins.put(pt, ps);
				if (cover.nodeFromId(target.getId()).getDegree() > 1) {
					ArrayList<MyEdge> a = network.inoutEdges(target);
					candidateEdges.removeAll(a);
				} else {
					candidateEdges.remove(candidate);
				}
				if (cover.nodeFromId(source.getId()).getDegree() > 1) {
					ArrayList<MyEdge> b = network.inoutEdges(source);
					candidateEdges.removeAll(b);
				} else {
					candidateEdges.remove(candidate);
				}
			}

		}
		return cover;

	}

	private static MyGraph greedyCover(MyGraph network, double factor) {
		ArrayList<MyEdge> candidateEdges = new ArrayList<MyEdge>(
				network.getEdges());
		for (MyEdge edge : candidateEdges) {
			double w = edge.getWeight() + factor;
			edge.setWeight(w);

		}
		ArrayList<MyEdge> chosenEdges = new ArrayList<MyEdge>();
		MyGraph cover = new MyGraph(network.getNodes(), chosenEdges);
		for (MyNode n : cover.getNodes()) {
			n.setAdj(new ArrayList<MyNode>());
		}
		HashMap<MyNode, MyNode> twins = new HashMap<MyNode, MyNode>();
		for (MyNode n : network.getNodes()) {
			twins.put(n, n);
		}
		IdComparator comparatorId = new IdComparator();
		Collections.sort(candidateEdges, comparatorId);
		weightComparator comparatorW = new weightComparator();
		Collections.sort(candidateEdges, comparatorW);
		Collections.reverse(candidateEdges);

		for (int i = 0; i < candidateEdges.size() - 1; i++) {
			if (candidateEdges.get(i).getWeight() < candidateEdges.get(i + 1)
					.getWeight()) {
				System.out.println("ERROR: "
						+ candidateEdges.get(i).toStringVerbose() + " > "
						+ candidateEdges.get(i + 1).toStringVerbose());
			} else if (candidateEdges.get(i).getWeight() == candidateEdges.get(
					i + 1).getWeight()) {

			} else if (candidateEdges.get(i).getWeight() > candidateEdges.get(
					i + 1).getWeight()) {

			}
		}


		while (!candidateEdges.isEmpty()) {
			MyEdge candidate = candidateEdges.get(0);
			MyNode source = candidate.getSource();
			MyNode target = candidate.getTarget();
			if (twins.get(source) == target) {
				candidateEdges.remove(candidate);
			} else {
				cover.addEdge(candidate);
				MyNode ps = twins.get(source);
				MyNode pt = twins.get(target);
				twins.put(ps, pt);
				twins.put(pt, ps);
				if (cover.nodeFromId(target.getId()).getDegree() > 1) {
					ArrayList<MyEdge> a = network.inoutEdges(target);
					candidateEdges.removeAll(a);
				} else {
					candidateEdges.remove(candidate);
				}
				if (cover.nodeFromId(source.getId()).getDegree() > 1) {
					ArrayList<MyEdge> b = network.inoutEdges(source);
					candidateEdges.removeAll(b);
				} else {
					candidateEdges.remove(candidate);
				}
			}

		}
		return cover;
	}

	/*
	 * N50 EVALUATOR:
	 * This method read a .fasta file and  write on console three values.
	 * These three values corresponds to the N50 statistic for the given set of sequences.
	 */
	private void n50avaluation(CommandLine cl) throws Exception {
		ArrayList<Integer> lenghts = new ArrayList<>();
		LinkedHashMap<String, ProteinSequence> a = FastaReaderHelper
				.readFastaProteinSequence(new File(cl.getOptionValue("n50")));
		for (Entry<String, ProteinSequence> entry : a.entrySet()) {
			int l = entry.getValue().getLength();
			lenghts.add(l);
		}
		System.out.println("Number of sequences in the file: " + lenghts.size());
		System.out.println("N50: " + N50.n50(lenghts));
		System.out.println("----------------------");

	}

	private double n50avaluation(String scaffoldsfilename) throws Exception {
		ArrayList<Integer> lenghts = new ArrayList<>();
		LinkedHashMap<String, ProteinSequence> a = FastaReaderHelper
				.readFastaProteinSequence(new File(scaffoldsfilename));
		for (Entry<String, ProteinSequence> entry : a.entrySet()) {
			int l = entry.getValue().getLength();
			lenghts.add(l);
		}
		System.out.println("Computing N50 on " + lenghts.size()+ " sequences.");
		System.out.println("N50: " + N50.n50(lenghts));
		System.out.println("----------------------");

		return N50.n50(lenghts);
	}
}
