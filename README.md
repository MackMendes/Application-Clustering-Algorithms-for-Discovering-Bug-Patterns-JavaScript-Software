# Experimentos modificados - Mestrado USP

Neste repositório, têm algumas modificações que foram realizadas durante o desenvolvimento da minha [Dissertação de mestrado na USP](https://goo.gl/1sENyu). 

## Resumo da dissertação

As aplicações desenvolvidas com a linguagem JavaScript, vêm aumentando a cada dia, não somente aquelas na web (*client-side*), como também as aplicações executadas no servidor (*server-side*) e em dispositivos móveis (*mobile*). Neste contexto, a existência de ferramentas para identificação de defeitos é fundamental, para auxiliar desenvolvedores durante a evolução destas aplicações. Diferentes ferramentas e abordagens têm sido propostas ao longo dos anos, contudo apresentam limitações para evoluir com o tempo, a ponto de ficarem obsoletas rapidamente. O motivo disso é a utilização de uma lista fixa de defeitos predefinidos que são procurados no código. A ferramenta [BugAID](https://github.com/saltlab/BugAID) implementa uma estratégia semiautomática para descobrir padrões de defeitos através de agrupamentos das mudanças realizadas no decorrer do desenvolvimento do projeto. O objetivo deste trabalho é contribuir nessa ferramenta estendendo-a com melhorias na abordagem da extração de características, as quais são usadas pelos algoritmos de clusterização. O  módulo estendido do módulo de extração do BugAID (BE) encarregado da extração de características é chamado de BugAIDExtract+ (BE+). Além disso, neste trabalho é realizada uma avaliação de vários algoritmos de clusterização na descoberta dos padrões de defeitos em software JavaScript. Os resultados mostram que os algoritmos DBScan e Optics com BE+ apresentaram os melhores resultados para os índices Rand, Jaccard e Rand Ajustado, enquanto o HDBScan com BE e com BE+ apresentou o pior resultado. 


---------

# Modified Experiments - Masters USP

In this repository, there are some modifications that were made during the development of my [Master's Dissertation at USP](https://goo.gl/1sENyu).   

## Abstract of the dissertation

Applications developed with JavaScript language are increasing every day, not only for client-side, but also for server-side and for mobile devices. In this context, the existence of tools to identify faults is fundamental in order to assist developers during the evolution of their applications. Different tools and approaches have been proposed over the years, however they have limitations to evolve over time, becoming obsolete quickly. The reason for this is the use of a fixed list of pre-defined faults that are searched in the code. The [BugAID](https://github.com/saltlab/BugAID) tool implements a semiautomatic strategy for discovering bug patterns by grouping the changes made during the project development. The objective of this work is to contribute to the BugAID tool, extending this tool with improvements in the extraction of  characteristics to be used by the clustering algorithm. The extended module of the BugAID  extraction module (BE) that extracts the characteristics is called BE+. Additionally, an evaluation of the clustering algorithms used for discovering fault patterns in JavaScript software is performed. The results show that the DBScan and Optics algorithms with BE+ presented the best results for the Rand, Jaccard and Adjusted Rand indexes, while HDBScan with BE and BE+ presented the worst result.


## Fonts: 
* [Discovering Bug Patterns in JavaScript](http://salt.ece.ubc.ca/software/bugaid/). Quinn Hanam, Fernando S de M Brito, and Ali Mesbah. 2016. In Proceedings of the 2016 24th ACM SIGSOFT International Symposium on Foundations of Software Engineering. ACM, 144–156.
* [K-Means](https://pdfs.semanticscholar.org/a718/b85520bea702533ca9a5954c33576fd162b0.pdf)
* [C-Means Fuzzy](https://pdfs.semanticscholar.org/b9f0/7a272c1a7ac981d8b88889bc100442f26db3.pdf)
* [DBScan](https://www.aaai.org/Papers/KDD/1996/KDD96-037.pdf)
* [Optics](http://www.dbs.ifi.lmu.de/Publikationen/Papers/OPTICS.pdf)
* [HDBScan](https://www.researchgate.net/publication/315508524_hdbscan_Hierarchical_density_based_clustering)
* [HDBScan Documention](https://media.readthedocs.org/pdf/hdbscan/latest/hdbscan.pdf)
* [Efficient Computation of Multiple Density-Based Clustering Hierarchies](https://arxiv.org/pdf/1709.04545.pdf)
