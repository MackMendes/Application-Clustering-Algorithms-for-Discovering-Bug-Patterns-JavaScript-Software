
# ============================================================
# EVALUATION - HDBScan
# ============================================================
# Realiza a clusterização das característas obtidas na Mineração de códigos no GitHub
# ===========

setwd("G:/Mestrado/Meus experimentos/BugAID-Modificado/evaluation/codeR")


# ===========
# Leitura de CSV (75 commits)
dataset.charles <- read.csv(file="datasets/posdeposito/dataset_bugid_with_header_Amostra75vsAll-Charles_Equals.csv", header=TRUE, sep=",")
dataset.hanam <- read.csv(file="datasets/posdeposito/dataset_bugid_with_header_Amostra75vsAll-Haman_Equals.csv", header=TRUE, sep=",")


# ===========
# Retirando os metadados do DataSet (deixar somente os BCTs)
# ===========
db_charles <- dataset.charles[11:ncol(dataset.charles)]

db_hanam <- dataset.hanam[11:ncol(dataset.hanam)]


# ===========
# Carregando as bibliotecas
# ===========
if(!require(clusteval)) install.packages("clusteval")
library("clusteval")

if(!require(dbscan)) install.packages("dbscan")
library("dbscan")

if(!require(mclust)) install.packages("mclust")
library("mclust")

# ===========
# Evaluation 
# ===========

rangeMinPts <- seq(2, 20, by=1)

dsResultComplet <- data.frame()

jaccard_Charles <- double()
jaccard_Hanam <- double()

rand_Charles <- double()
rand_Hanam <- double()

adjustedRand_Charles <- double()
adjustedRand_Hanam <- double()


resultClustering_Charles <- list()
resultClustering_Hanam <- list()

n <- 1

# Resultados esperados 
# resultExpected <- c(1,1,1,2,2,2,3,3,3,4,4,4,5,0,5,5,6,6,6,7,7,8,0,0,0)
resultExpected <- c(1,1,1,1,2,2,2,3,3,3,4,4,4,4,5,0,5,5,6,6,6,7,7,8,0,0,0,9,9,9,9,10,10,10,10,10,11,11,0,11,11,0,0,12,12,12,0,0,0,0,0,0,0,0,0,0,13,13,13,14,14,14,14,14,0,15,15,0,0,0,16,16,0,16,0) 

for (jMinpts in rangeMinPts) {
  
  dsResultComplet[n,"ID"] <- n
  
  dsResultComplet[n,"MinPts"] <- jMinpts
  
  # ===== 
  # Charles
  
  resultClustering_Charles <- dbscan::hdbscan(db_charles, minPts = jMinpts)
  
  dsResultComplet[n,"Method_HDBScan_Charles"] <- resultClustering_Charles$hc$method
  
  dsResultComplet[n,"Dist_Method_HDBScan_Charles"] <- resultClustering_Charles$hc$dist.method
  
  dsResultComplet[n,"TotalCluster_Charles"] <- max(resultClustering_Charles$cluster)
  
  dsResultComplet[n,"TotalOutliers_Charles"] <-
    length(resultClustering_Charles$cluster[resultClustering_Charles$cluster == 0])
  
  jaccard_Charles <- 
    cluster_similarity(resultClustering_Charles$cluster, resultExpected, similarity = "jaccard", method = "independence")
  
  dsResultComplet[n,"Jaccard_Charles"] <- jaccard_Charles
  
  rand_Charles <- 
    cluster_similarity(resultClustering_Charles$cluster, resultExpected, similarity = "rand", method = "independence")
  
  dsResultComplet[n,"Rand_Charles"] <- rand_Charles
  
  adjustedRand_Charles <- 
    adjustedRandIndex(resultClustering_Charles$cluster, resultExpected)
  
  dsResultComplet[n,"AdjustedRand_Charles"] <- adjustedRand_Charles
  
  # ===== 
  # Hanam
  
  resultClustering_Hanam <- dbscan::hdbscan(db_hanam, minPts = jMinpts)
  
  dsResultComplet[n,"TotalCluster_Hanam"] <- max(resultClustering_Hanam$cluster)
  
  dsResultComplet[n,"Method_HDBScan_Hanam"] <- resultClustering_Hanam$hc$method
  
  dsResultComplet[n,"Dist_Method_HDBScan_Hanam"] <- resultClustering_Hanam$hc$dist.method
  
  dsResultComplet[n,"TotalOutliers_Hanam"] <-
    length(resultClustering_Hanam$cluster[resultClustering_Hanam$cluster == 0])
  
  jaccard_Hanam <-
    cluster_similarity(resultClustering_Hanam$cluster, resultExpected, similarity = "jaccard", method = "independence")
  
  dsResultComplet[n,"Jaccard_Hanam"] <- jaccard_Hanam
  
  rand_Hanam <-
    cluster_similarity(resultClustering_Hanam$cluster, resultExpected, similarity = "rand", method = "independence")
  
  dsResultComplet[n,"Rand_Hanam"] <- rand_Hanam
  
  adjustedRand_Hanam <- 
    adjustedRandIndex(resultClustering_Hanam$cluster, resultExpected)
  
  dsResultComplet[n,"AdjustedRand_Hanam"] <- adjustedRand_Hanam
  
  
  dsResultComplet[n,"Has_Result_Diff"] <- !(jaccard_Charles == jaccard_Hanam && rand_Charles == rand_Hanam)
  
  n <- n + 1;
}

write.csv(x = dsResultComplet, file="evaluation/evaluation-Hdbscan-charles-VS-Hanam-PosDefesa.csv")

