Medusa
======

A draft genome scaffolder that uses multiple reference genomes in a
graph-based approach.

Availability and dependencies
-----------------------------

The present document provides a short guide for using the stand-alone
version of the software Medusa. This software is not yet been published.
The web interface, a stand-alone version and the present manual are
available at <http://combo.dbe.unifi.it/medusa>. The source code is
accessible at <https://github.com/combogenomics/medusa>.

Before try our software assure you to have installed the following
packages:

1.  MUMmer: this software is available at
    <http://mummer.sourceforge.net/>. **Warning: after installation
    remember to add MUMmer relative path to the system variable PATH!**

2.  Python (from 2.6) and BioPython (from 1.61)

3.  Java (from 1.6).

Now you are ready for obtaining and use our tool. The archive Medusa.tar.gz
contains the following files:

1.  A runnable .jar file Medusa.jar This is the program you will run.

2.  A sub-folder with auxiliary scripts. Leave it in the same folder of
    the .jar

3.  A sub-folder with a dataset you can use to test the tool.

Input and Output
----------------

The following inputs are required:

-   The *targetGenome* file: a draft genome in fasta format. This is the
    genome you are interesting in scaffolding.

-   An arbitrary long list of *auxiliaryDraft* files: other draft
    genomes in fasta format. The closest these organisms are related to
    the target, the better the results will be. This files are expected
    to be collected in a specific directory. It is possible to specify
    the path to the directory, see the command “-f” in the next section.

The following output files will be produced.

-   targetGenome: a textual file containing information about
    your data. Number of scaffolds, N50 value etc..

-   targetGenome.Scaffold.fasta: a fasta file with the sequences grouped
    in scaffolds. Between two contigs in the same scaffolds 100 N are
    inserted to fill the gap.
    
The following output files can optionally be produced.

-   targetGenome_distanceTable: a tabular file with the estimation of the
	distance between successive contigs (bp).
	

-   targetGenome_network.gexf: the contig network in gexf format.

-   targetGenome_cover.gexf: the final path cover in gexf format.


Usage
-----

The project folder must contains:

-   the *targetGenome* in fasta format.

-   the medusa.jar file

-   the scripts sub-folder “medusa-scripts”.

-   the comparison genomes sub-folder “drafts”. (In alternative you can
    specify another path for this folder.)

You can now run the java application with the following parameters:

1.  The option *-i* is required and indicates the name of the target
    genome file.

2.  The option *-o* is optional and indicates the name of output fasta
    file.

3.  The option *-v* (recommended) print on console the information given
    by the package MUMmer. This option is strongly suggested to
    understand if MUMmer is not running properly.

4.  The option *-f* is optional and indicates the path to the comparison
    drafts folder.

5.  The option *-random* is available (not required). This option allows
    the user to run a given number of cleaning rounds and keep the best
    solution. Since the variability is small In practice 5 rounds are
    usually sufficient to find the best score.

6.  The option *-w2* is optional and allows for a sequence similarity
    based weighting scheme. Using a different weighting scheme may lead
    to better results.

7.  The option *-d* outputs a file indicating the estimated distances
    between pair of contigs (<contig A> <contig B> <estimated distance>).
	
8. The *-gexf* is optional. With this option the gexf format of the contig network and
	the path cover are porvided.

9.  Finally the *-h* option provides a small recap of the previous ones.

An Example
----------

When *medusa* archive is unzipped the files are the followings:

-   the medusa.jar file.

-   the scripts sub-folder “medusa-scripts”.

-   the comparison genomes sub-folder “drafts”. Empty

-   a folder “datasets”.

We provide you a dataset as an example. Move the target file in the main
folder and all the comparison genomes in the “drafts” folder. Now you
can run the program with the following command line:

    java -jar medusa.jar -i targetgenome -v

Running the example
-------------------

    java -jar medusa.jar -f datasets -i datasets/Burkholderia_target -v

Compile
-------

The project can be compiled by calling ant in the top-level directory:

    ant
