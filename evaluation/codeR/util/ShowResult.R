showResult <- function (dt){
  if(!require(dplyr)) install.packages("dplyr")
    
  library(dplyr)
  library(tidyr)
  
  # Montando estrutura para comparação dos valores
  metadados <- c("ID", "ProjectID", "CommitURL", "BuggyCommitID", "BugFixingCommit", 
                 "RepairedCommitID", "Class", "Method", "Cluster", "ModifiedStatements", 
                 "Qtd")
  
  colunas <- colnames(dt)
  
  #resultado_pilot <- tidyr::gather(dt, "nome_coluna", "valor_coluna", colunas[!colunas %in% metadados])
  resultado_pilot <- tidyr::gather(dt, "nome_coluna", "valor_coluna", c(12:ncol(dt)-1))
  
  # Obtendo apenas as linhas com item da Bag-of-Works
  resultado_pilot_job <- resultado_pilot[resultado_pilot$valor_coluna == 1,]
  
  # Obtendo apenas os itens  que estavam em algum Cluster
  resultado_pilot_job_final <- resultado_pilot_job[resultado_pilot_job$Cluster != 0, ]
  
  
  resultado_pilot_job_final <- resultado_pilot_job_final %>% 
    group_by(Cluster, nome_coluna) %>% 
    summarise(Commits = sum(Qtd))
  
  resultado_pilot_job_final <- resultado_pilot_job_final[order(-resultado_pilot_job_final$Commits),]
  
  
  r_aggregate <- aggregate(nome_coluna ~ Cluster, data = resultado_pilot_job_final, c)
  r_aggregate_commitsEClusters <- aggregate(Commits ~ Cluster, data = resultado_pilot_job_final, sum)
  
  
  resultado_final <- r_aggregate[order(-r_aggregate$Cluster),]
  r_aggregate_commitsEClusters <- r_aggregate_commitsEClusters[order(-r_aggregate_commitsEClusters$Cluster),]

  
  resultado_final$Commits <- r_aggregate_commitsEClusters$Commits
  
  if(nrow(resultado_final) == 1) {
    resultado_final$nome_coluna <- paste(resultado_final$nome_coluna, collapse=";")
  }
  else{
    resultado_final$nome_coluna <- as.character(resultado_final$nome_coluna)
    
    resultado_final$nome_coluna <- gsub("c(", "", resultado_final$nome_coluna, fixed = TRUE)
    resultado_final$nome_coluna <- gsub("\"", "", resultado_final$nome_coluna, fixed = TRUE)
    resultado_final$nome_coluna <- gsub(")", "", resultado_final$nome_coluna, fixed = TRUE)
    resultado_final$nome_coluna <- gsub(",", ";", resultado_final$nome_coluna, fixed = TRUE)
    
  }
  
  

  
  resultado_final <- resultado_final[order(-resultado_final$Commits),]
  
  return(resultado_final)
}