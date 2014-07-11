#!/usr/bin/python

"""
MeDuSa: A draft genome scaffolder that uses multiple 
reference genomes in a graph-based approach
"""

#############
# Imports   #
#############

from medusa_lib import *
from graph_lib import *    
import logging,sys,os
from optparse import OptionParser,OptionGroup

##########
# Logger #
##########

logger = logging.getLogger('medusa')

#################
# Opt parsing   #
#################

usage=""" %prog [options]
"""

parser = OptionParser(usage=usage)

# mandatory
group1 = OptionGroup(parser, "Mandatory Arguments")
group1.add_option("-i", "--input", dest="target",
                  help="target genome to be scaffolded", metavar="FILE")
group1.add_option("-f", "--files", dest="comparison_dir",
                  help="DIR where the comparison genomes are stored", metavar="DIR")
parser.add_option_group(group1)

# optional
group2 = OptionGroup(parser, "Optional Arguments")
group2.add_option("-o", "--output", dest="output",
                  help="write scaffold to FILE", metavar="FILE")
group2.add_option("-v", "--verbose", dest="verbosity", action="store_true",default=False,
                  help="print to STDOUT the information given by MUMmer")
group2.add_option("-r", "--random", dest="random", action="store_true",default=False,
                  help="allow for random choice when best edges have the same score")
parser.add_option_group(group2)

(options, args) = parser.parse_args()


if not options.target or not options.comparison_dir:
    parser.print_help()
    parser.error('Mandatory Arguments missing')


########
# Main #
########

target_,comparison_dir_=options.target,options.comparison_dir

## rename/filer the genomes

# go target
target=target_+'_renamed.fasta'
storeTmpFasta('tmp/',target_,
                    new_name=target,tag='target_contig',threshold=1000,conv_table=target_+'.ctable')

# go comparison genomes
n,genome_ctable=0,open('tmp/genomes_ctable')
for g in os.listdir(comparison_dir_):
    file_=comparison_dir_ + g
    n+=1
    storeTmpFasta('tmp/',file_,
                    new_name='comparison_%s.fasta' %n,tag='comparison_%s_contig' %n,threshold=1000,conv_table=file_+'.ctable')
    genome_ctable.write('%s\t%s\n' %(g,'comparison_%s.fasta' %n))
genome_ctable.close()

## do MUMmer for each pair

os.chdir('tmp/')
comparisons=[f for f in os.listdir('./') if f.startswith('comparison')]
for c in comparisons: runMummer(target,c)

## from MUMmer to network

coords=[f for f in os.listdir('./') if f.endswith('.coords')]
Scaffolding_graph=initialize_graph(target)
coords2graph(coords,Scaffolding_graph)

# from network 2 scaffold

# TODO

# export results

# TODO

############
#  TODOS   #
############

# (really) add logger 
# net2scaffold
# exports 
# ???
