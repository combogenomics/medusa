usage="bash pipe_testing_benchmark.sh scaffold contigs mappingFile"
scaffolds=$1
contigs=$2
mappingfile=$3

if [[ ! -f $scaffolds ]]; then
    echo "Scaffolds file not found!"
    echo $usage
    exit
fi

if [[ ! -f $contigs ]]; then
    echo "Contigs file not found!"
    echo $usage
    exit
fi

if [[ ! -f $mappingfile ]]; then
    echo "Mapping file not found!"
    echo $usage
    exit
fi

# the script maps the contigs to the scaffold to produce a so-called string scaffold
# then it compares the string scaffold to a mapping file, produced by contiguator, 
# that will be used as a reference scaffold

echo $contigs $scaffolds 
python bout2order_.py $contigs $scaffolds
echo finally computing the score
#python score_final.py scaffold_string_ $mappingfile > pipe_testing_report
python scaffold_score_2.py string_scaffold $mappingfile > pipe_testing_report
cat pipe_testing_report
