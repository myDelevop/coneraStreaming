rem #Processing Aco2CInput\atmospheric_arffTrain_1.arff
rem #Starting with the labeled node set in  Aco2CInput\atmospheric_arffTrain_1_arffSample1.txt
rem #Testing on Aco2CInput\atmospheric_arffTest_1.arff


rem #To construct Dd matrix (OPTION 1)
java -cp conera.jar distance.DistanceMap atmospheric_arffTrain_1.arff atmospheric.ini

rem #To construct Ed matrix (OPTION 2)
rem java -Xmx19G -cp conera.jar distance.CollectiveDistanceMap atmospheric_arffTrain_1.arff atmospheric.ini atmospheric_distances.csv nh_settings.ini

rem #To construct D+Ed matrix (OPTION 3)
rem java -Xmx19G -cp conera.jar distance.DescriptiveAndCollectiveDistanceMap atmospheric_arffTrain_1.arff atmospheric.ini atmospheric_distances.csv nh_settings.ini


rem #To perform active learning without considering the weigthing mechanism enabled during the active example selection phase
rem java -cp conera.jar collective.Aco2Regressor -data atmospheric_arffTrain_1.arff -distance atmospheric_distances.csv -sample atmospheric_arffTrain_1_arffSample1.txt -configData atmospheric.ini -configLearning nh_settings.ini -configLearner STDDEV.ini

rem #To perform active learning with considering the weigthing mechanism enabled during the active example selection phase
java -cp conera.jar collective.Aco2Regressor -data atmospheric_arffTrain_1.arff -distance atmospheric_distances.csv -sample atmospheric_arffTrain_1_arffSample1.txt -configData atmospheric.ini -configLearning nh_settings.ini -configLearner STDDEV.ini -distanceweight

rem #To use regressione hypotheses learnend during the active learning process to predict testing nodes
rem #Output saved in Aco2COutput\atmospheric_arffTrain_1_arffSample1_txtSTDDEVR2
java -cp conera.jar evaluation.DescriptorPredictionTest atmospheric_arffTrain_1_arffSample1_txtSTDDEVR2 atmospheric.ini atmospheric_arffTest_1.arff STDDEVR2Sample1.csv




rem #To perform active learning with Random selection
rem java -cp conera.jar self.RandomSelfClassifier -data atmospheric_arffTrain_1.arff -distance atmospheric_distances.csv -sample atmospheric_arffTrain_1_arffSample1.txt -configData atmospheric.ini -configLearning nh_settings.ini -configLearner NONE.ini
rem #To use regressione hypotheses learnend during the active learning process to predict testing nodes
rem #Output saved in Aco2COutput\atmospheric_arffTrain_1_arffSample1_txtSTDDEVR2
rem java -cp conera.jar evaluation.DescriptorPredictionTest atmospheric_arffTrain_1_arffSample1_txtNONER atmospheric.ini atmospheric_arffTest_1.arff NONESample1.csv


pause