
# ============================================================
# EVALUATION - C-means Fazzy
# Reference: http://www.sthda.com/english/articles/30-advanced-clustering/103-cmeans-r-function-compute-fuzzy-clustering/
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

if(!require(e1071)) install.packages("e1071")
library(e1071)

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

dists <- c("euclidean", "manhattan")

# Resultados esperados 
resultExpected <- c(1,1,1,2,2,2,3,3,3,4,4,4,5,0,5,5,6,6,6,7,7,8,0,0,0)


for (ncount in 1:nCountTest) {
  
  for (jDist in dists) {
    
    for (iCenters in rangeCenters) {
      
      dsResultComplet[n,"ID"] <- n
      
      dsResultComplet[n,"Centers"] <- iCenters
      
      dsResultComplet[n,"Distance"] <- jDist
      
      dsResultComplet[n,"NumbTest"] <- ncount
      
      # ===== 
      # Charles
      
      resultClustering_Charles <- e1071::cmeans(x = db_charles,  centers= iCenters, 
                                                iter.max = 100,  method = "cmeans", dist = jDist)
      
      dsResultComplet[n,"TotalCluster_Charles"] <- max(resultClustering_Charles$cluster)
      
      dsResultComplet[n,"Iterations_Charles"] <- resultClustering_Charles$iter
      
      dsResultComplet[n,"ValueObjectiveFunction_Charles"] <- resultClustering_Charles$withinerror
      
      jaccard_Charles <- 
        cluster_similarity(resultClustering_Charles$cluster, resultExpected, similarity = "jaccard", method = "independence")
      
      dsResultComplet[n,"Jaccard_Charles"] <- jaccard_Charles
      
      rand_Charles <- 
        cluster_similarity(resultClustering_Charles$cluster, resultExpected, similarity = "rand", method = "independence")
      
      dsResultComplet[n,"Rand_Charles"] <- rand_Charles
      
      adjustedRand_Charles <- 
        adjustedRandIndex(resultClustering_Charles$cluster, resultExpected)
      
      dsResultComplet[n,"AdjustedRand_Charles"] <- adjustedRand_Charles
      
      if(ncol(db_hanam) >= iCenters)
      {
        # ===== 
        # Hanam
        
        resultClustering_Hanam <- e1071::cmeans(x = db_hanam,  centers= iCenters, 
                                                  iter.max = 100,  method = "cmeans", dist = jDist)
        
        dsResultComplet[n,"TotalCluster_Hanam"] <- max(resultClustering_Hanam$cluster)
        
        dsResultComplet[n,"Iterations_Hanam"] <- resultClustering_Hanam$iter
        
        dsResultComplet[n,"ValueObjectiveFunction_Hanam"] <- resultClustering_Hanam$withinerror
        
        
        jaccard_Hanam <-
          cluster_similarity(resultClustering_Hanam$cluster, resultExpected, similarity = "jaccard", method = "independence")
        
        dsResultComplet[n,"Jaccard_Hanam"] <- jaccard_Hanam
        
        rand_Hanam <-
          cluster_similarity(resultClustering_Hanam$cluster, resultExpected, similarity = "rand", method = "independence")
        
        dsResultComplet[n,"Rand_Hanam"] <- rand_Hanam
        
        adjustedRand_Hanam <- 
          adjustedRandIndex(resultClustering_Hanam$cluster, resultExpected)
        
        dsResultComplet[n,"AdjustedRand_Hanam"] <- adjustedRand_Hanam
        
      }
      
      dsResultComplet[n,"Has_Result_Diff"] <- !(jaccard_Charles == jaccard_Hanam && rand_Charles == rand_Hanam)
      
      n <- n + 1;
      
    }
  
  }
}
  
write.csv(x = dsResultComplet, file="evaluation/evaluation-cmeansFazzy-charles-VS-Hanam-PosDeposito_v3.csv")
