from scaffold_score import *
from IPython import embed

def orientation_inversion(orientation):
	""" convert orientation string to the inverted one 
		i.e. '9517:-1_9693:1' to '9517:1_9693:-1' """
	l=orientation.split('&')[0].split('_')
	n1,v1,n2,v2=[i for j in l for i in j.split(':')]
	v1,v2=int(v1)*-1,int(v2)*-1
	l=['%s:%s' %(n1,v1),'%s:%s' %(n2,v2)]
	out='_'.join(l)
	return out


def DNA_inversions(edges,G,G2):
	inverted=[]
	for e in edges:
		n1,n2=e
		ori=orientation_inversion(G[n1][n2]['orientation'])
		if ori == G2[n1][n2]['orientation'] or convert_orientation_string(ori) == G2[n1][n2]['orientation']: inverted.append(e)
	return inverted


if __name__ == '__main__':
	import sys
	usage=''' python scaffold_score.py order_file mapping_file '''
	args=sys.argv
	try: input1,input2=args[1],args[2]
	except:
		print usage
		sys.exit()
	print 'loading mapping file',input2	
	G2=parse_mapping(input2)
	scaffolds=[l.strip().split() for l in open(input1)]
	#for scaffold in open(input1): scaffold_scoring(scaffold)
	#G2=parse_mapping('../burkholderiaMapping')
	#scaffolds=[l.strip().split() for l in open('../../Temporanei/Burkholderia_J2315_target_ORDER')]
	G=nx.Graph()
	for s in scaffolds: AddEdgeFromScaffold(G,s)
	right_ones,wrong_ones=get_right_edges(G,G2)
	correctly_oriented,wrongly_oriented=[],[]
	total_joins,total_misplace,total_disori=0,0,0	
	for e in right_ones:
		n1,n2=e
		ori,reference=G[n1][n2]['orientation'],G2[n1][n2]['orientation']
		try: convert_orientation_string(ori)
		except: print ori
		if ori==reference:
			#print ori,reference
			correctly_oriented.append(e)
		elif convert_orientation_string(ori)==reference:
			#print convert_orientation_string(ori),reference
			correctly_oriented.append(e)
		else:
			#print ori,reference,'!!!!!'
			wrongly_oriented.append(e)
	inversion_events=DNA_inversions(wrongly_oriented,G,G2)
	print '%s total edges, %s wrong position, %s wrong orientation of which %s were inversion events' %(len(G.edges()),len(wrong_ones),len(wrongly_oriented),len(inversion_events))
	if len(G.edges()) != 0:
		print 'correct joints: %s (%s%%)' %(len(G.edges())-(len(wrongly_oriented)+len(wrong_ones)), (len(G.edges())-(len(wrongly_oriented)+len(wrong_ones)))/float(len(G.edges()))*100)
	else:
		print 'correct joints: 0 (0%)'
	write_edges(wrong_ones,input1+'_wrong_edges')
	write_edges(wrongly_oriented,input1+'_disoriented_edges')
	write_edges(correctly_oriented,input1+'_right_edges')
