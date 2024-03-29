
# ============================================================
# EVALUATION - DBScan
# ============================================================
# Realiza a clusteriza��o das caracter�stas obtidas na Minera��o de c�digos no GitHub
# ===========

setwd("G:/Mestrado/Meus experimentos/BugAID-Modificado/evaluation/codeR")


# ===========
# Leitura de CSV

dataset.charles <- read.csv(file="datasets/posdeposito/dataset_bugid_with_header_Charles_FINAL.csv", header=TRUE, sep=",")
dataset.hanam <- read.csv(file="datasets/posdeposito/dataset_bugid_with_header_Hanam_FINAL.csv", header=TRUE, sep=",")

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


resultClustering_Charles <- list()
resultClustering_Hanam <- list()

n <- 1

# ====
# P�s Deposito
#  c(1,1,1,2,2,2,3,3,3,4,4,4,5,0,5,5,6,6,6,7,7,8,0,0,0)
# Resultados esperados - Charles
resultExpected_Charles <- c(1,1,1,2,2,2,3,3,3,4,4,4,5,0,5,5,6,6,6,7,7) #c(1,1,1,2,2,2,3,3,3,4,4,4,5,0,5,5,6,6,6,7,7,-,-,-,-)

# Resultados esperados - Hanam
resultExpected_Hanam <- c(2,2,2,3,3,3,5,0,5,5,6,6,6,7,7,8,0,0,0) #c(-,-,-,2,2,2,3,3,3,-,-,-,5,0,5,5,6,6,6,7,7,8,0,0,0)
# ====

for (iEps in rangeEpsilon) {
 
  for (jMinpts in rangeMinPts) {
    
    dsResultComplet[n,"ID"] <- n
    
    dsResultComplet[n,"Epsilon"] <- iEps
    
    dsResultComplet[n,"MinPts"] <- jMinpts
    
    # ===== 
    # Charles
    
    resultClustering_Charles <- dbscan::dbscan(db_charles,  eps = iEps, minPts = jMinpts)
    
    
    dsResultComplet[n,"TotalCluster_Charles"] <- max(resultClustering_Charles$cluster)
  
    dsResultComplet[n,"TotalOutliers_Charles"] <-
    length(resultClustering_Charles$cluster[resultClustering_Charles$cluster == 0])
    
    jaccard_Charles <- 
      cluster_similarity(resultClustering_Charles$cluster, resultExpected_Charles, similarity = "jaccard", method = "independence")
    
    dsResultComplet[n,"Jaccard_Charles"] <- jaccard_Charles
    
    rand_Charles <- 
      cluster_similarity(resultClustering_Charles$cluster, resultExpected_Charles, similarity = "rand", method = "independence")
    
    dsResultComplet[n,"Rand_Charles"] <- rand_Charles
    
    # ===== 
    # Hanam
    
    resultClustering_Hanam <- dbscan::dbscan(db_hanam,  eps = iEps, minPts = jMinpts)
    
    dsResultComplet[n,"TotalCluster_Hanam"] <- max(resultClustering_Hanam$cluster)
    
    dsResultComplet[n,"TotalOutliers_Hanam"] <-
      length(resultClustering_Hanam$cluster[resultClustering_Hanam$cluster == 0])
    
    jaccard_Hanam <-
      cluster_similarity(resultClustering_Hanam$cluster, resultExpected_Hanam, similarity = "jaccard", method = "independence")
    
    dsResultComplet[n,"Jaccard_Hanam"] <- jaccard_Hanam
    
    rand_Hanam <-
      cluster_similarity(resultClustering_Hanam$cluster, resultExpected_Hanam, similarity = "rand", method = "independence")
    
    dsResultComplet[n,"Rand_Hanam"] <- rand_Hanam
    
    dsResultComplet[n,"Has_Result_Diff"] <- !(jaccard_Charles == jaccard_Hanam && rand_Charles == rand_Hanam)
    
    n <- n + 1;
  }
  
}

write.csv(x = dsResultComplet, file="evaluation/evaluation-dbscan-charles-VS-Hanam-validar-PosDeposito_v1.csv")
# Resultados outliers
#write.csv(x = dsResultComplet, file="evaluation/evaluation-31commits_outliers-dbscan-charles-VS-Hanam.csv")

