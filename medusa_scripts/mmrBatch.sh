dir=$1
reference_genome=$2
outname=$3

inps=`ls $dir | grep -v $reference_genome`
for i in $inps; do
	if [ -z "$outname" ];
	then mmr=`bash medusa_scripts/mummerRunner.sh $reference_genome $dir/$i`;
	else mmr=`bash medusa_scripts/mummerRunner.sh $reference_genome $dir/$i $outname`;
	fi
	echo $mmr
	done
