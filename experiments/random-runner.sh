source ./config.sh

compileSource

index=0
count=${#experiments[@]}
ARRAY=()

while [ "$index" -lt "$count" ]; do
  echo -e "Starting experiment: ${experiments[$index]}"
  for k in "${testTypes[@]}"; do
    cd ${directories[$index]}
    java -cp ${experimentsCP[$index]} edu.washington.cs.dt.impact.Main.InstrumentationMain -inputDir bin

    echo 'Generating sootTestOutput on old subject'
    java -cp ${sootCP[$index]} edu.washington.cs.dt.main.ImpactMain -inputTests ${experiments[$index]}-$k-order
    cd ..

    java -Xms1g -Xmx2g -cp ${oldExperimentsCP[$index]} edu.washington.cs.dt.impact.Main.RandomizeRunner -technique prioritization -coverage statement -order original -resolveDependences -origOrder ${directories[$index]}/${experiments[$index]}-$k-order -testInputDir ${directories[$index]}/sootTestOutput -filesToDelete ${directories[$index]}/${experiments[$index]}-env-files -randomTimes $randomTimes -project ${experiments[$index]} -testType $k -outputDir ./

    clearSelectionTemp ${directories[$index]} ${newDirectories[$index]}
  done

  let "index++"
done
