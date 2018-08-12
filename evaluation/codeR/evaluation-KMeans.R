
# ============================================================
# EVALUATION - K-means (Clássico)
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


# ===========
# Evaluation 
# ===========

rangeCenters <- seq(2, as.integer(ncol(db_hanam)/2), by=1)

dsResultComplet <- data.frame()

jaccard_Charles <- double()
jaccard_Hanam <- double()

rand_Charles <- double()
rand_Hanam <- double()


resultClustering_Charles <- list()
resultClustering_Hanam <- list()

nCountTest <- 10 # Quantidade de testes

n <- 1

# Resultados esperados 
resultExpected <- c(5,5,5,5,5,6,8,6,6,6,6,6,6,8,6,6,6,7,7,7,6,7,8,6,6,6)

for (ncount in 1:nCountTest) {

  for (iCenters in rangeCenters) {
    
    dsResultComplet[n,"ID"] <- n
    
    dsResultComplet[n,"Centers"] <- iCenters
    
    dsResultComplet[n,"NumbTest"] <- ncount
    
    # ===== 
    # Charles
    
    resultClustering_Charles <- kmeans(x = db_charles,  centers= iCenters)
    
    dsResultComplet[n,"TotalCluster_Charles"] <- max(resultClustering_Charles$cluster)
    
    dsResultComplet[n,"TotalSumSquares_Charles"] <- resultClustering_Charles$totss
    
    dsResultComplet[n,"TotalwithinClusterSumSquares_Charles"] <- resultClustering_Charles$tot.withinss
    
    dsResultComplet[n,"TotalBetweenClusterSumSquares_Charles"] <- resultClustering_Charles$betweenss
    
    dsResultComplet[n,"Iterations_Charles"] <- resultClustering_Charles$iter
    
    dsResultComplet[n,"FaultInAlgorithm_Charles"] <- resultClustering_Charles$ifault
    
    jaccard_Charles <- 
      cluster_similarity(resultClustering_Charles$cluster, resultExpected, similarity = "jaccard", method = "independence")
    
    dsResultComplet[n,"Jaccard_Charles"] <- jaccard_Charles
    
    rand_Charles <- 
      cluster_similarity(resultClustering_Charles$cluster, resultExpected, similarity = "rand", method = "independence")
    
    dsResultComplet[n,"Rand_Charles"] <- rand_Charles
    
    # ===== 
    # Hanam
    
    resultClustering_Hanam <- kmeans(x = db_hanam,  centers= iCenters)
    
    dsResultComplet[n,"TotalCluster_Hanam"] <- max(resultClustering_Hanam$cluster)
    
    dsResultComplet[n,"TotalSumSquares_Hanam"] <- resultClustering_Hanam$totss
    
    dsResultComplet[n,"TotalwithinClusterSumSquares_Hanam"] <- resultClustering_Hanam$tot.withinss
    
    dsResultComplet[n,"TotalBetweenClusterSumSquares_Hanam"] <- resultClustering_Hanam$betweenss
    
    dsResultComplet[n,"Iterations_Hanam"] <- resultClustering_Hanam$iter
    
    dsResultComplet[n,"FaultInAlgorithm_Hanam"] <- resultClustering_Hanam$ifault
    
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

write.csv(x = dsResultComplet, file="evaluation/evaluation-kmeans-charles-VS-Hanam.csv")



