hdbscanClustering <- function(db, datasetBugId){
  
  library("dbscan")
  res.hdbs <- dbscan::hdbscan(x = db,  minPts = 5)
  
  # Obter os clusters
  datasetBugId$Cluster <- res.hdbs$cluster
  datasetBugId$Qtd = 1;
  
  #Realizar um agrupamento 
  library(data.table)
  dt <- data.table(datasetBugId)
  
  return(dt)
}