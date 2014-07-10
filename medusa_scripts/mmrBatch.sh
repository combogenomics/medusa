dir=$1
reference_genome=$2

inps=`ls $dir | grep -v $reference_genome`
for i in $inps; do
	mmr=`bash medusa_scripts/mummerRunner.sh $reference_genome $dir/$i`
	echo $mmr
	done
