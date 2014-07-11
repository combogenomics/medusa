
#######################

def initialize_graph(genome):
	""" initialize a Graph (networkx object), as a set of singleton nodes
		with same ids as the contigs of the targt draft genome """
	import networkx as nx
	from Bio.SeqIO import parse
	G=nx.Graph()
	contigs=[r for r in parse(genome,'fasta')]
	for c in contigs:
		id_,length=c.id,len(c.seq)
		G.add_node(id_,length=length)
	return G

def update_edges(G,Edge):
	""" update (or instanciate) the attributes of an edge E of the graph G """
	u,v,distance,orientation=Edge.name1,Edge.name2,Edge.distance,Edge.orientation
	w=G.get_edge_data(u,v,{'weight':0})['weight'] + 1
	d=G.get_edge_data(u,v,{'distance':0})['distance'] + distance
	o=G.get_edge_data(u,v,{'orientation':[]})['orientation']
	o.append(orientation)
	G.add_edge(u,v,weight=w,distance=d,orientation=o)
	return

def sort_(clusters):
	""" sort clusters of edges according to their mapping
		position on the reference genome """
	edges=[]
	for cl in clusters.values():
		if len(cl) == 1: continue
		cl.sort(key=lambda x:int(x.rstart))
		for i in range(len(cl)-1): edges.append((cl[i],cl[i+1]))
	return edges

def adjust_orientations(G):
	""" prepare edges of G for next phase (scaffolding Graph analysis) """
	for e in G.edges():
		n1,n2=e
		G[n1][n2]['orientation']=convert_orientations(e,G[n1][n2]['orientation'])
		max_count=G[n1][n2]['orientation'].count(max(G[n1][n2]['orientation'],
		key=lambda x:G[n1][n2]['orientation'].count(x)))
		G[n1][n2]['orientation_max']=list({tuple(i) for i in G[n1][n2]['orientation'] if G[n1][n2]['orientation'].count(i)==max_count})
		G[n1][n2]['orientation_max']='__'.join(['_'.join(i) for i in G[n1][n2]['orientation_max']])
		l=G[n1][n2]['orientation']
		counts={'_'.join(i):l.count(i)/float(len(l)) for i in l}
		G[n1][n2]['orientation']='__'.join(['%s&%s' %(k,v) for k,v in counts.items()])
	return

def coords2graph(coords,G):
	""" populate graph G with edges using MUMmer mappings """
	for coord in coords:
		print('using input',coord)
		clusters=parse_mummer(mapping_dir + coord)
		edges=sort_(clusters)
		for e in edges: update_edges(G,Edge(*e))
	print('adjusting orientation')
	adjust_orientations(G)
	return

#######################

class Edge(object):
	""" contains key attributes of scaffolding Graph edges """
	def __init__(self,hit1,hit2):
		self.name1,self.name2=hit1.query,hit2.query
		self.distance=hit1.distance_from(hit2)
		self.orientation=format_orientation_string(hit1,hit2)

