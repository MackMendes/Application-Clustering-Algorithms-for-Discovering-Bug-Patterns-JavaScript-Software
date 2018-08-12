kMeansClustering <- function (db, datasetBugId){
  
  # Rodando o KMeans
  res.dbkm <- kmeans(x = db, centers=219)
  # Obter os clusters
  datasetBugId$Cluster <- res.dbkm$cluster
  datasetBugId$Qtd = 1;
  
  #Realizar um agrupamento 
  library(data.table)
  dt <- data.table(datasetBugId)
  
  return(dt)
}