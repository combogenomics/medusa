from IPython import embed

def run_cmd(cmd, ignore_error=False, verbose=False):
	'''
	Run a command line command
	Returns True or False based on the exit code
	'''
	import sys,subprocess
	sys.stderr.write('Running %s\n'%cmd)
	proc = subprocess.Popen(cmd,shell=(sys.platform!="win32"),
	stdin=subprocess.PIPE,stdout=subprocess.PIPE,
	stderr=subprocess.PIPE)
	out = proc.communicate()
	return_code = proc.returncode
	if verbose:
		sys.stderr.write('%s\n'%str(out[0].decode('utf-8')))
		sys.stderr.write('\n')
	if return_code != 0 and not ignore_error:
		sys.stderr.write('Command (%s) failed w/ error %d\n'
		%(cmd, return_code))
		sys.stderr.write('%s\n'%str(out[1].decode('utf-8')))
		sys.stderr.write('\n')
	return bool(not return_code)

class mummer_hit(object):
	def __init__(self,line):
		self.qstart,self.qend,self.rstart,self.rend,self.len1,self.len2,self.percidy,\
		self.lenr,self.lenq,self.covq,self.covr,self.query,self.reference=[i for l in line.split(' | ') for i in l.split()]
		self.name=self.query
		self.line=line
		if int(self.rstart)>int(self.rend): self.orientation=-1
		else: self.orientation=1
	def distance_from(self,hit):
		a1,a2,b1,b2=int(self.rstart),int(self.rend),int(hit.rstart),int(hit.rend)
		if do_overlap([a1,a2],[b1,b2]): return 0
		distance=abs(min(a1-b1,a1-b2,a2-b1,a2-b2))
		return distance
	def isExactMatch(self):
		return float(self.covq) == 100.0

def parse_mummer(coords):
	hits=parse(coords)
	exact_matches=get_exactMatches(hits)
	return exact_matches
	
def get_exactMatches(hits):
	exactMatches=[h for h in hits if h.isExactMatch()]
	return exactMatches

def parse(coords_file):
	lines=[l.strip() for l in open(coords_file)]
	hits=[mummer_hit(l) for l in lines[5:]]
	return hits

def getFileName(f1,f2):
	fname1,fname2=f1.split('/')[-1],f2.split('/')[-1]
	tag1,tag2=fname1.split('.')[0],fname2.split('.')[0]
	return "%s_%s.coords" %(tag1,tag2)

def getBestHits(file_):
	""" get best hits from a nucmer output
		BUGGED FXN! USE getBestHits_
	"""
	hits=parse(file_)
	em={}
	for h in hits:
		scaffold=h.reference
		contig=h.query
		em[scaffold]=em.get(scaffold,{})
		prev=em[scaffold].get(contig,None)
		if prev==None:
			em[scaffold][contig]=h
		else:
			if float(em[scaffold][contig].covq) < float(h.covq):
				em[scaffold][contig]=h
	for s in em:
		best_hits=em[s].values()
		best_hits.sort(key=lambda x: int(x.rstart))
		em[s]=best_hits
	return em
	
def getBestHits_(file_):
	""" get best hits from a nucmer output"""
	hits=parse(file_)
	bh={}
	for h in hits:
		contig=h.query
		prev=bh.get(contig,None)
		if prev==None:
			bh[contig]=h
		else:
			if float(bh[contig].covq) < float(h.covq):
				bh[contig]=h
	by_scaffolds={}
	for h in bh.values():
		scaffold=h.reference
		by_scaffolds[scaffold]=by_scaffolds.get(scaffold,[])
		by_scaffolds[scaffold].append(h)
	for s in by_scaffolds:
		best_hits=by_scaffolds[s]
		best_hits.sort(key=lambda x: int(x.rstart))
		by_scaffolds[s]=best_hits
	return by_scaffolds
	
if __name__ == "__main__":

	import sys,subprocess
	sys.path.append('../medusa_scripts/')
	usage=''' python bout2order_.py contigs Medusa_scaffold '''
	args=sys.argv
	try: f1,f2=args[1],args[2]
	except:
		print( usage )
		sys.exit()
	# run nucmer
	bashCommand='bash ../medusa_scripts/mummerRunner.sh %s %s' %(f1,f2)
	process = run_cmd(bashCommand)
	# parse that thing
	file_=getFileName(f1,f2)
	try:best_hits=getBestHits_(file_)
	except: embed()
	out=open('string_scaffold','w')
	for s in best_hits:
		s_=''
		for h in best_hits[s]: 
			h_="%s:%s " %(h.query,h.orientation)
			s_+=h_
		out.write(s_.strip()+'\n')
