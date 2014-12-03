#!/usr/bin/python
import sys


def N50(numlist):
    numlist.sort()
    newlist = []
    for x in numlist :
        newlist += [x]*x
    # take the mean of the two middle elements if there are an even number
    # of elements.  otherwise, take the middle element
    if len(newlist) % 2 == 0:
        medianpos = len(newlist)/2  
        return float(newlist[medianpos] + newlist[medianpos-1]) /2
    else:
        medianpos = len(newlist)/2
        return newlist[medianpos]

if __name__=="__main__":
	from sys import argv
	from Bio.SeqIO import parse
	usage="""
	python N50.py FASTA [GAP LENGTH]

	Compute the N50 of a Scaffold. By default, Ns are kept.
	Optionally, stretches of Ns with given length (usually gaps)
	can be removed

	Examples:
	python N50.py burkho.scaffold
	python N50.py burkho.scaffold 100
	"""
	
	inp=argv[1]
	try: gap_length=argv[2]
	except: gap_length=0
	lengths = [len(str(i.seq).replace('N'*gap_length,'')) for i in parse(inp,'fasta')]
	print int(N50(lengths))
