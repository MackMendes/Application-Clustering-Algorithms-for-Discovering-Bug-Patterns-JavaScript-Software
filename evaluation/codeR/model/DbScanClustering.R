dbScanClustering <- function(db, datasetBugId){
  
  library("dbscan")
  res.db <- dbscan::dbscan(db,  eps = 0.30, minPts = 5)
  
  # Obter os clusters
  datasetBugId$Cluster <- res.db$cluster
  datasetBugId$Qtd = 1;
  
  #Realizar um agrupamento 
  library(data.table)
  dt <- data.table(datasetBugId)
  
  return(dt)
}