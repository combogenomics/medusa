import os,sys
#from IPython import embed

#######################

if __name__=='__main__':
	usage="""
	python mummer_parser.py coord_file out_file """
	args=sys.argv
	coords=args[1]
	out=args[2]

#######################

def parse(coords_file):
	""" parse a .coord file from MUMmer """
	lines=[l.strip() for l in open(coords_file)]
	hits=[mummer_hit(l) for l in lines[5:]]
	return hits

def get_bestHits(hits):
	""" given all hits, return the best hit for each query """
	query_contigs=set([h.query for h in hits])
	best_hits=[]
	for c in query_contigs:
		best_hit=max([h for h in hits if h.query == c],key=lambda x: float(x.covq))
		best_hits.append(best_hit)
	return best_hits

def get_Clusters(best_hits):
	""" from a list of (best) hits, collect groups of hits mapping
		on a same reference as a dictionary """
	clusters={}
	for h in best_hits:
		clusters[h.reference]=clusters.get(h.reference,[])
		clusters[h.reference].append(h)	
	return clusters

def write_Clusters(clusters,out):
	""" write the clusters of best hits to out"""
	out=open(out,'w')
	for cl in clusters.values():
		cl.sort(key=lambda x:int(x.rstart))
		# print '\n'.join([c.query for c in cl]),'\n'
	out.write('\n'.join([c.query for c in cl])+'\n\n')
	return

def parse_mummer(coords):
	""" parse a coord file from MUMmer and return clusters of best hits
		as a dictionary """
	hits=parse(coords)
	best_hits=get_bestHits(hits)
	clusters=get_Clusters(best_hits)
	return clusters

def do_overlap(a,b):
	""" return true if a and b map on overlapping regions """
	sol=	((max(a) > min(b)) and (max(a) < max(b))) or \
	((min(a) > min(b)) and (min(a) < max(b)))
	return sol

#######################


class mummer_hit(object):
	#
	def __init__(self,line):
		self.qstart,self.qend,self.rstart,self.rend,self.len1,self.len2,self.percidy,\
		self.lenr,self.lenq,self.covq,self.covr,self.query,self.reference=[i for l in line.split(' | ') for i in l.split()]
		self.name=self.query
		if int(self.rstart)>int(self.rend): self.orientation=-1
		else: self.orientation=1
	#
	def distance_from(self,hit):
		a1,a2,b1,b2=int(self.rstart),int(self.rend),int(hit.rstart),int(hit.rend)
		if do_overlap([a1,a2],[b1,b2]): return 0
		distance=abs(min(a1-b1,a1-b2,a2-b1,a2-b2))
		return distance

#######################

if __name__=='__main__':
	clusters=parse_mummer(coords)
#embed()
