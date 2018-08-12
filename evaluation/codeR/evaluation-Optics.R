
# ============================================================
# EVALUATION - Optics
# ============================================================
# Realiza a clusterização das característas obtidas na Mineração de códigos no GitHub
# ===========

setwd("G:/Mestrado/Meus experimentos/BugAID-Modificado/evaluation/codeR")


# ===========
# Leitura de CSV (apenas com 31 comits)
dataset.charles <- read.csv(file="datasets/dataset_bugid_31commits_charles.csv", header=TRUE, sep=",")

dataset.hanam <- read.csv(file="datasets/dataset_bugid_31commits_hanam.csv", header=TRUE, sep=",")


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

# Resultados esperados 
resultExpected <- c(5,5,5,5,5,6,8,6,6,6,6,6,6,8,6,6,6,7,7,7,6,7,8,6,6,6)


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
    
    dsResultComplet[n,"Has_Result_Diff"] <- !(jaccard_Charles == jaccard_Hanam && rand_Charles == rand_Hanam)
    
    n <- n + 1;
  }
  
}

write.csv(x = dsResultComplet, file="evaluation/evaluation-optics-charles-VS-Hanam.csv")



