from mummer_parser import *
import sys,os
#from IPython import embed
import networkx as nx

######################################

if __name__=='__main__':
	usage='python netcon_mummer.py mapping_dir query_genome gexf_out'
	args=sys.argv
	mapping_dir=args[1]
	query_genome=args[2]
	out=args[3]
	if not mapping_dir.endswith('/'): mapping_dir+='/'
	
######################################

def sort_(clusters):
	edges=[]
	for cl in clusters.values():
		if len(cl) == 1: continue
		cl.sort(key=lambda x:int(x.rstart))
		for i in range(len(cl)-1):
			edges.append((cl[i],cl[i+1]))
	return edges

def update_edges(G,Edge):
	u,v,distance,orientation=Edge.name1,Edge.name2,Edge.distance,Edge.orientation
	w=G.get_edge_data(u,v,{'weight':0})['weight'] + 1
	d=G.get_edge_data(u,v,{'distance':0})['distance'] + distance
	o=G.get_edge_data(u,v,{'orientation':[]})['orientation']
	o.append(orientation)
	G.add_edge(u,v,weight=w,distance=d,orientation=o)

def initialize_graph(genome):
	import networkx as nx
	from Bio.SeqIO import parse
	G=nx.Graph()
	contigs=[r for r in parse(genome,'fasta')]
	for c in contigs:
		id_,length=c.id,len(c.seq)
		G.add_node(id_,length=length)
	return G

def adjust_orientations(G):
	for e in G.edges():
		n1,n2=e
		G[n1][n2]['orientation']=convert_orientations(e,G[n1][n2]['orientation'])
		max_count=G[n1][n2]['orientation'].count(max(G[n1][n2]['orientation'],
					     key=lambda x:G[n1][n2]['orientation'].count(x)))
		#embed()
		G[n1][n2]['orientation_max']=list({tuple(i) for i in G[n1][n2]['orientation'] if G[n1][n2]['orientation'].count(i)==max_count})
		G[n1][n2]['orientation_max']='__'.join(['_'.join(i) for i in G[n1][n2]['orientation_max']])
		l=G[n1][n2]['orientation']
		counts={'_'.join(i):l.count(i)/float(len(l)) for i in l}
		G[n1][n2]['orientation']='__'.join(['%s&%s' %(k,v) for k,v in counts.items()])
		#embed()
	
def format_orientation_string(hit1,hit2):
	orientations=['%s:%s' %(hit1.name,hit1.orientation), '%s:%s' %(hit2.name,hit2.orientation)]
	#orientations.sort()
	return orientations

def convert_orientations(e,ori_list):
	''' convert elements of a list i.e. a:1,b:-1 to b:1,a:-1 '''
	#embed()
	n1_,n2_=e
	ori_new=[]
	for l in ori_list:
		if l[0].split(':')[0] != n1_:
			#print 'inverting!',l[0].split(':')[0],n1_, len(ori_list) 
			n1,v1,n2,v2=[i for j in l for i in j.split(':')]
			v1,v2=int(v1)*-1,int(v2)*-1
			l_=['%s:%s' %(n2,v2),'%s:%s' %(n1,v1)]
		else: l_=l
		ori_new.append(l_)
	#embed()	
	return ori_new


######################################

class Edge(object):
	def __init__(self,hit1,hit2):
		self.name1,self.name2=hit1.query,hit2.query
		self.distance=hit1.distance_from(hit2)
		self.orientation=format_orientation_string(hit1,hit2)

######################################
if __name__ == '__main__':

	inputs=[f for f in os.listdir(mapping_dir) if f.endswith('.coords')]
	G=initialize_graph(query_genome)
	for coord in inputs:
		print('using input',coord)
		clusters=parse_mummer(mapping_dir + coord)
		edges=sort_(clusters)
		for e in edges: update_edges(G,Edge(*e))
	#embed()
	G3=G.copy()
	print('adjusting orientations')
	adjust_orientations(G)
	nx.write_gexf(G,out)
