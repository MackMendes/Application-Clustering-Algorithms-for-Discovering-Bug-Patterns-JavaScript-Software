
# ======
# Realiza a clusterização das característas obtidas na Mineração de códigos no GitHub
# ==

setwd("G:/Mestrado/Meus experimentos/BugAID-Modificado/evaluation/codeR")


# ====
# Leitura de CSV (apenas com 31 comits)
dataset.charles <- read.csv(file="datasets/dataset_bugid_31commits_charles.csv", header=TRUE, sep=",")

dataset.hanamActual <- read.csv(file="datasets/dataset_bugid_31commits_hanam.csv", header=TRUE, sep=",")


# ====
# Retirando os metadados do DataSet
# Meta
db_charles <- dataset.charles[11:ncol(dataset.charles)]

db_hanamActual <- dataset.hanamActual[11:ncol(dataset.hanamActual)]


# Carregando função para mostrar o meu resultado
source("util/ShowResult.R")


# ============================================================
# Rodando o DBScan 
# ============================================================
# Carregando o DBScan
source("model/DbScanClustering.R")

# ----- Charles 
dt_dbScan_charles <- dbScanClustering(db_charles, dataset.charles)

# Montando estrutura para comparação dos valores
result_dbScan_charles <- showResult(dt_dbScan_charles)

# Escrever resultado
write.csv(x = result_dbScan_charles, file="results/resultado-dbscan-charles.csv")


# ----- Hanam Actual 
dt_dbScan_hanam <- dbScanClustering(db_hanamActual, db_hanamActual)

# Montando estrutura para comparação dos valores
result_dbScan_hanam <- showResult(dt_dbScan_hanam)

# Escrever resultado
write.csv(x = result_dbScan_hanam, file="results/resultado-dbscan-hanam.csv")


# ============================================================
# Rodando o K-Means
# ============================================================
# Carregando o K-Means
source("model/KMeansClustering.R")


# ----- Charles
dt_kMeans_charles <- kMeansClustering(db_charles, dataset.charles)

result_kMeans_charles <- showResult(dt_kMeans_charles)

write.csv(x = result_kMeans_charles, file="results/resultado-kMeans-charles.csv")

# ----- Hanam Actual 
dt_kMeans_hanam <- kMeansClustering(db_hanam, dataset.hanamActual)

result_kMeans_hanam <- showResult(dt_kMeans_hanam)

write.csv(x = result_kMeans_hanam, file="results/resultado-kMeans-hanam.csv")



# ============================================================
# Rodando o Optics
# ============================================================
# Carregando o Optics
source("model/OpticsClustering.R")


# ----- Charles
dt_optics_charles <- opticsClustering(db_charles, dataset.charles)

result_optics_charles <- showResult(dt_optics_charles)

write.csv(x = result_optics_charles, file="results/resultado-optics-charles.csv")


# ----- Hanam Actual 
dt_optics_hanam <- opticsClustering(db_hanam, dataset.hanamActual)

result_optics_hanam <- showResult(dt_optics_hanam)

write.csv(x = result_optics_hanam, file="results/resultado-optics-hanam.csv")


# ============================================================
# Rodando o HDBScan 
# ============================================================
# Carregando o HDBScan
source("model/HDbScanClustering.R")


# ----- Charles 
dt_hdbScan_charles <- hdbscanClustering(db_charles, dataset.charles)

# Montando estrutura para comparação dos valores
result_hdbScan_charles <- showResult(dt_hdbScan_charles)

# Escrever resultado
write.csv(x = result_hdbScan_charles, file="results/resultado-Hdbscan-charles.csv")


# ----- Hanam Actual 
dt_hdbScan_hanam <- hdbscanClustering(db_hanamActual, db_hanamActual)

# Montando estrutura para comparação dos valores
result_hdbScan_hanam <- showResult(dt_hdbScan_hanam)

# Escrever resultado
write.csv(x = result_hdbScan_hanam, file="results/resultado-Hdbscan-hanam.csv")
