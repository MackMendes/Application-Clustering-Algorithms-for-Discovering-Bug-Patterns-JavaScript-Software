
# ============================================================
# EVALUATION - Optics
# ============================================================
# Realiza a clusteriza��o das caracter�stas obtidas na Minera��o de c�digos no GitHub
# ===========

setwd("G:/Mestrado/Meus experimentos/BugAID-Modificado/evaluation/codeR")


# ===========
# Leitura de CSV (apenas com 31 comits)
#dataset.charles <- read.csv(file="datasets/posdeposito/dataset_bugid_with_header_Charles_FINAL_Equals.csv", header=TRUE, sep=",")
#dataset.hanam <- read.csv(file="datasets/posdeposito/dataset_bugid_with_header_Hanam_FINAL_Equals.csv", header=TRUE, sep=",")

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

rangeEpsilon <- seq(0.1, 6.3, by=0.2)
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

# ====
# P�s Defesa
# resultExpected <- c(1,1,1,1,2,2,2,3,3,3,4,4,4,4,5,0,5,5,6,6,6,7,7,8,0,0,0,9,9,9,9,10,10,10,10,10,11,11,0,11,11,0,0,12,12,12,0,0,0,0,0,0,0,0,0,0,13,13,13,14,14,14,14,14,0,15,15,0,0,0,16,16,0,16,0) 
resultExpected <- c(1,1,1,1,2,2,2,11,11,11,3,3,3,3,4,4,4,4,4,4,0,5,5,6,0,0,0,7,7,7,3,8,1,8,8,2,2,9,9,9,9,11,11,9,9,9,11,11,11,4,11,10,10,10,14,12,11,13,15,15,2,11,11,2,2,11,11,11,11,11,16,11,0,11,0)


for (iEps in rangeEpsilon) {
  
  for (jMinpts in rangeMinPts) {
    
    dsResultComplet[n,"ID"] <- n
    
    dsResultComplet[n,"Epsilon"] <- iEps
    
    dsResultComplet[n,"MinPts"] <- jMinpts
    
    dsResultComplet[n,"Epsilon_identify"] <- iEps
    
    # ===== 
    # Charles
    
    resultClustering_Charles <- dbscan::optics(db_charles,  eps = iEps, minPts = jMinpts)
    
    resultClustering_Charles <- extractDBSCAN(resultClustering_Charles, eps_cl = iEps)
    
    
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
    
    resultClustering_Hanam <- dbscan::optics(db_hanam,  eps = iEps, minPts = jMinpts)
    
    resultClustering_Hanam <- extractDBSCAN(resultClustering_Hanam, eps_cl = iEps)
    
    dsResultComplet[n,"TotalCluster_Hanam"] <- max(resultClustering_Hanam$cluster)
    
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
  
}

write.csv(x = dsResultComplet, file="evaluation/evaluation-optics-charles-VS-Hanam-PosDefesa.csv")




