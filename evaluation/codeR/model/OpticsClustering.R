opticsClustering <- function(db, datasetBugId){
  
  library("dbscan")
  res.op <- dbscan::optics(db,  eps = 0.30, minPts = 5)
  
  res.op <- extractDBSCAN(res.op, eps_cl = .2)
  
  # Obter os clusters
  datasetBugId$Cluster <- res.op$cluster
  datasetBugId$Qtd = 1;
  
  #Realizar um agrupamento 
  library(data.table)
  dt <- data.table(datasetBugId)
  
  return(dt)
}