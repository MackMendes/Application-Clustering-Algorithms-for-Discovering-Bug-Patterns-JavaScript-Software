
--// Quantidade de Commits por Reposit�rio (Todos - Hanam e Charles)

SELECT 
'$' + SUBSTRING(dtm.F4, 0, CHARINDEX('/commit', dtm.F4)) + '.git$' AS Url,
'$' + dtm.F2 + '$' AS 'Nome do Reposit�rio', 
COUNT(1) AS Commits
 FROM [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm
	INNER JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth
		ON dtm.F4 = dth.F4

GROUP BY '$' + SUBSTRING(dtm.F4, 0, CHARINDEX('/commit', dtm.F4)) + '.git$', dtm.F2

ORDER BY Commits DESC;



----------------------------------------------------------------------
---- // Quantidade de Commits por Reposit�rio (Hanam)

SELECT 
SUBSTRING(dth.F4, 0, CHARINDEX('/commit', dth.F4)) + '.git' AS Url,
dth.F2 AS 'Nome do Reposit�rio', 
COUNT(1) AS Commits,
SUM(
	CASE WHEN (dth2.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Defeito/Corre��o',
 
SUM(
	CASE WHEN (dth3.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Meger',


SUM(
	CASE WHEN (dth4.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Outras Mudan�as'
 
 FROM [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm
	INNER JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth
		ON dtm.F4 = dth.F4

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth2
		ON dth.F4 = dth2.F4
		AND dth2.F3 = 'BUG_FIX'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth3
		ON dth.F4 = dth3.F4
		AND dth3.F3 = 'MERGE'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth4
		ON dth.F4 = dth4.F4
		AND dth4.F3 = 'OTHER'

GROUP BY SUBSTRING(dth.F4, 0, CHARINDEX('/commit', dth.F4)) + '.git', dth.F2

ORDER BY Commits DESC;


----------------------------------------------------------------------
---- // Quantidade de Commits por Reposit�rio (Charles)

SELECT 
SUBSTRING(dtm.F4, 0, CHARINDEX('/commit', dtm.F4)) + '.git' AS Url,
dtm.F2 AS 'Nome do Reposit�rio', 
COUNT(1) AS Commits,
SUM(
	CASE WHEN (dtm2.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Defeito/Corre��o',
 
SUM(
	CASE WHEN (dtm3.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Merge',

SUM(
	CASE WHEN (dtm4.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Refatora��o',

SUM(
	CASE WHEN (dtm5.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Outras Mudan�as'
 
 FROM [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm
	INNER JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth
		ON dtm.F4 = dth.F4

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm2
		ON dtm.F4 = dtm2.F4
		AND dtm2.F3 = 'BUG_FIX'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm3
		ON dtm.F4 = dtm3.F4
		AND dtm3.F3 = 'MERGE'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm4
		ON dtm.F4 = dtm4.F4
		AND dtm4.F3 = 'REFACTORING' 

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm5
		ON dtm.F4 = dtm5.F4
		AND dtm5.F3 = 'OTHER'

GROUP BY SUBSTRING(dtm.F4, 0, CHARINDEX('/commit', dtm.F4)) + '.git', dtm.F2

ORDER BY Commits DESC;

------------------------------------------------------------
---- // Quantidade de Commits por Reposit�rio (Charles)




SELECT 'BugAID' AS 'M�todo', 
COUNT(1) AS Commits,
SUM(
	CASE WHEN (dth2.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Defeito/Corre��o',
 
SUM(
	CASE WHEN (dth3.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Merge',

SUM(
	CASE WHEN (dth4.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Refatora��o',

SUM(
	CASE WHEN (dth5.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Outras Mudan�as'
 
 FROM [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm
	INNER JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth
		ON dtm.F4 = dth.F4

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth2
		ON dth.F4 = dth2.F4
		AND dth2.F3 = 'BUG_FIX'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth3
		ON dth.F4 = dth3.F4
		AND dth3.F3 = 'MERGE'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth4
		ON dth.F4 = dth4.F4
		AND dth4.F3 = 'REFACTORING' 

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth5
		ON dth.F4 = dth5.F4
		AND dth5.F3 = 'OTHER'

UNION ALL 

SELECT 'BugAID++' AS 'M�todo', 
COUNT(1) AS Commits,
SUM(
	CASE WHEN (dtm2.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Defeito/Corre��o',
 
SUM(
	CASE WHEN (dtm3.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Merge',

SUM(
	CASE WHEN (dtm4.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Refatora��o',

SUM(
	CASE WHEN (dtm5.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Outras Mudan�as'
 
 FROM [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm
	INNER JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth
		ON dtm.F4 = dth.F4

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm2
		ON dtm.F4 = dtm2.F4
		AND dtm2.F3 = 'BUG_FIX'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm3
		ON dtm.F4 = dtm3.F4
		AND dtm3.F3 = 'MERGE'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm4
		ON dtm.F4 = dtm4.F4
		AND dtm4.F3 = 'REFACTORING' 

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm5
		ON dtm.F4 = dtm5.F4
		AND dtm5.F3 = 'OTHER'


---------------------------------------------

SELECT 'BugAID++' AS 'M�todo', 
COUNT(1) AS Commits,

SUM(
	CASE WHEN (dtm2.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Defeito/Corre��o',
 
SUM(
	CASE WHEN (dtm3.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Merge',

SUM(
	CASE WHEN (dtm4.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Refatora��o',

SUM(
	CASE WHEN (dtm5.F4 IS NULL) THEN 0 ELSE 1 END
) AS 'Commits Indicados Outras Mudan�as'
 
 FROM [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm
	INNER JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_hanam] AS dth
		ON dtm.F4 = dth.F4

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm2
		ON dtm.F4 = dtm2.F4
		AND dtm2.F3 = 'BUG_FIX'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm3
		ON dtm.F4 = dtm3.F4
		AND dtm3.F3 = 'MERGE'

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm4
		ON dtm.F4 = dtm4.F4
		AND dtm4.F3 = 'REFACTORING' 

	LEFT JOIN [DiscoveringBugPatternsJS].[dbo].[dataset_charles$] AS dtm5
		ON dtm.F4 = dtm5.F4
		AND dtm5.F3 = 'OTHER'


SELECT TOP 10 * FROM [dbo].[dataset_charles$]