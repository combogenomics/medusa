import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

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

public class Scaffolder {
	public static final int VERSION_MAJOR = 1;
	public static final int VERSION_MINOR = 0;
	
	public static void main(String[] args)
			throws Exception {
		new Scaffolder(args);
	}

	public Scaffolder(String[] args)
			throws Exception {
		runOnTerminal(args);
	}

	@SuppressWarnings("static-access")
	private void runOnTerminal(String[] args)
			throws Exception {
		Options opts = new Options();

		Option input = OptionBuilder
				.withArgName("<targetGenome>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"REQUIRED PARAMETER; File name of the fasta file of the target genome.")
				.create("i");
		opts.addOption(input);
		
		Option output = OptionBuilder
				.withArgName("<outputName>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER; Ouptut file name. Default value: input.Scaffold.fasta")
				.create("o");
		opts.addOption(output);
		
		Option f = OptionBuilder
				.withArgName("<draftsFolder>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER; The folder containing the comparison genomes. Default value: drafts")
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
						"OPTIONAL PARAMETER;The stout of MUMmer is printed on console.")
				.create("v");
		opts.addOption(verbose);
		Option weightScheme2 = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER;The weight of a join is evaluated taking in account similarity and coverage of the sequences.")
				.create("w2");
		opts.addOption(weightScheme2);
		
		Option gexf = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER;Conting network and path cover are given in gexf format.")
				.create("gexf");
		opts.addOption(gexf);
		
		Option help = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"Print this help and exist.")
				.create("h");
		opts.addOption(help);
		
		
		Option distances = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"OPTIONAL PARAMETER;For each joint an estimation of the lenght of the gap is provided. This information is contained in the file *_distanceTable")
				.create("d");
		opts.addOption(distances);
		
		Option random = OptionBuilder.withArgName("<numberOfRounds>")
				.hasArgs(1).withValueSeparator()
				.withDescription("OPTIONAL PARAMETER; This (integer) value indicates how many randomly sampled solutions the tool evauate in order to minimize the number of scaffolds. Suggested value=5.")
				.create("random");
		opts.addOption(random);


		BasicParser bp = new BasicParser();
		try {
			
			CommandLine cl = bp.parse(opts, args);
			if(cl.hasOption("h") || !cl.hasOption("i")){
				printHelp(opts);
			}
			else if (cl.hasOption("i")) {
				scaffolderHS(cl);
			}
		} catch (UnrecognizedOptionException uoe) {
			printHelp(opts);
		}
	}
	
	private void printHelp(Options opts) {
		System.out.println(String.format("Medusa version %d.%d", VERSION_MAJOR, VERSION_MINOR));
		HelpFormatter f1 = new HelpFormatter();
		f1.printHelp("java -jar medusa.jar -i inputfile -v", "available options: ", opts, "");
	
	}




	
	/*public static Double computeN50(MyGraph cover) {
		int sum = 0;
		ArrayList<Integer> a = new ArrayList<Integer>();
		 ArrayList<String> multicontigscaffold = cover.subPaths();
		String current;
		for (int i = 0; i <= multicontigscaffold.size()-1; i++) {
			current = multicontigscaffold.get(i);
			String[] currentSplit = current.split("@");
			int le = Integer.parseInt(currentSplit[1].replaceFirst("@", ""));
			a.add(le);
			sum=sum+le;
		}
		//System.out.println("Total lenght of muticontig Scaffolds="+ sum);
		for(MyNode s : cover.getNodes()){//aggiunge i singoletti
			if(s.getDegree()==0){
				a.add(s.getContiglength());
				sum=sum+s.getContiglength();
			}
		}

		
		Collections.sort(a);
		//Collections.reverse(a);		
		
		//System.out.println("Total sum of lenghts="+ sum);
		int index = 0;
		int partialSum = a.get(0);
		double n50;
		while (partialSum < (sum / 2)) {
			index++;
			partialSum = partialSum + a.get(index);
		}
		n50 = a.get(index);
		// il minore per
		// cui sommando tutti i
		// maggiori uguali si
		// copre meta luneghezza
		
		// Double n50= median(a);
		// ArrayList<Integer> a = new ArrayList<Integer>();//cosi e' la mediana
		// della distribuzione delle lunghezze
		// for(int i: lenghtsMap.keySet()){
		// int v = lenghtsMap.get(i);
		// for(int m=0;m<=v;m++){
		// a.add(i);
		// }
		// }
		// Collections.sort(a);
		// Double n50= median(a);
		return n50;
	}	public static double median(ArrayList<Integer> m) {
		int middle = m.size() / 2;
		if (m.size() % 2 == 1) {
			return m.get(middle);
		} else {
			return (m.get(middle - 1) + m.get(middle) / 2.0);
		}
	}*/

	private int computeLenght(ArrayList<String> paths) {
		int l = 0;
		String current;
		for (int i = 0; i < paths.size(); i++) {
			current = paths.get(i);
			String[] currentSplit = current.split("@");
			int le = Integer.parseInt(currentSplit[1].replaceFirst("@", ""));
			l = l + le;
		}
		return l;
	}


	private void scaffolderHS(CommandLine cl)
			throws Exception {

		String input = cl.getOptionValues("i")[0];
		System.out.println("INPUT FILE:" + input);
		int rounds = 1;
		if (cl.getOptionValue("random") != null) {
			rounds = Integer.parseInt(cl.getOptionValue("random"));
			System.out.println("Random rounds:" + rounds);
		}
		String scaffoldsfilename = input+"Scaffold.fasta";
		if (cl.getOptionValue("o") != null) {
			scaffoldsfilename = cl.getOptionValue("o");
		}
		String draftsFolder = "drafts";
		if (cl.getOptionValue("f") != null) {
			draftsFolder = cl.getOptionValue("f");
		}
		String medusaScripts ="medusa_scripts";
		if (cl.getOptionValue("scriptPath") != null) {
			medusaScripts = cl.getOptionValue("scriptPath");
		}
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
		String line;
		  System.out.print("Running MUMmer..."); Process process = new
		  ProcessBuilder(medusaScripts+"/mmrBatch.sh", draftsFolder, input).start();
		  BufferedReader errors = new BufferedReader(new InputStreamReader(
				  process.getErrorStream()));
		  if(cl.hasOption("v")){
					  while ((line = errors.readLine()) != null) {
						  System.out.println(line); } if (process.waitFor() != 0) { throw new
						  RuntimeException("Error running MUMmer."); }  
		  } else{
			  while ((line = errors.readLine()) != null) {
				  }
			  if (process.waitFor() != 0) { throw new
				  RuntimeException("Error running MUMmer."); }
		  }
		  
		  System.out.print("done.\n");
		  
		  System.out.println("------------------------------------------------------------------------------------------------------------------------");
		  System.out.print("Building the network...");
		  String current = new java.io.File( "." ).getCanonicalPath();
		  if(cl.hasOption("w2")){//new weight scheme
			  
			  process = new ProcessBuilder("python", medusaScripts+"/netcon_mummer.py", "-f"+current,
					  "-i"+input, "-onetwork", "-w") .start(); 
					  errors = new BufferedReader(new
					  InputStreamReader( process.getErrorStream())); while ((line =
					  errors.readLine()) != null) { System.out.println(line); } if
					  (process.waitFor() != 0) { throw new
					  RuntimeException("Error: Network construction failed."); }
		  }else{//old weight scheme
			  process = new ProcessBuilder("python", medusaScripts+"/netcon_mummer.py", "-f"+current,
					  "-i"+input, "-onetwork") .start(); 
					  errors = new BufferedReader(new
					  InputStreamReader( process.getErrorStream())); while ((line =
					  errors.readLine()) != null) { System.out.println(line); } if
					  (process.waitFor() != 0) { throw new
					  RuntimeException("Error: Network construction failed."); }
		  }
		 
		
		MyGraph grafo = GexfReader.read("network");
		 //cancella i file coords and delta e il file network
		 File network = new File("network");
		 network.delete();
		File dir = new File(".");
		for (String address : dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".coords");
			}
		})) {
			File f = new File(address);
			f.delete();
		}
		for (String address : dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".delta");
			}
		})) {
			File f = new File(address);
			f.delete();
		}
		
		if(grafo.getEdges().size()==0){
			System.out.println("SORRY: No information found. Are you sure to have MUMmer packedge location in your PATH? If yes, the chosen drafts genomes don't provide sufficient information for scaffolding the target genome.");
			return;
		}
		System.out.print("done.\n");
		System.out.println("------------------------------------------------------------------------------------------------------------------------");

		System.out.print("Cleaning the network...");

		double factor = 1;
		for(MyEdge e : grafo.getEdges()){
			factor=factor+e.getWeight();
		}
		MyGraph grafoProvvisorio = new MyGraph(grafo);
		MyGraph cover = greedyCover(grafoProvvisorio, factor);

		
		cover.cleanOrinetation();
		if (rounds > 1) {
			System.out.println("Candidate cover size: "+ cover.getEdges().size());
			for (int i = 2; i <= rounds; i++) {
				 grafoProvvisorio = new MyGraph(grafo);
				MyGraph candidateCover = greedyCoverRandom(grafoProvvisorio, factor);
				candidateCover.cleanOrinetation();
				System.out.println("Candidate cover size: "+ candidateCover.getEdges().size());
				if (candidateCover.getEdges().size() > cover.getEdges().size()) {
					cover = candidateCover;
				}
			}
			System.out.println("Best cover size: " + cover.getEdges().size());
		}
		System.out.print("done.\n");
		System.out.println("------------------------------------------------------------------------------------------------------------------------");
	
		// ------ create SCAFFOLDS file ---------
		LinkedHashMap<String, ProteinSequence> a = FastaReaderHelper.readFastaProteinSequence(new File(input));
		HashMap<String, String> sequences = new HashMap<String, String>();
		for(ProteinSequence s : a.values()){
			sequences.put(s.getOriginalHeader().split(" ")[0], s.getSequenceAsString());
		}
		ArrayList<String> scaffolds = cover.readScaffoldsSeq(input, sequences);
		File outputFile2 = new File(scaffoldsfilename);
		PrintWriter writerOutput2 = new PrintWriter(new FileWriter(outputFile2));
		int j = 1;
		for (String s : scaffolds) {
			writerOutput2.println(">Scaffold_" + j);
			writerOutput2.println(s);
			j++;
		}
		
		for (MyNode n: cover.getNodes()){
			if(n.getDegree()==0){
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
		
		
		
		int finalSingletons=(cover.getNodes().size()-cover.notSingletons());
		ArrayList<String> paths = cover.subPaths();
		int totalLength = computeLenght(paths);

		int numberOfScaffolds = paths.size() + finalSingletons;
		
		System.out.println("Number of scaffolds: " + numberOfScaffolds + " (singletons = " + finalSingletons + ", multi-contig scaffold = " + paths.size()+") \nfrom " +cover.getNodes().size() +" initial fragments.");
		System.out.println("Total length of the jointed fragments: " + totalLength);
		//N50 script:
		System.out.print("N50: "); process = new ProcessBuilder("python", medusaScripts+"/N50.py", scaffoldsfilename) .start(); 
		  errors = new BufferedReader(new InputStreamReader( process.getInputStream())); while ((line =
		  errors.readLine()) != null) { System.out.println(line); } if
		  (process.waitFor() != 0) { throw new
		  RuntimeException("Error: Network construction failed."); }
		
		writerOutput.println("Number of scaffolds: " + numberOfScaffolds + " (singletons = " + finalSingletons + ", multi-contig scaffold = " + paths.size()+") \nfrom " +cover.getNodes().size() +" initial fragments.");
		writerOutput.println("Total length of the jointed fragments: " + totalLength);
		writerOutput.flush();
		System.out.println("Summary File saved: " + outputFile);
		
		
		//-------------OPTIONAL OUTPUTS------------------//
		
		if(cl.hasOption("gexf")){
				System.out.println("------------------------------------------------------------------------------------------------------------------------");
			GexfWriter.write(grafo, input+"_network.gexf");
			GexfWriter.write(cover, input+"_cover.gexf");
		}
		 if(cl.hasOption("d")){
				System.out.println("------------------------------------------------------------------------------------------------------------------------");
			MyGraph.writeDistanceFile(cover, input+"_distanceTable");
		}
	}
	private MyGraph greedyCoverRandom(MyGraph network, double factor) {
		ArrayList<MyEdge> candidateEdges = new ArrayList<MyEdge>(network.getEdges());
		for(MyEdge edge : candidateEdges){
			double w = edge.getWeight()+factor;
			edge.setWeight(w);
			
		}
		ArrayList<MyEdge> chosenEdges = new ArrayList<MyEdge>();
		MyGraph cover = new MyGraph(network.getNodes(),chosenEdges);
		for(MyNode n : cover.getNodes()){
			n.setAdj(new ArrayList<MyNode>());
		}
		HashMap<MyNode, MyNode> twins = new HashMap<MyNode, MyNode>();
		for(MyNode n : network.getNodes()){
			twins.put(n, n);
		}
		IdComparator comparatorId = new IdComparator();
		weightComparator comparatorW = new weightComparator();
		Collections.sort(candidateEdges, comparatorId);//prima li sorta per ID
		Collections.shuffle(candidateEdges);
		Collections.sort(candidateEdges,comparatorW);//poi li sorta per peso
		Collections.reverse(candidateEdges);
		while(!candidateEdges.isEmpty()){
		MyEdge candidate = candidateEdges.get(0);
		MyNode source = candidate.getSource();
		MyNode target = candidate.getTarget();
		if(twins.get(source)==target){
			candidateEdges.remove(candidate);
		}else {// se non loro stessi gli estremi di un cammino
			cover.addEdge(candidate);
			MyNode ps = twins.get(source);
			MyNode pt = twins.get(target);
			twins.put(ps, pt);
			twins.put(pt, ps);
			if(cover.nodeFromId(target.getId()).getDegree()>1){
				ArrayList<MyEdge> a = network.inoutEdges(target);
				candidateEdges.removeAll(a);
			}else{
				candidateEdges.remove(candidate);	
			}
			if(cover.nodeFromId(source.getId()).getDegree()>1){
				ArrayList<MyEdge> b = network.inoutEdges(source);
				candidateEdges.removeAll(b);
			}
			else{
				candidateEdges.remove(candidate);	
			}
		}
			
		}
	return cover;		
		
	}


	public static MyGraph greedyCover(MyGraph network, double factor){
		ArrayList<MyEdge> candidateEdges = new ArrayList<MyEdge>(network.getEdges());
		for(MyEdge edge : candidateEdges){
			double w = edge.getWeight()+factor;
			edge.setWeight(w);
			
		}
		ArrayList<MyEdge> chosenEdges = new ArrayList<MyEdge>();
		MyGraph cover = new MyGraph(network.getNodes(),chosenEdges);
		for(MyNode n : cover.getNodes()){
			n.setAdj(new ArrayList<MyNode>());
		}
		HashMap<MyNode, MyNode> twins = new HashMap<MyNode, MyNode>();
		for(MyNode n : network.getNodes()){
			twins.put(n, n);
		}
		 	IdComparator comparatorId = new IdComparator();
			Collections.sort(candidateEdges, comparatorId);//prima li sorta per ID
			weightComparator comparatorW = new weightComparator();
			Collections.sort(candidateEdges, comparatorW);//poi li sorta per peso
			Collections.reverse(candidateEdges);
			
			/////////
			for(int i=0;i < candidateEdges.size()-1;i++){
				if(candidateEdges.get(i).getWeight() < candidateEdges.get(i+1).getWeight()){
					System.out.println("ERROR: "+ candidateEdges.get(i).toStringVerbose() +" > "+ candidateEdges.get(i+1).toStringVerbose() );
				} else if(candidateEdges.get(i).getWeight() == candidateEdges.get(i+1).getWeight()){
			//		System.out.println("=");

				}else if(candidateEdges.get(i).getWeight() > candidateEdges.get(i+1).getWeight()){
			//		System.out.println("OK");

				}			
			}
			
			/////////
			
		while(!candidateEdges.isEmpty()){
		MyEdge candidate = candidateEdges.get(0);
		MyNode source = candidate.getSource();
		MyNode target = candidate.getTarget();
		if(twins.get(source)==target){
			candidateEdges.remove(candidate);
		}else {// se non loro stessi gli estremi di un cammino
			cover.addEdge(candidate);
			MyNode ps = twins.get(source);
			MyNode pt = twins.get(target);
			twins.put(ps, pt);
			twins.put(pt, ps);
			if(cover.nodeFromId(target.getId()).getDegree()>1){
				ArrayList<MyEdge> a = network.inoutEdges(target);
				candidateEdges.removeAll(a);
			}else{
				candidateEdges.remove(candidate);	
			}
			if(cover.nodeFromId(source.getId()).getDegree()>1){
				ArrayList<MyEdge> b = network.inoutEdges(source);
				candidateEdges.removeAll(b);
			}
			else{
				candidateEdges.remove(candidate);	
			}
		}
			
		}
	return cover;		
}
}
