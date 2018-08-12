
# ============================================================
# EVALUATION - C-means Fazzy
# Reference: http://www.sthda.com/english/articles/30-advanced-clustering/103-cmeans-r-function-compute-fuzzy-clustering/
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

if(!require(e1071)) install.packages("e1071")
library(e1071)

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

dists <- c("euclidean", "manhattan")

# Resultados esperados 
resultExpected <- c(5,5,5,5,5,6,8,6,6,6,6,6,6,8,6,6,6,7,7,7,6,7,8,6,6,6)


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
      
      dsResultComplet[n,"Has_Result_Diff"] <- !(jaccard_Charles == jaccard_Hanam && rand_Charles == rand_Hanam)
      
      n <- n + 1;
      
    }
  
  }
}
  
write.csv(x = dsResultComplet, file="evaluation/evaluation-cmeansFazzy-charles-VS-Hanam.csv")



