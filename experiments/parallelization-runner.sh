source ./config.sh

index=0
count=${#experiments[@]}
while [ "$index" -lt "$count" ]; do
  echo -e "Starting experiment: ${experiments[$index]}"
  cd ${directories[$index]}

  for j in "${testTypes[@]}"; do
    instrumentFiles ${experimentsCP[$index]}

    echo 'Generating sootTestOutput'
  	java -cp ${sootCP[$index]} edu.washington.cs.dt.main.ImpactMain -inputTests ${experiments[$index]}-$j-order

    echo 'Running prioritization for original order'
    java -Xms1g -Xmx2g -cp ${experimentsCP[$index]} edu.washington.cs.dt.impact.Main.OneConfigurationRunner -technique prioritization -coverage statement -order original -origOrder ${experiments[$index]}-$j-order -testInputDir sootTestOutput -filesToDelete ${experiments[$index]}-env-files -project ${experiments[$index]} -testType $j -outputDir ../${paraDir} -timesToRun ${medianTimes} -getCoverage

    runParallelizationOneConfigurationRunner ${experiments[$index]} ${experimentsCP[$index]} $j
    clearTemp ${experiments[$index]} $j
  done

  cd ..
  let "index++"
done
