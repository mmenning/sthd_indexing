# sthd_indexing

## High-Dimensional Spatio-Temporal Indexing

### Structure

- STPA working name is RTPTree
- Index source code under indices/src
- Evaluation program source code under evaluation/src
- Evaluation schemata under evalution/schemata
- Configuration xml-Files under evaluatio/evalXMLs
- Evaluation results under evaluation/results

### Evaluation 

Start evalution from directory evaluation with 

    java -cp "target/evaluation-1.0.jar:../indices/target/indices-1.0.jar:.:.." de.mmenning.db.index.evaluation.EvaluateIndex evalXMLs/memSkewedSetup.xml evalXMLs/sequential/base.xml results/sequential/memSkewed.dat

- First parameter: the general workload setup
- Second parameter: the index to be investigated
- Third paramter: the output file
