
# ============================================================
# EVALUATION - K-means (Clássico)
# ============================================================
# Realiza a clusterização das característas obtidas na Mineração de códigos no GitHub
# ===========

setwd("G:/Mestrado/Meus experimentos/BugAID-Modificado/evaluation/codeR")


# ===========
# Leitura de CSV (apenas com 31 comits)
dataset.charles <- read.csv(file="datasets/posdeposito/dataset_bugid_with_header_Charles_FINAL_Equals.csv", header=TRUE, sep=",")
dataset.hanam <- read.csv(file="datasets/posdeposito/dataset_bugid_with_header_Hanam_FINAL_Equals.csv", header=TRUE, sep=",")


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

if(!require(mclust)) install.packages("mclust")
library("mclust")

# ===========
# Evaluation 
# ===========

rangeCenters <- seq(2, ncol(db_charles), by=1)

dsResultComplet <- data.frame()

jaccard_Charles <- double()
jaccard_Hanam <- double()

rand_Charles <- double()
rand_Hanam <- double()

adjustedRand_Charles <- double()
adjustedRand_Hanam <- double()


resultClustering_Charles <- list()
resultClustering_Hanam <- list()

nCountTest <- 10 # Quantidade de testes

n <- 1

# Resultados esperados 
resultExpected <- c(1,1,1,2,2,2,3,3,3,4,4,4,5,0,5,5,6,6,6,7,7,8,0,0,0)

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
    
    adjustedRand_Charles <- 
      adjustedRandIndex(resultClustering_Charles$cluster, resultExpected)
    
    dsResultComplet[n,"AdjustedRand_Charles"] <- adjustedRand_Charles
    
    # ===== 
    # Hanam
    
    if(ncol(db_hanam) >= iCenters)
    {
    
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
      
      adjustedRand_Hanam <- 
        adjustedRandIndex(resultClustering_Hanam$cluster, resultExpected)
      
      dsResultComplet[n,"AdjustedRand_Hanam"] <- adjustedRand_Hanam
      
      dsResultComplet[n,"Has_Result_Diff"] <- !(jaccard_Charles == jaccard_Hanam && rand_Charles == rand_Hanam)
      
    }
    
    n <- n + 1;
  
  }
}

write.csv(x = dsResultComplet, file="evaluation/evaluation-kmeans-charles-VS-Hanam-PosDeposito_v3.csv")



